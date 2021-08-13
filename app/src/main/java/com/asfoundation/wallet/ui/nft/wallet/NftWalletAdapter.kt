package com.asfoundation.wallet.ui.nft.wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import com.asfoundation.wallet.nfts.domain.NftAsset

class NftWalletAdapter(val nftClickListener: NftClickListener) :
  RecyclerView.Adapter<NftWalletViewHolder>() {
  val activeNftList: MutableList<NftAsset> = mutableListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NftWalletViewHolder {
    val layout = LayoutInflater.from(parent.context)
      .inflate(R.layout.nft_layout, parent, false)
    return NftWalletViewHolder(layout)
  }

  override fun getItemCount(): Int {
    return activeNftList.size
  }

  override fun onBindViewHolder(holder: NftWalletViewHolder, position: Int) {
    holder.bind(activeNftList[position])
    holder.itemView.setOnClickListener {
      nftClickListener.onNftClicked(
        holder,
        position,
        activeNftList[position]
      )
    }
  }

  fun updateList(list: List<NftAsset>) {
    activeNftList.clear()
    activeNftList.addAll(list)
    notifyDataSetChanged()
  }
}

