package com.asfoundation.wallet.ui.nft.details

import com.asfoundation.wallet.nfts.NftInteractor
import com.asfoundation.wallet.nfts.domain.NftAsset
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class NftDetailsModule {

  @Provides
  fun providesNftDetailsPresenter(
    activity: NftDetailsActivity,
    interactor: NftInteractor,
    data: NftDetailsData
  ): NftDetailsPresenter {
    return NftDetailsPresenter(activity as NftDetailsView, CompositeDisposable(), interactor, data)
  }

  @Provides
  fun providesNftDetailsData(activity: NftDetailsActivity): NftDetailsData {
    activity.intent.extras!!.apply {
      return NftDetailsData(getSerializable("Asset") as NftAsset)
    }
  }
}