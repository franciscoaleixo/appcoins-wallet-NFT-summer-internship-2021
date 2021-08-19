package com.asfoundation.wallet.nfts.repository

import android.util.Log
import com.asfoundation.wallet.nfts.NftNetworkInfo
import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.nfts.repository.api.NftApi
import com.asfoundation.wallet.service.KeyStoreFileManager
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.spongycastle.util.encoders.Hex
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.TimeUnit


class NftRepository(
  private val nftApi: NftApi,
  private val keyStoreFileManager: KeyStoreFileManager,
  private val defaultNFTNetwork: NftNetworkInfo,
) {
  fun getNFTCount(address: String): Single<Int> {
    return nftApi.getNFTsOfWallet(address).map { response -> response.assets.size }
  }

  fun getNFTAssetList(address: String): Single<List<NftAsset>> {
    return nftApi.getNFTsOfWallet(address).map { response ->
      response.assets.map { assetResponse ->
        NftAsset(
          assetResponse.id,
          assetResponse.token_id,
          assetResponse.image_preview_url,
          assetResponse.name,
          assetResponse.description,
          assetResponse.asset_contract.address,
          assetResponse.asset_contract.schema_name,
          checkOwnership(
            address,
            assetResponse.asset_contract.address,
            BigDecimal(assetResponse.token_id),
            assetResponse.asset_contract.schema_name
          )
        )
      }
    }
  }

  fun checkOwnership(
    fromAddress: String,
    contractAddress: String,
    tokenId: BigDecimal,
    schema: String
  ): Boolean {

    val web3j = Web3jFactory.build(HttpService(defaultNFTNetwork.rpcServerUrl))
    if (schema == "ERC721") {
      val response = web3j.ethCall(
        Transaction.createEthCallTransaction(
          fromAddress,
          contractAddress,
          Hex.toHexString(createNftOwnerData(tokenId))
        ),
        DefaultBlockParameterName.LATEST
      ).send()
      return fromAddress == response.result.removeRange(IntRange(2, 25))
    } else {
      Log.d("NFT", schema)
    }

    return true
  }

  fun createNftOwnerData(tokenID: BigDecimal): ByteArray {
    val params: List<Type<*>> = listOf(Uint256(tokenID.toBigInteger()))
    val returnTypes: List<TypeReference<*>> = listOf(object : TypeReference<Bool?>() {})
    val function = Function("ownerOf", params, returnTypes)
    val encodedFunction = FunctionEncoder.encode(function)
    return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction))
  }

  fun createNftTransferData(
    from: String,
    to: String,
    tokenID: BigDecimal,
    functionName: String = "transferFrom"
  ): ByteArray {
    val params: List<Type<*>> = listOf(Address(from), Address(to), Uint256(tokenID.toBigInteger()))
    val returnTypes: List<TypeReference<*>> = listOf(object : TypeReference<Bool?>() {})
    val function = Function(functionName, params, returnTypes)
    val encodedFunction = FunctionEncoder.encode(function)
    return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction))
  }

  fun createAndSendTransactionSingle(
    fromAddress: String,
    signerPassword: String,
    toAddress: String,
    tokenID: BigDecimal,
    contractAddress: String
  ): Single<String> {
    return Single.just(
      createAndSendTransaction(
        fromAddress, signerPassword, toAddress,
        tokenID, contractAddress
      )
    )
  }

  fun getTransactionStatus(hash: String): Observable<Boolean> {
    val web3j = Web3jFactory.build(HttpService(defaultNFTNetwork.rpcServerUrl))
    return Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
      .timeInterval()
      .switchMap {
        if (web3j.ethGetTransactionReceipt(hash).send().transactionReceipt == null) {
          return@switchMap Observable.just(false)
        }
        return@switchMap Observable.just(true)
      }
      .filter { receipt -> receipt }
      .distinctUntilChanged { receipt -> receipt }
  }


  private fun createAndSendTransaction(
    fromAddress: String,
    signerPassword: String,
    toAddress: String,
    tokenID: BigDecimal,
    contractAddress: String
  ): String {
    val data = createNftTransferData(fromAddress, toAddress, tokenID)
    val web3j = Web3jFactory.build(HttpService(defaultNFTNetwork.rpcServerUrl))
    val ethGetTransactionCount = web3j.ethGetTransactionCount(
      fromAddress, DefaultBlockParameterName.LATEST
    ).sendAsync().get()

    val nonce = ethGetTransactionCount.transactionCount
    val gasPrice = web3j.ethGasPrice().send().gasPrice
    var gasLimit = BigDecimal(144000).toBigInteger()
    val calculateGasTransaction = Transaction(
      fromAddress,
      nonce,
      gasPrice,
      gasLimit,
      contractAddress,
      BigInteger.ZERO,
      Hex.toHexString(data)
    )
    val estimatedGas = web3j.ethEstimateGas(calculateGasTransaction).send()

    if (!estimatedGas.hasError()) {
      gasLimit = estimatedGas.amountUsed
    }

    Log.d("NFT", "GasPrice: $gasPrice   GasLimit: $gasLimit   Nonce: $nonce")

    val transaction: RawTransaction = RawTransaction.createTransaction(
      nonce,
      gasPrice,
      gasLimit,
      contractAddress,
      Hex.toHexString(data)
    )
    val signedTransaction: ByteArray = TransactionEncoder.signMessage(
      transaction,
      getCredentials(signerPassword, fromAddress)
    )
    val raw = web3j.ethSendRawTransaction(Numeric.toHexString(signedTransaction)).send()

    if (raw.transactionHash == null) {
      Log.d("NFT", raw.error.message)
      return raw.error.message
    } else {
      Log.d("NFT", raw.transactionHash)
      return raw.transactionHash
    }
  }

  fun getCredentials(signerPassword: String?, address: String): Credentials {
    return WalletUtils.loadCredentials(signerPassword, keyStoreFileManager.getKeystore(address))
  }
}