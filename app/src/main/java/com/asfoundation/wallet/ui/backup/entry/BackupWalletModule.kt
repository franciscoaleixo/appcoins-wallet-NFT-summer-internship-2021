package com.asfoundation.wallet.ui.backup.entry

import androidx.fragment.app.Fragment
import com.asfoundation.wallet.ui.backup.entry.BackupWalletFragment.Companion.PARAM_WALLET_ADDR
import com.asfoundation.wallet.ui.balance.BalanceInteractor
import com.asfoundation.wallet.util.CurrencyFormatUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

@InstallIn(FragmentComponent::class)
@Module
class BackupWalletModule {

  @Provides
  fun providesBackupWalletPresenter(fragment: BackupWalletFragment,
                                    balanceInteractor: BalanceInteractor,
                                    currencyFormatUtils: CurrencyFormatUtils,
                                    data: BackupWalletData,
                                    navigator: BackupWalletNavigator): BackupWalletPresenter {
    return BackupWalletPresenter(balanceInteractor, fragment as BackupWalletFragmentView, data,
        navigator, currencyFormatUtils, CompositeDisposable(), Schedulers.io(),
        AndroidSchedulers.mainThread())
  }

  @Provides
  fun providesBackupWalletData(fragment: BackupWalletFragment): BackupWalletData {
    fragment.arguments!!.apply {
      return BackupWalletData(getString(PARAM_WALLET_ADDR)!!)
    }
  }

  @Provides
  fun providesBackupNavigator(fragment: BackupWalletFragment): BackupWalletNavigator {
    return BackupWalletNavigator(fragment.requireFragmentManager())
  }

  @Provides
  fun providesBackupWalletFragment(fragment: Fragment): BackupWalletFragment {
    return fragment as BackupWalletFragment
  }
}