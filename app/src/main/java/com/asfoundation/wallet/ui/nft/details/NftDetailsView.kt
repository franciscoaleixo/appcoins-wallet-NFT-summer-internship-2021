package com.asfoundation.wallet.ui.nft.details;

import com.asfoundation.wallet.nfts.domain.NftAsset
import io.reactivex.Observable

public interface NftDetailsView {

  fun getOkClick(): Observable<Any>

  fun getTransactClick(): Observable<Any>

  fun close()

  fun setupUi(nftAsset: NftAsset)

  fun onTransferClicked()
}
