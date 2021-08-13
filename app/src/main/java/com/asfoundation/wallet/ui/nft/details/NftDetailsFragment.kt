package com.asfoundation.wallet.ui.nft.details

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asf.wallet.R
import com.asfoundation.wallet.GlideApp
import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.ui.balance.BalanceActivityView
import com.asfoundation.wallet.ui.nft.transfer.NftWalletTransferFragment
import com.asfoundation.wallet.viewmodel.BasePageViewFragment
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_token_details.close_btn
import kotlinx.android.synthetic.main.fragment_nft_details.*
import javax.inject.Inject


class NftDetailsFragment(private val asset: NftAsset) : BasePageViewFragment(), NftDetailsView {

  private var contentVisible = false

  @Inject
  lateinit var presenter: NftDetailsPresenter

  private var activityView: BalanceActivityView? = null
  private var onBackPressedSubject: PublishSubject<Any>? = null

  companion object {
    @JvmStatic
    fun newInstance(
      asset: NftAsset
    ): NftDetailsFragment {
      return NftDetailsFragment(asset)
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
    presenter = NftDetailsPresenter(
      this, CompositeDisposable(), NftDetailsData(asset)
    )
    sharedElementEnterTransition =
      TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    onBackPressedSubject = PublishSubject.create()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_nft_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.present()
  }

  override fun onDestroy() {
    presenter.stop()
    super.onDestroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean("visible", contentVisible)
  }

  override fun setupUi(nftAsset: NftAsset) {
    GlideApp.with(token_icon)
      .load(nftAsset.image_preview_url)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(token_icon)
    token_name.text = nftAsset.name
    close_btn.visibility = View.VISIBLE;
    transact_btn.visibility = View.VISIBLE;
    token_description.text = nftAsset.description
    token_description.visibility = View.VISIBLE;
  }

  override fun getOkClick(): Observable<Any> {
    return RxView.clicks(close_btn)
  }

  override fun getTransactClick(): Observable<Any> {
    return RxView.clicks(transact_btn)
  }

  override fun close() {
    fragmentManager!!.popBackStack()
  }

  override fun onTransferClicked() {
    fragmentManager!!.beginTransaction()
      .setReorderingAllowed(true)
      .replace(R.id.fragment_container, NftWalletTransferFragment.newInstance(asset))
      .addToBackStack(null)
      .commit()
  }


  fun getAsset(): NftAsset {
    return asset
  }

}
