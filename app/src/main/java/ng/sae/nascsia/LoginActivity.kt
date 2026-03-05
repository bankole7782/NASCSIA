package ng.sae.nascsia

import android.R.attr.rotation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ng.sae.nascsia.pplans.ProductionPlanActivity
import ng.sae.nascsia.pplans.jsonToMutableMap
import ng.sae.nascsia.ui.theme.NASCSIATheme
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

val SERVER_ADDR: String = "http:////192.168.1.70:8085"

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NASCSIATheme {
                LoginScreen()
            }
        }
    }
}

/**
 * Defines the entire composable UI for the Login Screen.
 */
@Composable
fun LoginScreen() {
    // 1. State Management: Use rememberSaveable to retain state across configuration changes
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isRolling by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2. Logo/Image Placeholder
            Spacer(modifier = Modifier.height(64.dp))
            Image(
                painter = painterResource(R.drawable.nasc_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(240.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NASC SIA (Seed Inspection App)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "mail us ojobankole@gmail.com for your access codes"
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 3. Username Input Field (Name)
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Staff Name") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Username Icon") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Password Input Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Company Access Code") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password Icon") },
                visualTransformation = PasswordVisualTransformation(), // Hides text input
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 5. Login Button
            Button(
                onClick = {
                    // Simple validation and action
                    if (username.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please enter both credentials.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    coroutineScope.launch(Dispatchers.Main) {
                        isRolling = true
                        var loginStatus: Boolean = false
                        withContext(Dispatchers.IO) {
                            loginStatus = validateAndStoreAcessCode(context, username, password)
                        }
//                        delay(10000L)
                        isRolling = false
                        if (loginStatus) {
                            Toast.makeText(context, "Login Successful! Hello, $username", Toast.LENGTH_LONG).show()
                            context.startActivity(Intent(context, ProductionPlanActivity::class.java))
                        } else {
                            Toast.makeText(context, "Login Failed. Check credentials.", Toast.LENGTH_SHORT).show()
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Login", fontSize = 18.sp)
            }
        }
    }

    RollingLoadingIcon(isRolling)
}

fun validateAndStoreAcessCode(context: Context, username: String, accessCode: String):Boolean {
    if (accessCode == "cp" || accessCode == "ip") {
        // test accounts
        val userStr = "access_code: $accessCode\nusername: $username\ncompany: Test1"
        val userDataFile = File(context.getExternalFilesDir(""), "user_data.txt")
        userDataFile.writeText(userStr)
        return true
    } else {
        // do network call check
        val client = OkHttpClient()

        val loginURL = "$SERVER_ADDR/verify_accesscode/$accessCode"
        val request = Request.Builder()
            .url(loginURL).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return false
            }

            val retMap = jsonToMutableMap(response.body.string())
            if (retMap["error"] == "false") {
                val userStr = "access_code: $accessCode\nusername: $username\ncompany: " + retMap["company_name"]
                val userDataFile = File(context.getExternalFilesDir(""), "user_data.txt")
                userDataFile.writeText(userStr)
                return true
            }
        }

        return false
    }
}

fun retrieveAccessMap(context: Context): Map<String, String> {
    var outMap: MutableMap<String, String> = mutableMapOf()

    val userDataFile = File(context.getExternalFilesDir(""), "user_data.txt")
    val userDataParts = userDataFile.readText().split("\n")
    for (part in userDataParts) {
        val itemParts = part.split(":")
        outMap[itemParts[0]] = itemParts[1].trim()
    }

    return outMap
}

@Composable
fun RollingLoadingIcon(isLoading: Boolean) {
    if (isLoading) {
        val infiniteTransition = rememberInfiniteTransition(label = "rolling")
//        val angle by infiniteTransition.animateFloat(
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        // Overlay Box: Full screen, semi-transparent background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                // Disable touch interactions
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {},
            contentAlignment = Alignment.Center
        ) {
            // Rotating Icon
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Loading",
                modifier = Modifier
                    .size(64.dp)
                    .rotate(rotation),
                tint = Color.White
            )
        }
    }
}
