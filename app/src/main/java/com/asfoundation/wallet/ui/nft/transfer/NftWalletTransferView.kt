package com.asfoundation.wallet.ui.nft.transfer

import com.asfoundation.wallet.ui.nft.details.NftDetailsData
import io.reactivex.Observable

interface NftWalletTransferView {

  fun getTransferClick(): Observable<String>

  fun setupUI(nftDetailsdata: NftDetailsData)

  fun showFeedback(result: String)

  fun showLoading()

  fun showInvalidAddressError()
}