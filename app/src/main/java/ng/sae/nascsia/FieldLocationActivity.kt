package ng.sae.nascsia

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ng.sae.nascsia.ui.theme.NASCSIATheme

class FieldLocationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NASCSIATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FieldLocationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


/**
 * Defines the composable UI for the Field Location form.
 */
@Composable
fun FieldLocationScreen(modifier: Modifier) {
    // 1. State Management for form fields
    var state by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var plantingArea by remember { mutableStateOf("") }
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

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Field Location Data",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Input Details for New Site",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(40.dp))

            // 2. State in Nigeria Field
            StateDropdown(state) {
                state = it
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Address Field
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Physical Address") },
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Mobile Number Field
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it.filter { it.isDigit() } }, // Only allow digits
                label = { Text("Mobile Number of Contact Person") },
                leadingIcon = { Icon(Icons.Filled.Call, contentDescription = "Phone Icon") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Planting Area Field (ha)
            OutlinedTextField(
                value = plantingArea,
                onValueChange = { plantingArea = it.filter { it.isDigit() || it == '.' } }, // Allow digits and decimal point
                label = { Text("Planting Area (Hectares)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                trailingIcon = { Text(text = "ha", modifier = Modifier.padding(end = 12.dp)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 6. Submission Button
            Button(
                onClick = {
                    if (state.isBlank() || address.isBlank() || mobileNumber.isBlank() || plantingArea.isBlank()) {
                        Toast.makeText(context, "Please fill in all field details.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Data Saved! State: $state, Area: $plantingArea ha",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Submit Location Data", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * List of all Nigerian states plus FCT for the dropdown menu.
 */
val NIGERIAN_STATES = listOf(
    "Abia", "Adamawa", "Akwa Ibom", "Anambra", "Bauchi", "Bayelsa",
    "Benue", "Borno", "Cross River", "Delta", "Ebonyi", "Edo",
    "Ekiti", "Enugu", "FCT", "Gombe", "Imo", "Jigawa", "Kaduna",
    "Kano", "Katsina", "Kebbi", "Kogi", "Kwara", "Lagos", "Nasarawa",
    "Niger", "Ogun", "Ondo", "Osun", "Oyo", "Plateau", "Rivers",
    "Sokoto", "Taraba", "Yobe", "Zamfara"
)

/**
 * Composable for the State Dropdown using ExposedDropdownMenuBox.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateDropdown(selectedState: String, onStateSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            // Display the selected state
            value = selectedState,
            onValueChange = {}, // Read-only for the user
            readOnly = true,
            label = { Text("State in Nigeria") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        // Dropdown Menu where options are shown
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            NIGERIAN_STATES.forEach { stateName ->
                DropdownMenuItem(
                    text = { Text(stateName) },
                    onClick = {
                        onStateSelected(stateName)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
