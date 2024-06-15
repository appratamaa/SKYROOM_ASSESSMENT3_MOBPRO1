package org.d3if3156.skyroom.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if3156.skyroom.model.Sky
import org.d3if3156.skyroom.network.ApiStatus
import org.d3if3156.skyroom.network.SkyApi
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Sky>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = SkyApi.service.getSky(userId)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
                errorMessage.value = "Failed to retrieve data: ${e.message}"
            }
        }
    }

    fun saveData(userId: String, nama_rasibintang: String, daerah_langitdifoto: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = SkyApi.service.postSky(
                    userId,
                    nama_rasibintang.toRequestBody("text/plain".toMediaTypeOrNull()),
                    daerah_langitdifoto.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )

                if (result.status == "success") {
                    Log.d("MainViewModel", "Data saved successfully")
                    retrieveData(userId)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error saving data: ${e.message}"
            }
        }
    }

    fun deleteData(userId: String, skyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = SkyApi.service.deleteSky(userId, skyId)
                if (response.status == "success") {
                    Log.d("MainViewModel", "Data deleted successfully: $skyId")
                    retrieveData(userId)
                } else {
                    Log.e("MainViewModel", "Failed to delete data: ${response.message}")
                    errorMessage.value = "Failed to delete data: ${response.message}"
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error deleting data: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody
        )
    }

    fun clearMessage() {
        errorMessage.value = null
    }
}