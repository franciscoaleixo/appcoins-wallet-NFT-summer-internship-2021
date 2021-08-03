package com.asfoundation.wallet.ui.nft

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.asf.wallet.R
import com.asfoundation.wallet.nfts.NftInteractor
import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.ui.balance.BalanceActivityView
import com.asfoundation.wallet.viewmodel.BasePageViewFragment
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_nft.*
import kotlinx.android.synthetic.main.nft_layout.view.*
import javax.inject.Inject
import kotlin.math.abs


class NftWalletFragment : BasePageViewFragment(), NftWalletView {

  @Inject
  lateinit var nftInteractor: NftInteractor

  private lateinit var presenter: NftWalletPresenter
  private var onBackPressedSubject: PublishSubject<Any>? = null
  private lateinit var adapter: NftWalletAdapter
  private var activityView: BalanceActivityView? = null
  private val nftClickListener: NftClickListener = object : NftClickListener {
    override fun onNftClicked(vh: NftWalletViewHolder?, pos: Int, asset: NftAsset) {
      showNftDetails(vh!!.itemView, asset)
    }
  }

  companion object {
    @JvmStatic
    fun newInstance() = NftWalletFragment()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    presenter = NftWalletPresenter(
      this, nftInteractor, Schedulers.io(), AndroidSchedulers.mainThread(), CompositeDisposable()
    )
    onBackPressedSubject = PublishSubject.create()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_nft, container, false)
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context !is BalanceActivityView) {
      throw IllegalStateException("Nft Fragment must be attached to Balance Activity")
    }
    activityView = context
  }


  override fun setupUI() {
    nft_info_lable_placeholder.playAnimation()
    nft_info_value_placeholder.playAnimation()
  }

  override fun updateNftList(list: List<NftAsset>) {
    adapter.updateList(list)
  }


  override fun updateNftData(nftCount: String) {
    nft_info_lable_placeholder.visibility = View.GONE
    nft_info_value_placeholder.visibility = View.GONE
    (nft_info_lable_placeholder as LottieAnimationView).cancelAnimation()
    (nft_info_value_placeholder as LottieAnimationView).cancelAnimation()
    val plural = if (nftCount == "1") "" else "s"
    nft_info_value.text = "$nftCount NFT$plural"
    nft_info_value.visibility = View.VISIBLE
    nft_info_label.text = "Wallet NFT Count"
    nft_info_label.visibility = View.VISIBLE
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val recicleView = nft_recycler_view
    adapter = NftWalletAdapter(nftClickListener)
    recicleView.adapter = adapter
    recicleView.layoutManager = LinearLayoutManager(context)
    activityView?.setupToolbar()
    presenter.present()

    (app_bar as AppBarLayout).addOnOffsetChangedListener(
      AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        if (nft_info_label != null) {
          val percentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
          setAlpha(nft_info_value, percentage)
          setAlpha(nft_info_label, percentage)
          setAlpha(nft_info_lable_placeholder, percentage)
          setAlpha(nft_info_value_placeholder, percentage)
        }
      })
  }

  override fun showNftDetails(view: View, asset: NftAsset) {
    activityView?.showNftDetailsScreen(view.nft_image, view.nft_title, view, asset)
  }

  private fun setAlpha(view: View, alphaPercentage: Float) {
    view.alpha = 1 - alphaPercentage * 1.20f
  }


}