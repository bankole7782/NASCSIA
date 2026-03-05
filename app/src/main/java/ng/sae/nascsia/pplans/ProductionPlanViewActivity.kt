package ng.sae.nascsia.pplans

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ng.sae.nascsia.RollingLoadingIcon
import ng.sae.nascsia.SERVER_ADDR
import ng.sae.nascsia.retrieveAccessMap
import ng.sae.nascsia.ui.theme.NASCSIATheme
import java.io.File
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException

class ProductionPlanViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val accessMap = retrieveAccessMap(this)
        val plansDir = File(getExternalFilesDir(""), "plans")
        val companyPlansDir = File(plansDir, accessMap["company"]!!)
        val planFileName = intent.getStringExtra("planFileName")
        val planFile = File(companyPlansDir, planFileName!!)
        val planDetails = jsonToMutableMap(planFile.readText())

        setContent {
            NASCSIATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PPViewScreen(
                        item = planDetails, planFileName = planFileName,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PPViewScreen(item: MutableMap<String, String>, planFileName: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val accessMap = retrieveAccessMap(context)
    val coroutineScope = rememberCoroutineScope()
    var isRolling by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Production Plan: " + planFileName?.substring(0, planFileName.length-5),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(10.dp))

        for ((key, value) in item) {
            if (key in listOf("receipt_photo",  "field_photo_1", "field_photo_2")) {
                continue
            }
            Text(key, fontWeight = FontWeight.Bold)
            Text(value)
            Spacer(Modifier.height(5.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("Receipt Photo", fontWeight = FontWeight.Bold)
        AsyncImage(
            model = getPhotoFile(context, item["receipt_photo"]!!),
            contentDescription = "Receipt Photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text("Field Photo 1", fontWeight = FontWeight.Bold)
        AsyncImage(
            model = getPhotoFile(context, item["field_photo_1"]!!),
            contentDescription = "Receipt Photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text("Field Photo 2", fontWeight = FontWeight.Bold)
        AsyncImage(
            model = getPhotoFile(context, item["field_photo_2"]!!),
            contentDescription = "Receipt Photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier= Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var enableDeleteBtn: Boolean  = true
            var enablePushBtn : Boolean = true
            if ("field_id" in item) {
                enableDeleteBtn = false
                enablePushBtn = false
            }
            Button(
                onClick = {
                    // delete photos
                    val photosDir = File(context.getExternalFilesDir(""), "plan_photos")
                    val companyPlanPhotosDir = File(photosDir, accessMap["company"]!!)

                    val photo1 = File(companyPlanPhotosDir, item["receipt_photo"]!!)
                    val photo2 = File(companyPlanPhotosDir, item["field_photo_1"]!!)
                    val photo3 = File(companyPlanPhotosDir, item["field_photo_2"]!!)

                    photo1.delete()
                    photo2.delete()
                    photo3.delete()

                    // delete plan file
                    val plansDir = File(context.getExternalFilesDir(""), "plans")
                    val companyPlansDir = File(plansDir, accessMap["company"]!!)
                    val planFile = File(companyPlansDir, planFileName!!)
                    planFile.delete()

                    context.startActivity(Intent(context, ProductionPlanActivity::class.java))

                },
                enabled = enableDeleteBtn,
                modifier = Modifier
                    .height(56.dp)
            ) {
                Text("Delete Plan", fontSize = 18.sp)
            }

            Spacer(modifier= Modifier.width(15.dp))

            Button(
                onClick = {
                    isRolling = true
                    // sync info
                    val photosDir = File(context.getExternalFilesDir(""), "plan_photos")
                    val companyPlanPhotosDir = File(photosDir, accessMap["company"]!!)

                    val photo1 = File(companyPlanPhotosDir, item["receipt_photo"]!!)
                    val photo2 = File(companyPlanPhotosDir, item["field_photo_1"]!!)
                    val photo3 = File(companyPlanPhotosDir, item["field_photo_2"]!!)

                    coroutineScope.launch(Dispatchers.IO) {
                        Compressor.compress(context, photo1) {
                            resolution(1280, 720) // Max resolution
                            quality(80)            // 80% quality
                            format(Bitmap.CompressFormat.JPEG) // Convert to WEBP
                            size(697_152)        // Max file size in bytes (600KB)
                        }
                        Compressor.compress(context, photo2) {
                            resolution(1280, 720) // Max resolution
                            quality(80)            // 80% quality
                            format(Bitmap.CompressFormat.JPEG) // Convert to WEBP
                            size(697_152)        // Max file size in bytes (600KB)
                        }

                        Compressor.compress(context, photo3) {
                            resolution(1280, 720) // Max resolution
                            quality(80)            // 80% quality
                            format(Bitmap.CompressFormat.JPEG) // Convert to WEBP
                            size(697_152)        // Max file size in bytes (600KB)
                        }

                        val uploadUrl = SERVER_ADDR + "/submit_photo/" + accessMap["access_code"]!!
                        val photo1ServerName = uploadImage(photo1, uploadUrl)
                        val photo2ServerName = uploadImage(photo2, uploadUrl)
                        val photo3ServerName = uploadImage(photo3, uploadUrl)

                        val itemCopy = item.toMutableMap()
                        itemCopy["receipt_photo"] = photo1ServerName
                        itemCopy["field_photo_1"] = photo2ServerName
                        itemCopy["field_photo_2"] = photo3ServerName

                        val formDataUrl = SERVER_ADDR + "/submit_pplan/" + accessMap["access_code"]!!

                        val retStr = postFormData(formDataUrl, itemCopy)
                        val retObj = jsonToMutableMap(retStr)

                        item["field_id"] = retObj["field_id"]!!

                        val gson = Gson()
                        val jsonString = gson.toJson(item)

                        val plansDir = File(context.getExternalFilesDir(""), "plans")
                        val companyPlansDir = File(plansDir, accessMap["company"]!!)
                        val planFile = File(companyPlansDir, planFileName!!)
                        planFile.writeText(jsonString)

                        isRolling = false

                        Toast.makeText(context, "Push Successful", Toast.LENGTH_LONG).show()
                        context.startActivity(Intent(context, ProductionPlanActivity::class.java))
                    }
                },
                enabled = enablePushBtn,
                modifier = Modifier
                    .height(56.dp)
            ) {
                Text("Push Plan", fontSize = 18.sp)
            }

        }

    }

    RollingLoadingIcon(isRolling)

}

fun uploadImage(file: File, uploadUrl: String): String {
    val client = OkHttpClient()

    // Define the media type for the image
    val mediaType = "image/jpeg".toMediaTypeOrNull()

    // Create the RequestBody for the file
    val requestFile = file.asRequestBody(mediaType)

    // Build the MultipartBody request body
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
//        .addFormDataPart("userid", userId) // Add other form data if needed
        .addFormDataPart(
            "file", // The name expected by the server
            file.name,
            requestFile
        )
        .build()

    // Build the final POST request
    val request = Request.Builder()
        .url(uploadUrl)
        .post(requestBody)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            return ""
        }
        val retMap = jsonToMutableMap(response.body.string())
        if (retMap["error"] == "false") {
            return retMap["photo_name"]!!
        }
    }

    return ""
}
