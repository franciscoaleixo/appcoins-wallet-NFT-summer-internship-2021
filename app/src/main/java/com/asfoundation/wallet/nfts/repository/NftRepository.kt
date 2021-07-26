package com.asfoundation.wallet.nfts.repository

import com.asfoundation.wallet.nfts.domain.NftAsset
import com.asfoundation.wallet.nfts.repository.api.NftApi
import io.reactivex.Single

class NftRepository(
  private val nftApi: NftApi
) {
  fun getNFTCount(address: String): Single<Int> {
    return nftApi.getNFTsOfWallet(address).map { response -> response.assets.size }
  }

  fun getNFTAssetList(address: String): Single<List<NftAsset>> {
    return nftApi.getNFTsOfWallet(address).map { response ->
      response.assets.map { assetResponse ->
        NftAsset(
          assetResponse.id,
          assetResponse.image_preview_url,
          assetResponse.name
        )
      }
    }
  }
}