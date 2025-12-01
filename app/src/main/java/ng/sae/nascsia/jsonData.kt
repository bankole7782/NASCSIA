package ng.sae.nascsia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable

@Serializable
data class UserData(val accessCode: String, val userName: String)


@Serializable
data class FieldDef(
    val state: String,
    val address: String,
    val mobileNumber: String,
    val plantingArea: Double,
    val latitude: Double,
    val longitude: Double
)
