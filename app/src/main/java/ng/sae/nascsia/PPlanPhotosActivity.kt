package ng.sae.nascsia

import android.content.Context
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Requires Coil dependency
import java.io.File

class PPlanPhotosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PPlanPhotosScreen()
                }
            }
        }
    }
}


@Composable
fun PPlanPhotosScreen() {
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
            onClick = { /* Handle Save */ },
            enabled =  seedSrcImageUri != null && fieldPhoto1ImageUri != null && fieldPhoto2ImageUri != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Production Plan")
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.filesDir, "profile_picture_${System.currentTimeMillis()}.jpg")

    return try {
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath // This is what we store in the DB
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
