package kr.blugon.tjfinder.module.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface FinderApi {
    @Multipart
    @POST("uploadImage")
    fun sendImage(
        @Part file: MultipartBody.Part
    ): Call<FinderResponse>
}

data class FinderResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("imagePath") val imagePath: String?,
    @SerializedName("message") val message: String?,
)