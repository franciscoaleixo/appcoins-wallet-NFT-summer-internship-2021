package com.asfoundation.wallet.nfts.repository

import android.util.Log
import com.asfoundation.wallet.nfts.NftNetworkInfo
import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.nfts.repository.api.NftApi
import com.asfoundation.wallet.service.KeyStoreFileManager
import com.asfoundation.wallet.ui.iab.raiden.MultiWalletNonceObtainer
import io.reactivex.Single
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
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger

class NftRepository(
  private val nftApi: NftApi,
  private val keyStoreFileManager: KeyStoreFileManager,
  private val defaultNFTNetwork: NftNetworkInfo,
  private val nonceObtainer: MultiWalletNonceObtainer,
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
        )
      }
    }
  }

  fun createNftTransferData(from: String, to: String, tokenID: BigDecimal): ByteArray {
    val params: List<Type<*>> = listOf(Address(from), Address(to), Uint256(tokenID.toBigInteger()))
    val returnTypes: List<TypeReference<*>> = listOf(object : TypeReference<Bool?>() {})
    val function = Function("transferFrom", params, returnTypes)
    val encodedFunction = FunctionEncoder.encode(function)
    return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction))
  }

  fun createNftApproveData(owner: String, approved: String, tokenID: BigDecimal): ByteArray {
    val params: List<Type<*>> =
      listOf(Address(owner), Address(approved), Uint256(tokenID.toBigInteger()))
    val returnTypes: List<TypeReference<*>> = listOf(object : TypeReference<Bool?>() {})
    val function = Function("approve", params, returnTypes)
    val encodedFunction = FunctionEncoder.encode(function)
    return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction))
  }

  fun createAndSendTransactionSingle(
    fromAddress: String,
    signerPassword: String,
    gasPrice: BigDecimal,
    gasLimit: BigDecimal,
    toAddress: String,
    tokenID: BigDecimal,
    contractAddress: String
  ): Single<String> {
    return Single.just(
      createAndSendTransaction(
        fromAddress, signerPassword, gasPrice, gasLimit, toAddress,
        tokenID, contractAddress
      )
    )
  }

  fun createAndSendTransaction(
    fromAddress: String,
    signerPassword: String,
    gasPrice: BigDecimal,
    gasLimit: BigDecimal,
    toAddress: String,
    tokenID: BigDecimal,
    contractAddress: String
  ): String {
    val data = createNftTransferData(fromAddress, toAddress, tokenID)
    val web3j = Web3jFactory.build(HttpService(defaultNFTNetwork.rpcServerUrl))
    val nonce: BigInteger = nonceObtainer.getNonce(Address(fromAddress), 4)
/*
    val transaction = Transaction.createFunctionCallTransaction(fromAddress, nonce,gasPrice.toBigInteger() , gasLimit.toBigInteger() ,toAddress ,Hex.toHexString(data))
    val raw = web3j.ethSendTransaction(transaction).sendAsync().get();
    */
    val transaction: RawTransaction = RawTransaction.createTransaction(
      nonce,
      gasPrice.toBigInteger(),
      gasLimit.toBigInteger(),
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