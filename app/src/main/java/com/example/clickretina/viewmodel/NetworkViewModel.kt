package com.example.clickretina.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidassignment.model.Response
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NetwkViewModel : ViewModel() {
    // LiveData to observe the first title from the server
    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    // LiveData to observe the description from the server
    private val _description = MutableLiveData<String>()
    val description: LiveData<String>
        get() = _description

    // LiveData to observe errors from the server
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    // Fetching data using default Android libraries (without third-party libraries)
    fun fetchDataFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            var httpsURLConnection: HttpURLConnection? = null

            try {
                val url = URL("https://www.jsonkeeper.com/b/6HBE")
                httpsURLConnection = url.openConnection() as HttpURLConnection

                val code = httpsURLConnection.responseCode
                if (code != 200) {
                    throw Exception("Error from server: $code")
                }

                val bufferedReader =
                    BufferedReader(InputStreamReader(httpsURLConnection.inputStream))
                val jsonStringHolder = StringBuilder()
                while (true) {
                    val readLine = bufferedReader.readLine() ?: break
                    jsonStringHolder.append(readLine)
                }

                // Parse the response from the API
                val responseData = Gson().fromJson(jsonStringHolder.toString(), Response::class.java)

                // Extract the content of the first choice, which is a JSON string
                val messageContent = responseData.choices[0].message.content

                // Parse the content JSON string into an object
                val contentJson = JsonParser.parseString(messageContent).asJsonObject
                val titles = contentJson.getAsJsonArray("titles").map { it.asString }
                val description = contentJson.get("description").asString

                // Post the first title and description to LiveData
                _title.postValue(titles[0])  // Assuming you want the first title
                _description.postValue(description)

            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                httpsURLConnection?.disconnect()
            }
        }
    }
}