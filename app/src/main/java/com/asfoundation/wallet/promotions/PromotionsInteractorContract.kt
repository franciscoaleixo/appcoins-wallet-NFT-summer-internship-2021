package com.asfoundation.wallet.promotions

import android.content.Intent
import com.appcoins.wallet.gamification.GamificationScreen
import com.asfoundation.wallet.referrals.CardNotification
import com.asfoundation.wallet.referrals.ReferralsScreen
import io.reactivex.Completable
import io.reactivex.Single

interface PromotionsInteractorContract {

  fun retrievePromotions(): Single<PromotionsModel>

  fun hasAnyPromotionUpdate(referralsScreen: ReferralsScreen,
                            gamificationScreen: GamificationScreen,
                            promotionUpdateScreen: PromotionUpdateScreen): Single<Boolean>

  fun getUnwatchedPromotionNotification(): Single<CardNotification>

  fun dismissNotification(): Completable

  fun hasPerksUpdate(promotionUpdateScreen: PromotionUpdateScreen): Single<Boolean>
}
