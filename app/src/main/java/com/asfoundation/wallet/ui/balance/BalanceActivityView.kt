package com.asfoundation.wallet.ui.balance

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.asfoundation.wallet.nfts.domain.NftAsset
import io.reactivex.Observable

interface BalanceActivityView {

  fun showBalanceScreen()

  fun showTokenDetailsScreen(
    tokenDetailsId: TokenDetailsActivity.TokenDetailsId,
    imgView: ImageView, textView: TextView, parentView: View
  )

  fun showNftDetailsScreen(
    imgView: ImageView,
    textView: TextView,
    parentView: View,
    asset: NftAsset
  )

  fun setupToolbar()

  fun navigateToWalletDetailView(walletAddress: String, isActive: Boolean)

  fun shouldExpandBottomSheet(): Boolean

  fun enableBack()

  fun disableBack()

  fun backPressed(): Observable<Any>

  fun navigateToTransactions()

  fun navigateToRemoveWalletView(walletAddress: String, totalFiatBalance: String,
                                 appcoinsBalance: String, creditsBalance: String,
                                 ethereumBalance: String)

  fun navigateToBackupView(walletAddress: String)

  fun navigateToRestoreView()

  fun showCreatingAnimation()

  fun showWalletCreatedAnimation()
}
