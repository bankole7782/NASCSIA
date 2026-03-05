package ng.sae.nascsia.pplans

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ng.sae.nascsia.retrieveAccessMap
import ng.sae.nascsia.ui.theme.NASCSIATheme
import java.io.File
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

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
fun PPViewScreen(item: Map<String, String>, planFileName: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val accessMap = retrieveAccessMap(context)

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
                    // sync info
                },
                enabled = enablePushBtn,
                modifier = Modifier
                    .height(56.dp)
            ) {
                Text("Push Plan", fontSize = 18.sp)
            }

        }

    }
}

