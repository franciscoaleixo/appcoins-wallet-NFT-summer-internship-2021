package com.asfoundation.wallet.ui.nft

import com.asfoundation.wallet.nfts.domain.NftAsset

interface NftClickListener {
  fun onNftClicked(vh: NftWalletViewHolder?, pos: Int, asset: NftAsset)
}