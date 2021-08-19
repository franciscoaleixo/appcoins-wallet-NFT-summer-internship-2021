package com.asfoundation.wallet.ui.nft.wallet

import android.view.View
import android.view.View.VISIBLE

import androidx.recyclerview.widget.RecyclerView
import com.asfoundation.wallet.GlideApp
import com.asfoundation.wallet.nfts.domain.NftAsset
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.nft_layout.view.*


class NftWalletViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  fun bind(nft: NftAsset) {
    val currentUrl: String = nft.image_preview_url
    GlideApp.with(itemView)
      .load(currentUrl)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(itemView.nft_image)
    itemView.nft_title.text = nft.name
    if (!nft.isOwnedByMe) {
      itemView.nft_status.text = "In Transit"
      itemView.nft_status.visibility = VISIBLE
    }
  }

}
