package com.asfoundation.wallet.ui.nft.details

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class NftDetailsModule {

  @Provides
  fun providesNftDetailsPresenter(
    fragment: NftDetailsFragment,
    data: NftDetailsData
  ): NftDetailsPresenter {
    return NftDetailsPresenter(fragment as NftDetailsView, CompositeDisposable(), data)
  }

  @Provides
  fun providesNftDetailsData(fragment: NftDetailsFragment): NftDetailsData {
    return NftDetailsData(fragment.getAsset())
  }
}