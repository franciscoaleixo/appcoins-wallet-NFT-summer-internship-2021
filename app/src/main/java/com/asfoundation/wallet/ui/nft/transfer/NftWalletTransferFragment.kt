package com.asfoundation.wallet.ui.nft.transfer

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asf.wallet.R
import com.asfoundation.wallet.GlideApp
import com.asfoundation.wallet.nfts.NftInteractor
import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.ui.balance.BalanceActivityView
import com.asfoundation.wallet.ui.nft.details.NftDetailsData
import com.asfoundation.wallet.viewmodel.BasePageViewFragment
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_iab_transaction_completed.view.*
import kotlinx.android.synthetic.main.fragment_nft_transfer.*
import javax.inject.Inject

class NftWalletTransferFragment(private val asset: NftAsset) : BasePageViewFragment(),
  NftWalletTransferView {

  @Inject
  lateinit var nftInteractor: NftInteractor

  private lateinit var presenter: NftWalletTransferPresenter

  private var onBackPressedSubject: PublishSubject<Any>? = null
  private var activityView: BalanceActivityView? = null

  companion object {
    @JvmStatic
    fun newInstance(
      asset: NftAsset
    ): NftWalletTransferFragment {
      return NftWalletTransferFragment(asset)
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context !is BalanceActivityView) {
      throw IllegalStateException("Nft Fragment must be attached to Balance Activity")
    }
    activityView = context
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    presenter = NftWalletTransferPresenter(
      this,
      CompositeDisposable(),
      nftInteractor,
      NftDetailsData(asset),
      AndroidSchedulers.mainThread()
    )
    onBackPressedSubject = PublishSubject.create()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_nft_transfer, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    activityView?.setupToolbar(R.string.nftTransfer)

    complete_payment_view.lottie_transaction_success.setAnimation(R.raw.top_up_success_animation)

    presenter.present()

  }

  override fun onDestroy() {
    presenter.stop()
    super.onDestroy()
  }

  override fun setupUI(nftDetailsData: NftDetailsData) {
    GlideApp.with(nft_image)
      .load(nftDetailsData.asset.image_preview_url)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(nft_image)
    nft_name.text = nftDetailsData.asset.name
  }

  override fun getTransferClick() =
    RxView.clicks(send_button).map { transact_fragment_recipient_address.text.toString() }

  override fun showFeedback(result: String) {
    loading.visibility = View.INVISIBLE
    complete_payment_view.transaction_success_text.text = result
    complete_payment_view.visibility = View.VISIBLE
    complete_payment_view.lottie_transaction_success.playAnimation()
    complete_payment_view.lottie_transaction_success.visibility = View.VISIBLE
    complete_payment_view.lottie_transaction_success.addAnimatorListener(object :
      Animator.AnimatorListener {

      override fun onAnimationStart(p0: Animator?) {
      }

      override fun onAnimationEnd(p0: Animator?) {
        fragmentManager!!.popBackStack()
        fragmentManager!!.popBackStack()
        activityView?.enableBack()
      }

      override fun onAnimationCancel(p0: Animator?) {
      }

      override fun onAnimationRepeat(p0: Animator?) {
      }

    })
  }

  override fun showLoading() {
    activityView?.disableBack()
    title.visibility = View.INVISIBLE
    transact_fragment_recipient_address_layout.visibility = View.INVISIBLE
    atransact_fragment_recipient_address_layout.visibility = View.INVISIBLE
    send_button.visibility = View.INVISIBLE
    nft_image.visibility = View.INVISIBLE
    loading.visibility = View.VISIBLE
    send_button.isEnabled = false
  }

  override fun showInvalidAddressError() {
    transact_fragment_recipient_address_layout.error = getString(R.string.error_invalid_address)
    loading.visibility = View.INVISIBLE
    title.visibility = View.VISIBLE
    transact_fragment_recipient_address_layout.visibility = View.VISIBLE
    atransact_fragment_recipient_address_layout.visibility = View.VISIBLE
    nft_image.visibility = View.VISIBLE
    send_button.visibility = View.VISIBLE
    send_button.isEnabled = true
  }

}