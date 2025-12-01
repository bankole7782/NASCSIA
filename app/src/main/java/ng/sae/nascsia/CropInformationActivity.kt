package ng.sae.nascsia

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat.enableEdgeToEdge
import java.time.Year
import ng.sae.nascsia.ui.theme.NASCSIATheme

// --- 1. ENUMS AND DATA CLASSES ---

enum class Crop(val displayName: String) {
    BANANA_PLANTAIN("Banana / Plantain"),
    BENI_SEED("Beni seed"),
    CASSAVA("Cassava"),
    COWPEA("Cowpea"),
    COTTON("Cotton"),
    MAIZE("Maize"),
    MILLET("Millet"),
    POTATO("Potato"),
    RICE("Rice"),
    SORGHUM("Sorghum"),
    SOYBEAN("Soybean"),
    SWEET_POTATO("Sweet potato"),
    YAM("Yam");

    companion object {
        fun getDisplayNames() = entries.map { it.displayName }
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

data class CropInfoState(
    val crop: String = "",
    val variety: String = "",
    val seedClass: String = "",
    val productionYear: String = "",
    val plantingDate: String = ""
)

// --- 2. MAIN ACTIVITY AND THEME (Mocked for single file) ---

class CropInformationActivity : ComponentActivity() {
//    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Simplified theme for this single file
            NASCSIATheme() {
                CropInfoScreen()
            }
        }
    }
}

// --- 3. REUSABLE DROPDOWN COMPONENT ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> EnumDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption.ifEmpty { "Select $label" },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

// --- 4. MAIN SCREEN COMPOSABLE ---

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropInfoScreen() {
    val context = LocalContext.current
    var cropInfoState by remember { mutableStateOf(CropInfoState()) }
    var showSubmissionMessage by remember { mutableStateOf(false) }


    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Seed Production Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- Crop Dropdown ---
            EnumDropdown(
                label = "Crop",
                options = Crop.getDisplayNames(),
                selectedOption = cropInfoState.crop,
                onOptionSelected = { selectedCrop ->
                    cropInfoState = cropInfoState.copy(crop = selectedCrop)
                }
            )

            // --- Variety Text Field ---
            OutlinedTextField(
                value = cropInfoState.variety,
                onValueChange = { cropInfoState = cropInfoState.copy(variety = it) },
                label = { Text("Variety") },
                placeholder = { Text("e.g., Tainung No. 1, Oba Super 6") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // --- Seed Class Dropdown ---
            EnumDropdown(
                label = "Class of Seed",
                options = SeedClass.getDisplayNames(),
                selectedOption = cropInfoState.seedClass,
                onOptionSelected = { selectedClass ->
                    cropInfoState = cropInfoState.copy(seedClass = selectedClass)
                }
            )

            // --- Year of Production Text Field ---
            OutlinedTextField(
                value = cropInfoState.productionYear,
                onValueChange = {
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        cropInfoState = cropInfoState.copy(productionYear = it)
                    }
                },
                label = { Text("Year of Production") },
                placeholder = { Text(Year.now().value.toString()) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // --- Proposed Date of Planting Text Field (Simplified) ---
            OutlinedTextField(
                value = cropInfoState.plantingDate,
                onValueChange = { cropInfoState = cropInfoState.copy(plantingDate = it) },
                label = { Text("Proposed Date of Planting") },
                placeholder = { Text("DD/MM/YYYY") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // --- Submission Button ---
            Button(
                onClick = {
                    // Simple validation
                    if (cropInfoState.crop.isEmpty() || cropInfoState.seedClass.isEmpty() ||
                        cropInfoState.variety.isEmpty() || cropInfoState.productionYear.isEmpty() ||
                        cropInfoState.plantingDate.isEmpty()) {
                        Toast.makeText(context, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Logic to handle submission (e.g., save to database, API call)
                        // For this example, we just show a toast and a success message.
                        Toast.makeText(context, "Data submitted for ${cropInfoState.crop}", Toast.LENGTH_LONG).show()
                        showSubmissionMessage = true
                        // Reset form state after successful submission
//                        cropInfoState = CropInfoState()

                        context.startActivity(Intent(context, SeedSourceActivity::class.java))

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Submit Crop Information")
            }

            // Submission Success Message (Animated)
            AnimatedVisibility(
                visible = showSubmissionMessage,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Text(
                        text = "Successfully submitted data!",
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
