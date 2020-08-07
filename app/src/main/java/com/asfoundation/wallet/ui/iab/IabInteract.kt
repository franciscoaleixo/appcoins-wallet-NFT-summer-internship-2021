package com.asfoundation.wallet.ui.iab

import com.appcoins.wallet.bdsbilling.Billing
import com.appcoins.wallet.bdsbilling.repository.BillingSupportedType
import com.appcoins.wallet.gamification.Gamification
import com.asfoundation.wallet.backup.NotificationNeeded
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.interact.AutoUpdateInteract
import com.asfoundation.wallet.support.SupportInteractor
import com.asfoundation.wallet.transactions.MissingTransactionDetails
import com.asfoundation.wallet.util.MissingProductException
import io.reactivex.Single
import java.math.BigDecimal

class IabInteract(private val inAppPurchaseInteractor: InAppPurchaseInteractor,
                  private val autoUpdateInteract: AutoUpdateInteract,
                  private val supportInteractor: SupportInteractor,
                  private val gamificationRepository: Gamification,
                  private val billing: Billing) {

  companion object {
    const val PRE_SELECTED_PAYMENT_METHOD_KEY = "PRE_SELECTED_PAYMENT_METHOD_KEY"
  }

  fun showSupport() = supportInteractor.displayChatScreen()

  fun hasPreSelectedPaymentMethod() = inAppPurchaseInteractor.hasPreSelectedPaymentMethod()

  fun getPreSelectedPaymentMethod(): String = inAppPurchaseInteractor.preSelectedPaymentMethod

  fun getWalletAddress(): Single<String> = inAppPurchaseInteractor.walletAddress

  fun getAutoUpdateModel(invalidateCache: Boolean = true) =
      autoUpdateInteract.getAutoUpdateModel(invalidateCache)

  fun isHardUpdateRequired(blackList: List<Int>, updateVersionCode: Int, updateMinSdk: Int) =
      autoUpdateInteract.isHardUpdateRequired(blackList, updateVersionCode, updateMinSdk)

  fun registerUser() =
      inAppPurchaseInteractor.walletAddress.flatMap { address ->
        gamificationRepository.getUserStats(address)
            .doOnSuccess { supportInteractor.registerUser(it.level, address) }
      }

  fun savePreSelectedPaymentMethod(paymentMethod: String) {
    inAppPurchaseInteractor.savePreSelectedPaymentMethod(paymentMethod)
  }

  fun incrementAndValidateNotificationNeeded(): Single<NotificationNeeded> =
      inAppPurchaseInteractor.incrementAndValidateNotificationNeeded()

  fun getMissingTransactionDetails(
      transactionBuilder: TransactionBuilder): Single<MissingTransactionDetails> {
    return billing.getProducts(transactionBuilder.domain, listOf(transactionBuilder.skuId),
        BillingSupportedType.valueOfInsensitive(transactionBuilder.type))
        .map { products -> products.first() }
        .map { product ->
          MissingTransactionDetails(product.title, BigDecimal(product.price.appcoinsAmount),
              product.subscriptionPeriod)
        }
        .onErrorResumeNext {
          getProductAmountOnError(transactionBuilder).map { MissingTransactionDetails(it) }
        }
  }

  private fun getProductAmountOnError(transactionBuilder: TransactionBuilder): Single<BigDecimal> {
    return if (transactionBuilder.amount()
            .compareTo(BigDecimal.ZERO) == 0) {
      Single.error(MissingProductException())
    } else {
      Single.just(transactionBuilder.amount())
    }
  }

}