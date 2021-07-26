package com.asfoundation.wallet.nfts.repository.api

import com.asfoundation.wallet.nfts.NftResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NftApi {
  @GET("assets")
  fun getNFTsOfWallet(@Query("owner") owner: String): Single<NftResponse>
}