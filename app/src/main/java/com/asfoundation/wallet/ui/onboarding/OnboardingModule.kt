package com.asfoundation.wallet.ui.onboarding

import android.content.Context
import com.appcoins.wallet.bdsbilling.WalletService
import com.appcoins.wallet.bdsbilling.repository.BdsRepository
import com.appcoins.wallet.gamification.Gamification
import com.asfoundation.wallet.logging.Logger
import com.asfoundation.wallet.repository.PreferencesRepositoryType
import com.asfoundation.wallet.router.TransactionsRouter
import com.asfoundation.wallet.support.SupportInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject

@InstallIn(ActivityComponent::class)
@Module
class OnboardingModule {

  @Provides
  fun providesOnboardingPresenter(activity: OnboardingActivity, interactor: OnboardingInteractor,
                                  logger: Logger,
                                  navigator: OnboardingNavigator): OnboardingPresenter {
    return OnboardingPresenter(CompositeDisposable(), activity as OnboardingView, interactor,
        AndroidSchedulers.mainThread(), Schedulers.io(), ReplaySubject.create(), logger, navigator)
  }

  @Provides
  fun providesOnboardingInteractor(walletService: WalletService,
                                   preferencesRepositoryType: PreferencesRepositoryType,
                                   supportInteractor: SupportInteractor, gamification: Gamification,
                                   bdsRepository: BdsRepository): OnboardingInteractor {
    return OnboardingInteractor(walletService, preferencesRepositoryType, supportInteractor,
        gamification, bdsRepository)
  }

  @Provides
  fun providesOnboardingNavigator(activity: OnboardingActivity,
                                  transactionsRouter: TransactionsRouter): OnboardingNavigator {
    return OnboardingNavigator(activity, transactionsRouter)
  }

  @Provides
  fun providesOnboardingActivity(@ActivityContext context: Context): OnboardingActivity {
    return context as OnboardingActivity
  }
}