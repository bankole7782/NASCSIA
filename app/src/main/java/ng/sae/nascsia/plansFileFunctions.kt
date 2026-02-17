package ng.sae.nascsia

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserData(val accessCode: String, val userName: String)

//data class PlanDef(
//    val staffName: String,
//    val state: String,
//    val address: String,
//    val mobileNumber: String,
//    val plantingArea: Double,
//    val latitude: Double,
//    val longitude: Double,
//    val crop: String,
//    val variety: String,
//    val seedClass: String,
//    val productionYear: String,
//    val plantingDate: String,
//    val srcQuantityProcured: String,
//    val srcSeedClass: SeedClass,
//    val srcSeedCodexNumber: String, // Optional field
//    val srcSupplierName: String,
//    val srcProductionYear: String
//)

fun getCurrentTimeString(): String {
    // Get the current date and time
    val currentTime = Date()

    // Define the desired date and time format
    val formatter = SimpleDateFormat("yyyy:M:dd_hh:mm:ss", Locale.getDefault())

    // Format the current time into a string
    return formatter.format(currentTime)
}

fun serializePlan(context: Context, inMap: MutableMap<String, Any>) {
    // prepare for address
    inMap["address"] = inMap["address"].toString().replace("\n", "____")
    var outStr: String = ""
    for ((key, value) in inMap) {
        outStr += "$key: $value\n"
    }

    val plansDir = File(context.getExternalFilesDir(""), "plans")
    plansDir.mkdirs()

    val planFileName = "plans/" + getCurrentTimeString() + ".txt"
    val planFile = File(context.getExternalFilesDir(""), planFileName)
    planFile.writeText(outStr)
}

fun deserializePlan(context: Context, planFileName: String?): Map<String, String> {
    var outMap: MutableMap<String, String> = mutableMapOf()
    val planFile = File(context.getExternalFilesDir(""), "plans/"+planFileName)
    val planFileText = planFile.readText()
    val planFileTextParts  = planFileText.split("\n")
    for (item in planFileTextParts) {
        val itemParts = item.split(":")
        if (itemParts.size == 2) {
            outMap[itemParts[0]] = itemParts[1].replace("____", "\n")
        }
    }
    return outMap
}

fun getDateStrFromFilename(inStr: String): String {
    var tmp = inStr.replace(".txt", "")
    tmp = tmp.replace("_", "  ")
    return tmp
}

fun getPhotoFile(context: Context, planPhotoName: String): File {
    val planPhotos = File(context.getExternalFilesDir(""), "plan_photos")
    val returnFile = File(planPhotos, planPhotoName.trim())
    return returnFile
}