package ng.sae.nascsia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class UserData(val accessCode: String, val userName: String)

data class PlanDef(
    val state: String,
    val address: String,
    val mobileNumber: String,
    val plantingArea: Double,
    val latitude: Double,
    val longitude: Double,
    val crop: String,
    val variety: String,
    val seedClass: String,
    val productionYear: String,
    val plantingDate: String,
    val srcQuantityProcured: String,
    val srcSeedClass: SeedClass,
    val srcSeedCodexNumber: String, // Optional field
    val srcSupplierName: String,
    val srcProductionYear: String
)
