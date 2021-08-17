package com.asfoundation.wallet.nfts

import android.util.Log
import com.asfoundation.wallet.interact.FetchGasSettingsInteract
import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.nfts.repository.NftRepository
import com.asfoundation.wallet.repository.PasswordStore
import com.asfoundation.wallet.service.AccountWalletService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal

class NftInteractor(
  private val accountWalletService: AccountWalletService,
  private val nftRepository: NftRepository,
  private val passwordStore: PasswordStore,
  private val fetchGasSettingsInteract: FetchGasSettingsInteract
) {
  fun getNFTCount(): Single<Int> {
    return accountWalletService.find()
      .flatMap { nftRepository.getNFTCount(it.address) }
  }

  fun getNFTAsset(): Single<List<NftAsset>> {
    return accountWalletService.find()
      .flatMap { nftRepository.getNFTAssetList(it.address) }
  }

  fun sendNFT(to: String, asset: NftAsset): Single<String> {
    return accountWalletService.find()
      .flatMap { wallet ->
        passwordStore.getPassword(wallet.address)
          .flatMap { password ->
            nftRepository.createAndSendTransactionSingle(
              wallet.address,
              password,
              to,
              BigDecimal(asset.token_id),
              asset.contract_address
            )
          }
      }.doOnError { err ->
        Log.d("NFT", err.message)
      }.subscribeOn(Schedulers.io())


  }
}