package com.asfoundation.wallet.ui.nft

import android.view.View
import com.asfoundation.wallet.nfts.domain.NftAsset

interface NftWalletView {

  fun setupUI()

  fun updateNftList(list: List<NftAsset>)

  fun updateNftData(count: String)

  fun showNftDetails(view: View, asset: NftAsset)


}