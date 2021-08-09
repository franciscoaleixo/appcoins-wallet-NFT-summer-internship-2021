package com.asfoundation.wallet.ui.nft.details

import android.util.Log
import com.asfoundation.wallet.nfts.NftInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class NftDetailsPresenter(
  private val view: NftDetailsView,
  private val disposables: CompositeDisposable,
  private val nftInteractor: NftInteractor,
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
        //.doOnNext{view.showLoading}
        .flatMapSingle {
          nftInteractor.sendNFT(
            "0x62317826Eb67852DbFB4770a5fF9218ccA7B712D",
            data.asset
          )
        }.observeOn(AndroidSchedulers.mainThread())
        //.doOnNext{MOSTRAR FEEDBACK}
        .doOnError { err ->
          Log.d("NFT", err.message)
        }.subscribe({}, { it.printStackTrace() })
    )
  }
}