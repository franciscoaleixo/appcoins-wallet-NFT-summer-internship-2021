package com.asfoundation.wallet.ui.nft

import com.asfoundation.wallet.nfts.NftInteractor
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class NftWalletPresenter(
  private val view: NftWalletFragment,
  private val nftInteractor: NftInteractor,
  private val networkScheduler: Scheduler,
  private val viewScheduler: Scheduler,
  private val disposables: CompositeDisposable,
) {

  fun present() {
    view.setupUI()
    requestNftList()
    requestNftData()
  }

  private fun requestNftList() {
    disposables.add(nftInteractor.getNFTAsset()
      .subscribeOn(networkScheduler)
      .observeOn(viewScheduler)
      .doOnSuccess { view.updateNftList(it) }
      .subscribe({}, { it.printStackTrace() })
    )
  }

  private fun requestNftData() {
    disposables.add(nftInteractor.getNFTCount()
      .subscribeOn(networkScheduler)
      .observeOn(viewScheduler)
      .doOnSuccess { view.updateNftData(it.toString()) }
      .subscribe({}, { it.printStackTrace() })
    )
  }

}