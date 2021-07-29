package com.asfoundation.wallet.ui.nft

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Transition
import android.view.View
import android.view.Window
import com.asf.wallet.R
import com.asfoundation.wallet.GlideApp
import com.asfoundation.wallet.ui.BaseActivity
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_token_details.*


class NftDetailsActivity : BaseActivity(), NftDetailsView {

  private var contentVisible = false
  private lateinit var presenter: NftDetailsPresenter
  private lateinit var nftName: String
  private lateinit var nftDescription: String
  private lateinit var nftUrl: String

  override fun onResume() {
    super.onResume()
    sendPageViewEvent()
  }


  companion object {
    @JvmStatic
    fun newInstance(
      context: Context,
      nftName: String,
      nftDescription: String,
      nftUrl: String
    ): Intent {
      val intent = Intent(context, NftDetailsActivity::class.java)
      intent.putExtra("Name", nftName)
      intent.putExtra("Description", nftDescription)
      intent.putExtra("Url", nftUrl)
      return intent
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    setContentView(R.layout.activity_nft_details)
    presenter = NftDetailsPresenter(this, CompositeDisposable())
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
    super.onBackPressed()
  }

  override fun setupUi() {
    intent.extras?.let {
      if (it.containsKey(
          "Name"
        ) && it.containsKey("Description") && it.containsKey("Url")
      ) {
        nftName = it.getSerializable("Name") as String
        nftDescription = it.getSerializable("Description") as String
        nftUrl = it.getSerializable("Url") as String
        setContent(nftName, nftDescription, nftUrl)
      }
    }

    val sharedElementEnterTransition = window.sharedElementEnterTransition
    sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
      override fun onTransitionStart(transition: Transition) {
      }

      override fun onTransitionEnd(transition: Transition) {
        if (!contentVisible) {
          token_description.visibility = View.VISIBLE
          close_btn.visibility = View.VISIBLE
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
    }
  }

  override fun getOkClick(): Observable<Any> {
    return RxView.clicks(close_btn)
  }

  override fun close() {
    onBackPressed()
  }

}
