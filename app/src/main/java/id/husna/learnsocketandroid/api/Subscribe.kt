package id.husna.learnsocketandroid.api

import com.google.gson.annotations.SerializedName

data class Subscribe(
    val type: String = "subscribe",
    @SerializedName("product_ids")
    val productIds: List<String>,
    val channels: List<String>
)