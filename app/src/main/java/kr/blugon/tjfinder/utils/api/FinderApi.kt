package kr.blugon.tjfinder.utils.api

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.gson.annotations.SerializedName
import kr.blugon.tjfinder.ui.screen.child.playlist.FileUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface FinderApi {
    @Multipart
    @POST("uploadImage")
    fun sendImage(
        @Part file: MultipartBody.Part
    ): Call<FinderResponse>

    companion object {
        @Composable
        fun imageUploader(complete: (String)-> Unit): ManagedActivityResultLauncher<String, Uri?> {
            val context = LocalContext.current
            val retrofit = Retrofit
                .Builder()
                .baseUrl("${TjFinderApi.RequestURL}/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FinderApi::class.java)

            return rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                if (uri == null) return@rememberLauncherForActivityResult

                val fileName = "${uri.toString().split("/").last()}.${context.contentResolver.getType(uri)!!.split("/").last()}"
                val file = FileUtil.createTempFile(context, fileName)
                FileUtil.copyToFile(context, uri, file)

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val requestBody = MultipartBody.Part.createFormData("image", file.name, requestFile)

                retrofit.sendImage(requestBody).enqueue(object: Callback<FinderResponse> {
                    override fun onResponse(call: Call<FinderResponse>, response: Response<FinderResponse>) {
                        val body = response.body()
                        if(response.isSuccessful && body?.code == 200) {
                            complete(body.imagePath!!)
                            Toast.makeText(context, "이미지 전송 성공", Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFailure(call: Call<FinderResponse>, t: Throwable) = Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}

data class FinderResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("imagePath") val imagePath: String?,
    @SerializedName("message") val message: String?,
)