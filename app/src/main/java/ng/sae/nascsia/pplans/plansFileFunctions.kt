package ng.sae.nascsia.pplans

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.iterator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ng.sae.nascsia.retrieveAccessMap
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Calendar

data class UserData(val accessCode: String, val userName: String)

//data class PlanDef(
//    val staffName: String,
//    val state: String,
//    val address: String,
//    val mobileNumber: String,
//    val plantingArea: Double,
//    val latitude: Double,
//    val longitude: Double,
//    val crop: String,
//    val variety: String,
//    val seedClass: String,
//    val productionYear: String,
//    val plantingDate: String,
//    val srcQuantityProcured: String,
//    val srcSeedClass: SeedClass,
//    val srcSeedCodexNumber: String, // Optional field
//    val srcSupplierName: String,
//    val srcProductionYear: String
//)

fun getCurrentTimeString(): String {
    // Get the current date and time
    val currentTime = Date()

    // Define the desired date and time format
    val formatter = SimpleDateFormat("yyyy:M:dd_hh:mm:ss", Locale.getDefault())

    // Format the current time into a string
    return formatter.format(currentTime)
}


fun getDateStrFromFilename(inStr: String): String {
    var tmp = inStr.replace(".txt", "")
    tmp = tmp.replace("_", "  ")
    return tmp
}

fun getPhotoFile(context: Context, planPhotoName: String): File {
    val accessMap = retrieveAccessMap(context)
    val plansDir = File(context.getExternalFilesDir(""), "plan_photos")
    val companyPlanPhotosDir = File(plansDir, accessMap["company"]!!)
    return File(companyPlanPhotosDir, planPhotoName.trim())
}


fun generateRandomFourDigitInt(): Int {
    val min = 1000
    val max = 9999
    // Generates a random integer in the range [1000, 9999]
    val randomPin = (min..max).random()
    return randomPin
}

fun jsonToMutableMap(jsonString: String): MutableMap<String, String> {
    val gson = Gson()
    // Define the type of the target map using TypeToken to handle generics
    val type = object : TypeToken<MutableMap<String, String>>() {}.type
    // Use fromJson() to deserialize the JSON string into the specified type
    return gson.fromJson(jsonString, type)
}

fun getTodayDateAsStringLegacy(): String {
    // Get a Calendar instance with the current date and time
    val calendar = Calendar.getInstance()

    // Define the desired format pattern and use the default locale
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Format the current time from the calendar instance as a string
    return dateFormat.format(calendar.time)
}

fun postFormData(url: String, formData: Map<String, String>): Boolean {
    val client = OkHttpClient()

    // Build the form body
    val formBodyBuilder = FormBody.Builder()
    for ((key, value) in formData) {
        formBodyBuilder.add(key, value)
    }
    val formBody = formBodyBuilder.build()

    // Build the request
    val request = Request.Builder()
        .url(url)
        .post(formBody) // Use .post() with the form body
        .build()

    // Execute the request (synchronously in this example, use client.newCall(request).enqueue(...) for asynchronous)
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            Log.i("Post form response", response.body!!.string())
            return false
        }

        return true
    }
}