package com.asfoundation.wallet.nfts

import com.google.gson.annotations.SerializedName

data class NftResponse(val assets: List<NftAssetResponse>)

data class NftAssetResponse(
  @SerializedName("id") val id: String,
  @SerializedName("token_id") val token_id: String,
  @SerializedName("image_preview_url") val image_preview_url: String,
  @SerializedName("name") val name: String,
  @SerializedName("description") val description: String,
  @SerializedName("asset_contract") val asset_contract: NftAssetResponseContract,
)

data class NftAssetResponseContract(
  @SerializedName("address") val address: String,
  @SerializedName("schema_name") val schema_name: String,
)
