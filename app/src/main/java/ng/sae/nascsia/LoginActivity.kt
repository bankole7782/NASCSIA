package ng.sae.nascsia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.Json
import ng.sae.nascsia.ui.theme.NASCSIATheme
import java.io.File

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
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
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
                    } else if (validateAndStoreAcessCode(context, username, password)) {
                        Toast.makeText(context, "Login Successful! Hello, $username", Toast.LENGTH_LONG).show()
                        context.startActivity(Intent(context, FieldsActivity::class.java))
                    } else {
                        Toast.makeText(context, "Login Failed. Check credentials.", Toast.LENGTH_SHORT).show()
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
}

fun validateAndStoreAcessCode(context: Context, username: String, accessCode: String):Boolean {
    if (accessCode == "cp" || accessCode == "ap") {
        // test accounts
        val obj = Json.encodeToString(UserData(accessCode = accessCode, userName = username))
        val userDataFile = File(context.getExternalFilesDir(""), "user_data.json")
        userDataFile.writeText(obj)
        return true
    } else {
        // do network call check
        return false
    }
}