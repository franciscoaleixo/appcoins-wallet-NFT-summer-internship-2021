package com.asfoundation.wallet.nfts

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
              BigDecimal(100000000000000L),
              BigDecimal(100000L),
              to,
              BigDecimal(asset.token_id),
              asset.contract_address
            )
          }
      }.subscribeOn(Schedulers.io())
  }

/*
    private fun getStoredNFTCount(walletAddress: String?): Single<Integer> {
        return (walletAddress?.let { Single.just(it) } ?: accountWalletService.find()
            .map { it.address })
            .subscribeOn(networkScheduler)
            .flatMap { nftRepository.getStoredNFTCount(it) }
    }

 */
}