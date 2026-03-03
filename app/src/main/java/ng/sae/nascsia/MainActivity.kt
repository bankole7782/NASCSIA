package ng.sae.nascsia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import ng.sae.nascsia.pplans.ProductionPlanActivity
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
    val userDataFile = File(context.getExternalFilesDir(""), "user_data.txt")
    if (!userDataFile.exists()) {
        context.startActivity(Intent(context, LoginActivity::class.java))
        return
    }

    val accessMap = retrieveAccessMap(context)
    if (accessMap["access_code"]!!.startsWith("c")) {
        context.startActivity(Intent(context, ProductionPlanActivity::class.java))
    } else if (accessMap["access_code"]!!.startsWith(prefix="i")) {

    } else {
        Toast.makeText(context, "Invalid credentials.", Toast.LENGTH_LONG).show()
        userDataFile.delete()
        context.startActivity(Intent(context, LoginActivity::class.java))
    }
}
