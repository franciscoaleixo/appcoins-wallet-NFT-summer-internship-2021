package com.asfoundation.wallet.ui.nft;

import io.reactivex.Observable

public interface NftDetailsView {

  fun setupUi()

  fun getOkClick(): Observable<Any>

  fun close()


}
