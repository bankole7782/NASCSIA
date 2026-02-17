package ng.sae.nascsia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ng.sae.nascsia.ui.theme.NASCSIATheme
import java.io.File

/**
 * Main activity using Jetpack Compose to display the login screen.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Apply the custom theme (or your generated theme)
            NASCSIATheme {
                DecideFirstActivity()
            }
        }
    }
}


@Composable
fun DecideFirstActivity() {
    val context = LocalContext.current
    val userDataFile = File(context.getExternalFilesDir(""), "user_data.json")
    if (!userDataFile.exists()) {
        context.startActivity(Intent(context, LoginActivity::class.java))
        return
    }

    val userDataParts = userDataFile.readText().split("\n")
    val userDataAccessCode = userDataParts[0]
    val userDataUserName = userDataParts[1]
    if (userDataAccessCode.startsWith("c")) {
        context.startActivity(Intent(context, PPlanPhotosActivity::class.java))
//        ProductionPlanScreen()
    } else if (userDataAccessCode.startsWith(prefix="i")) {

    } else {
        Toast.makeText(context, "Invalid credentials.", Toast.LENGTH_LONG).show()
        userDataFile.delete()
        context.startActivity(Intent(context, LoginActivity::class.java))
    }
}
