package com.asfoundation.wallet.nfts.domain

import com.google.gson.annotations.SerializedName

data class NftAsset(
  @SerializedName("id") val id: String,
  @SerializedName("image_preview_url") val image_preview_url: String,
  @SerializedName("name") val name: String
)