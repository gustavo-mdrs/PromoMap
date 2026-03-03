package com.example.promomap.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

// Modelo da resposta do Cloudinary (queremos apenas o URL seguro)
data class CloudinaryResponse(val secure_url: String)

interface CloudinaryApi {
    @Multipart
    @POST("{cloud_name}/image/upload")
    suspend fun uploadImage(
        @Path("cloud_name") cloudName: String,
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: okhttp3.RequestBody
    ): CloudinaryResponse
}

object CloudinaryClient {
    private val api: CloudinaryApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/v1_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }

    // Função que a nossa interface vai chamar para enviar a foto
    suspend fun uploadImage(context: Context, imageUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Substitua aqui com os seus dados do site do Cloudinary!
                val cloudName = "dckibmxrc"
                val uploadPreset = "promomap_preset"

                // 2. Lê os bytes da imagem do telemóvel
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes() ?: return@withContext null

                // 3. Prepara os dados para envio
                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", "promo_image.jpg", requestBody)
                val presetBody = uploadPreset.toRequestBody("text/plain".toMediaTypeOrNull())

                // 4. Envia e recebe o URL!
                val response = api.uploadImage(cloudName, multipartBody, presetBody)
                response.secure_url
            } catch (e: Exception) {
                null
            }
        }
    }
}