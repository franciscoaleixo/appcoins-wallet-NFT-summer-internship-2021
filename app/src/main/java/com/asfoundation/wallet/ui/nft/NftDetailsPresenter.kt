package com.asfoundation.wallet.ui.nft

import io.reactivex.disposables.CompositeDisposable

class NftDetailsPresenter(
  private val view: NftDetailsView,
  private val disposables: CompositeDisposable
) {
  fun present() {
    view.setupUi()
    handleOkClick()
  }

  fun stop() {
    disposables.dispose()
  }

  private fun handleOkClick() {
    disposables.add(
      view.getOkClick()
        .doOnNext { view.close() }
        .subscribe()
    )
  }
}