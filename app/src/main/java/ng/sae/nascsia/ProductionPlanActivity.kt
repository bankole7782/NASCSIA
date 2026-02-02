package ng.sae.nascsia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import java.io.File

class ProductionPlanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
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
                .padding(24.dp),
//                .verticalScroll(scrollState), // Make the form scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "NASC SIA : Production Plan",
                fontSize = 30.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))
            val existingPlans = allPlans(context)

            if (existingPlans.isNotEmpty()) {
                LazyColumn(modifier= Modifier
//                    .verticalScroll(scrollState)
                ) {
                    items(existingPlans) { item ->
                        PPView(item)
                        Spacer(modifier=Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Add Button
            Button(
                onClick = {
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

@Composable
fun PPView(item: Map<String, String>) {
    val ctx = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxSize(),
        onClick = {
            val intent1 = Intent(ctx, ProductionPlanViewActivity::class.java)
            intent1.putExtra("planFileName", item["plan_name"])
            ctx.startActivity(intent1)
        },
    ) {
        Column(
        modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Date: " +  item["plan_name"]!!, fontSize = 20.sp)
            Text(text = "Crop: " + item["plan_crop"]!!)
            Text(text = "Address: " + item["plan_address"]!!)
            Text(text="Synced: false")
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

fun allPlans(context: Context) : List<Map<String, String>> {
    val ret: MutableList<Map<String, String>> = mutableListOf()
    val plansDir = File(context.getExternalFilesDir(""), "plans")
    if (! plansDir.exists() && ! plansDir.isDirectory) {
        return mutableListOf<Map<String, String>>()
    }

    val plans = plansDir.listFiles()
    for (plan in plans!!) {
        var planDetailsMap = deserializePlan(context, plan.name)

        var retMap: MutableMap<String, String> = mutableMapOf()
        retMap["plan_name"] = plan.name
        retMap["plan_address"] = planDetailsMap["address"].toString()
        retMap["plan_crop"] = planDetailsMap["crop"].toString()

        ret.add(retMap)
    }

    return ret
}
