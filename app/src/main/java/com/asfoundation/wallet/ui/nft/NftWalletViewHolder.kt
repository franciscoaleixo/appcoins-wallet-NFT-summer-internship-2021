package com.asfoundation.wallet.ui.nft

import android.view.View

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
  }

}
