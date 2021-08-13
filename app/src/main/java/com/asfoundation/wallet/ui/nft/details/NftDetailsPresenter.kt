package com.asfoundation.wallet.ui.nft.details

import io.reactivex.disposables.CompositeDisposable

class NftDetailsPresenter(
  private val view: NftDetailsView,
  private val disposables: CompositeDisposable,
  private val data: NftDetailsData
) {
  fun present() {
    view.setupUi(data.asset)
    handleOkClick()
    handleTransactClick()
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

  private fun handleTransactClick() {
    disposables.add(
      view.getTransactClick()
        .doOnNext {
          view.onTransferClicked()
        }.subscribe()
    )
  }
}