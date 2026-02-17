package ng.sae.nascsia

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Requires Coil dependency
import ng.sae.nascsia.ui.theme.NASCSIATheme
import java.io.File

class PPlanPhotosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            NASCSIATheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PPlanPhotosScreen()
                }
            }
        }
    }
}


@Composable
fun PPlanPhotosScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var seedSrcImageUri by remember { mutableStateOf<Uri?>(null) }
    var fieldPhoto1ImageUri by remember { mutableStateOf<Uri?>(null) }
    var fieldPhoto2ImageUri by remember { mutableStateOf<Uri?>(null) }

    // This is the "launcher" that handles the photo picker logic
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> seedSrcImageUri = uri }
    )
    val photoPickerLauncher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> fieldPhoto1ImageUri = uri }
    )
    val photoPickerLauncher3 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> fieldPhoto2ImageUri = uri }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Production Plan Photos", style = MaterialTheme.typography.headlineMedium)

        // first photo
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(110.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (seedSrcImageUri != null) {
                AsyncImage(
                    model = seedSrcImageUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("No Image")
            }
        }

        Button(onClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text("Select Seed Source Receipt Photo")
        }

        // Second Photo
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(110.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (fieldPhoto1ImageUri != null) {
                AsyncImage(
                    model = fieldPhoto1ImageUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("No Image")
            }
        }

        Button(onClick = {
            photoPickerLauncher2.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text("Select Field Photo 1")
        }

        // third photo
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(110.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (fieldPhoto2ImageUri != null) {
                AsyncImage(
                    model = fieldPhoto2ImageUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("No Image")
            }
        }

        // Action: Pick Photo
        Button(onClick = {
            photoPickerLauncher3.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text("Select Field Photo 2")
        }

        // end to screen
        Button(
            onClick = {
                // save photos
                val receiptPhotoName = "receipt_${System.currentTimeMillis()}.jpg"
                saveImageToInternalStorage(context, seedSrcImageUri!!, receiptPhotoName)

                val field1PhotoName = "field1_${System.currentTimeMillis()}.jpg"
                saveImageToInternalStorage(context, fieldPhoto1ImageUri!!, field1PhotoName)

                val field2PhotoName = "field2_${System.currentTimeMillis()}.jpg"
                saveImageToInternalStorage(context, fieldPhoto2ImageUri!!, field2PhotoName)

                PlanDefMap["receipt_photo"] = receiptPhotoName
                PlanDefMap["field_photo_1"] = field1PhotoName
                PlanDefMap["field_photo_2"] = field2PhotoName

                serializePlan(context, PlanDefMap)
                PlanDefMap = mutableMapOf()

                context.startActivity(Intent(context, ProductionPlanActivity::class.java))

            },
            enabled =  seedSrcImageUri != null && fieldPhoto1ImageUri != null && fieldPhoto2ImageUri != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Production Plan")
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri, filename: String): String? {
    val inputStream = context.contentResolver.openInputStream(uri)
//    val file = File(context.getExternalFilesDir(""), "receipt_${System.currentTimeMillis()}.jpg")
    val planPhotos = File(context.getExternalFilesDir(""), "plan_photos")
    planPhotos.mkdirs()

    val file = File(planPhotos, filename)

    return try {
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
        MaterialTheme    }
        }
        file.absolutePath // This is what we store in the DB
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
