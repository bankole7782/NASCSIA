package ng.sae.nascsia

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ng.sae.nascsia.ui.theme.NASCSIATheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


class SeedSourceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NASCSIATheme {
                    SeedSourceScreen()
            }
        }
    }
}

enum class SeedClass(val displayName: String) {
    NUCLEUS("Nucleus"),
    BREEDER("Breeder"),
    FOUNDATION("Foundation"),
    CERTIFIED("Certified");

    companion object {
        fun getDisplayNames() = entries.map { it.displayName }
    }
}

/**
 * Data class representing the state of the Seed Source Information form.
 */
data class SeedSourceInfo(
    val quantityProcured: String = "",
    val seedClass: SeedClass = SeedClass.NUCLEUS,
    val seedCodexNumber: String = "", // Optional field
    val supplierName: String = "",
    val productionYear: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeedSourceScreen() {
    var info by remember { mutableStateOf(SeedSourceInfo()) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Simple validation flags
    val isQuantityValid = info.quantityProcured.toFloatOrNull() != null
    val isYearValid = info.productionYear.toIntOrNull() in 1900..2100 || info.productionYear.isEmpty()
    val isFormComplete = isQuantityValid && info.supplierName.isNotBlank() && isYearValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seed Source Procurement", style = MaterialTheme.typography.titleLarge) }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (isFormComplete) {
                            println("Saving Seed Source Info: $info")
                            // TODO: Implement actual save logic (e.g., to a database)
                            context.startActivity(Intent(context, CropInformationActivity::class.java))

                        }
                    },
                    enabled = isFormComplete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Seed Source")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // 1. Quantity Procured (kg)
            OutlinedTextField(
                value = info.quantityProcured,
                onValueChange = { newValue -> info = info.copy(quantityProcured = newValue) },
                label = { Text("Quantity Procured (kg)") },
                placeholder = { Text("e.g., 50.5") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = !isQuantityValid && info.quantityProcured.isNotBlank(),
                supportingText = {
                    if (!isQuantityValid && info.quantityProcured.isNotBlank()) {
                        Text("Please enter a valid number for quantity.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // 2. Class of Seed Source (Dropdown)
            SeedClassDropdown(
                selectedClass = info.seedClass,
                onClassSelected = { newClass -> info = info.copy(seedClass = newClass) }
            )
            Spacer(Modifier.height(16.dp))

            // 3. Optional Seed Codex Number
            OutlinedTextField(
                value = info.seedCodexNumber,
                onValueChange = { newValue -> info = info.copy(seedCodexNumber = newValue) },
                label = { Text("Seed Codex Number (Optional)") },
                placeholder = { Text("e.g., SCX-2023-A01") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // 4. Supplier Name
            OutlinedTextField(
                value = info.supplierName,
                onValueChange = { newValue -> info = info.copy(supplierName = newValue) },
                label = { Text("Supplier Name") },
                placeholder = { Text("e.g., AgroSeed Corp") },
                isError = info.supplierName.isBlank() && info.quantityProcured.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // 5. Year of Source Seed Production
            OutlinedTextField(
                value = info.productionYear,
                onValueChange = { newValue ->
                    // Limit input to 4 digits for a year
                    if (newValue.length <= 4) {
                        info = info.copy(productionYear = newValue)
                    }
                },
                label = { Text("Year of Source Seed Production") },
                placeholder = { Text("e.g., 2023") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = !isYearValid && info.productionYear.isNotBlank(),
                supportingText = {
                    if (!isYearValid && info.productionYear.isNotBlank()) {
                        Text("Please enter a valid year (1900-2100).")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

/**
 * Composable for the Seed Class Dropdown menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeedClassDropdown(
    selectedClass: SeedClass,
    onClassSelected: (SeedClass) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            // The text field is acting as the anchor for the dropdown
            readOnly = true,
            value = selectedClass.displayName,
            onValueChange = { /* Read only */ },
            label = { Text("Class of Seed Source") },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand Dropdown")
            },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SeedClass.entries.forEach { seedClass ->
                DropdownMenuItem(
                    text = { Text(seedClass.displayName) },
                    onClick = {
                        onClassSelected(seedClass)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
