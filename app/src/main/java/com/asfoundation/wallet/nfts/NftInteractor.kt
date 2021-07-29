package com.asfoundation.wallet.nfts

import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.nfts.repository.NftRepository
import com.asfoundation.wallet.service.AccountWalletService
import io.reactivex.Scheduler
import io.reactivex.Single

class NftInteractor(
  private val accountWalletService: AccountWalletService,
  private val nftRepository: NftRepository,
  private val networkScheduler: Scheduler
) {
  fun getNFTCount(): Single<Int> {
    return accountWalletService.find()
      .flatMap { nftRepository.getNFTCount(it.address) }
  }

  fun getNFTAsset(): Single<List<NftAsset>> {
    return accountWalletService.find()
      .flatMap { nftRepository.getNFTAssetList(it.address) }
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