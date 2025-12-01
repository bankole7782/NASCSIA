package ng.sae.nascsia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ng.sae.nascsia.ui.theme.NASCSIATheme

class ProductionPlanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NASCSIATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProductionPlanScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductionPlanScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState), // Make the form scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NASC SIA : Production Plan",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Add Button
            Button(
                onClick = {
//                    // Simple validation and action
//                    if (username.isBlank() || password.isBlank()) {
//                        Toast.makeText(context, "Please enter both credentials.", Toast.LENGTH_SHORT).show()
//                    } else if (validateAndStoreAcessCode(context, username, password)) {
//                        Toast.makeText(context, "Login Successful! Hello, $username", Toast.LENGTH_LONG).show()
//                        context.startActivity(Intent(context, ProductionPlanActivity::class.java))
//                    } else {
//                        Toast.makeText(context, "Login Failed. Check credentials.", Toast.LENGTH_SHORT).show()
//                    }

                    context.startActivity(Intent(context, FieldLocationActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Add Production Plan", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sync Button
            Button(
                onClick = {
                    // sync info
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Sync with NASC", fontSize = 18.sp)
            }
        }
    }
}

//
//fun getAllFieldsRemote(context: Context, accessCode: String): List<FieldDef>? {
//
//    val testDataFields = listOf(
//        FieldDef("Kano", "Kano Street","8323", 1.2, 7.323, 8.2343),
//        FieldDef("Kaduna", "Kaduna Street","32324", 2.0, 7.123, 8.232)
//    )
//
//    if (accessCode == "cp" || accessCode == "ap") {
//        return testDataFields
//    } else {
//        // do network calls
//        return null
//    }
//}