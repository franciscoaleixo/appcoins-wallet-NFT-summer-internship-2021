package com.asfoundation.wallet.ui.nft.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Transition
import android.view.View
import android.view.Window
import com.asf.wallet.R
import com.asfoundation.wallet.GlideApp
import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.ui.BaseActivity
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_nft_details.*
import kotlinx.android.synthetic.main.activity_token_details.*
import kotlinx.android.synthetic.main.activity_token_details.close_btn
import kotlinx.android.synthetic.main.activity_token_details.token_description
import kotlinx.android.synthetic.main.activity_token_details.token_icon
import kotlinx.android.synthetic.main.activity_token_details.token_name
import javax.inject.Inject


class NftDetailsActivity() : BaseActivity(), NftDetailsView {

  private var contentVisible = false
  @Inject
  lateinit var presenter: NftDetailsPresenter

  override fun onResume() {
    super.onResume()
    sendPageViewEvent()
  }


  companion object {
    @JvmStatic
    fun newInstance(
      context: Context,
      asset: NftAsset
    ): Intent {
      val intent = Intent(context, NftDetailsActivity::class.java)
      intent.putExtra("Asset", asset)
      return intent
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    setContentView(R.layout.activity_nft_details)
    savedInstanceState?.let {
      contentVisible = it.getBoolean("visible")
    }
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

  private fun setContent(nftName: String, nftDescription: String, nftUrl: String) {

    GlideApp.with(token_icon)
      .load(nftUrl)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(token_icon)
    token_name.text = nftName
    token_description.text = nftDescription

  }

  override fun onBackPressed() {
    token_description.visibility = View.INVISIBLE
    close_btn.visibility = View.INVISIBLE
    transact_btn.visibility = View.INVISIBLE
    super.onBackPressed()
  }

  override fun setupUi(nftAsset: NftAsset) {
    setContent(
      nftAsset.name,
      if (nftAsset.description == null) "This NFT doesn't have a description" else nftAsset.description,
      nftAsset.image_preview_url
    )

    val sharedElementEnterTransition = window.sharedElementEnterTransition
    sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
      override fun onTransitionStart(transition: Transition) {
      }

      override fun onTransitionEnd(transition: Transition) {
        if (!contentVisible) {
          token_description.visibility = View.VISIBLE
          close_btn.visibility = View.VISIBLE
          transact_btn.visibility = View.VISIBLE
          contentVisible = true
        }
      }

      override fun onTransitionCancel(transition: Transition) {
      }

      override fun onTransitionPause(transition: Transition) {
      }

      override fun onTransitionResume(transition: Transition) {
      }
    })

    if (contentVisible) {
      token_symbol.visibility = View.VISIBLE
      token_description.visibility = View.VISIBLE
      close_btn.visibility = View.VISIBLE
      transact_btn.visibility = View.VISIBLE
    }
  }

  override fun getOkClick(): Observable<Any> {
    return RxView.clicks(close_btn)
  }

  override fun getTransactClick(): Observable<Any> {
    return RxView.clicks(transact_btn)
  }

  override fun close() {
    onBackPressed()
  }

}
