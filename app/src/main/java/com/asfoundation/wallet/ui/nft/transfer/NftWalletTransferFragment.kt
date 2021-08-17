package com.asfoundation.wallet.ui.nft.transfer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.asf.wallet.R
import com.asfoundation.wallet.GlideApp
import com.asfoundation.wallet.entity.Address
import com.asfoundation.wallet.nfts.NftInteractor
import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.ui.balance.BalanceActivityView
import com.asfoundation.wallet.ui.barcode.BarcodeCaptureActivity
import com.asfoundation.wallet.ui.nft.details.NftDetailsData
import com.asfoundation.wallet.viewmodel.BasePageViewFragment
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.FacebookSdk.getApplicationContext
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_nft_transfer.*
import javax.inject.Inject

class NftWalletTransferFragment(private val asset: NftAsset) : BasePageViewFragment(),
  NftWalletTransferView {

  private val BARCODE_READER_REQUEST_CODE = 1

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
      this, CompositeDisposable(), nftInteractor, NftDetailsData(asset), Schedulers.io()
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

    val scanBarcodeButton: ImageButton = scan_barcode_button
    scanBarcodeButton.setOnClickListener { view: View? ->
      val intent = Intent(
        getApplicationContext(),
        BarcodeCaptureActivity::class.java
      )
      startActivityForResult(intent, BARCODE_READER_REQUEST_CODE)
    }

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

  override fun validateAddress() {
    val address = transact_fragment_recipient_address.text.toString()
    if (Address.isAddress(address)) {
      //Show FEEDBACK
    }
    Log.d("NFT", "1")
  }

  override fun showFeedback(result: String) {
  }

  override fun showLoading() {
    /*
    loading.visibility = View.VISIBLE
    send_button.isEnabled = false
    */
  }

  override fun showInvalidAddressError() {

  }

}