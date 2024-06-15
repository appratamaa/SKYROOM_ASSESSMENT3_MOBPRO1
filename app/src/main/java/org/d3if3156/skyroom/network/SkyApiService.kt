package org.d3if3156.skyroom.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if3156.skyroom.model.OpStatus
import org.d3if3156.skyroom.model.Sky
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

private const val BASE_URL = "https://unspoken.my.id/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface SkyApiService {
    @GET("api_andrepp.php")
    suspend fun getSky(
        @Header("Authorization") userId: String
    ): List<Sky>

    @Multipart
    @POST("api_andrepp.php")
    suspend fun postSky(
        @Header("Authorization") userId: String,
        @Part("nama_rasibintang") nama_rasibintang: RequestBody,
        @Part("daerah_langitdifoto") daerah_langitdifoto: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @DELETE("api_andrepp.php")
    suspend fun deleteSky(
        @Header("Authorization") userId: String,
        @Query("id") skyId: String
    ): OpStatus
}


object SkyApi {
    val service: SkyApiService by lazy {
        retrofit.create(SkyApiService::class.java)
    }

    fun getSkyUrl(imageId: String): String {
        return "${BASE_URL}image.php?id=$imageId"
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }