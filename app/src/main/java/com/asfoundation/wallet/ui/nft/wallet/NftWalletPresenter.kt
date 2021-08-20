package com.asfoundation.wallet.ui.nft.wallet

import com.asfoundation.wallet.nfts.NftInteractor
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

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
    recurrentUpdateNftList()
  }

  fun dismiss() {
    disposables.dispose()
  }

  private fun requestNftList() {
    disposables.add(nftInteractor.getNFTAsset()
      .subscribeOn(networkScheduler)
      .observeOn(viewScheduler)
      .doOnSuccess { view.updateNftList(it) }
      .subscribe({}, { it.printStackTrace() })
    )
  }

  private fun recurrentUpdateNftList() {
    disposables.add(
      Observable.interval(20, TimeUnit.SECONDS)
        .flatMapSingle { nftInteractor.getNFTAsset() }
        .subscribeOn(networkScheduler)
        .observeOn(viewScheduler)
        .doOnNext { view.updateNftList(it) }
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