package com.asfoundation.wallet.ui.nft.transfer

import com.asfoundation.wallet.entity.Address
import com.asfoundation.wallet.nfts.NftInteractor
import com.asfoundation.wallet.ui.nft.details.NftDetailsData
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_nft_transfer.*

class NftWalletTransferPresenter(
  private val view: NftWalletTransferView,
  private val disposables: CompositeDisposable,
  private val nftInteractor: NftInteractor,
  private val data: NftDetailsData,
  private val viewScheduler: Scheduler,
) {
  fun present() {
    view.setupUI(data)
    handleTransactionClick()
  }

  fun stop() {
    disposables.dispose()
  }

  private fun handleTransactionClick() {
    disposables.add(view.getTransferClick()
      .observeOn(viewScheduler)
      .doOnNext { view.showLoading() }
      .map { walletAddress ->
        if (!Address.isAddress(walletAddress)) {
          view.showInvalidAddressError()
          return@map ""
        }
        return@map walletAddress
      }.filter { walletAddress ->
        walletAddress.isNotEmpty()
      }.flatMap { walletAddress ->
        nftInteractor.sendNFT(walletAddress, data.asset)
          .observeOn(AndroidSchedulers.mainThread())
          .doOnNext { status ->
            if (status) {
              view.showFeedback("Transaction Done")
            }
          }
      }.subscribe({}, { it.printStackTrace() })
    )
  }
}