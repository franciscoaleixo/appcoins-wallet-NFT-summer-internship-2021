package com.asfoundation.wallet.nfts.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NftAsset(
  @SerializedName("id") val id: String,
  @SerializedName("token_id") val token_id: String,
  @SerializedName("image_preview_url") val image_preview_url: String,
  @SerializedName("name") val name: String,
  @SerializedName("description") val description: String?,
  @SerializedName("address") val contract_address: String,
  @SerializedName("schema_name") val schema_name: String,
  @SerializedName("isOwnedByMe") val isOwnedByMe: Boolean,

  ) : Serializable