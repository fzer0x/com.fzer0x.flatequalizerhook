package com.fzer0x.flatequalizerhook

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fzer0x.flatequalizerhook.ui.theme.CardBackground
import com.fzer0x.flatequalizerhook.ui.theme.DarkBackground
import com.fzer0x.flatequalizerhook.ui.theme.DeepSkyBlue
import com.fzer0x.flatequalizerhook.ui.theme.FlatequalizerhookTheme

class MainActivity : ComponentActivity() {

    private fun isModuleActive(): Boolean = false

    private fun getTargetAppVersion(): String {
        return try {
            val pInfo = packageManager.getPackageInfo("com.jazibkhan.equalizer", 0)
            pInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "Not Installed"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlatequalizerhookTheme {
                MainScreen(
                    isModuleActive = isModuleActive(),
                    targetAppVersion = getTargetAppVersion()
                )
            }
        }
    }
}

@Composable
fun MainScreen(isModuleActive: Boolean, targetAppVersion: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = DeepSkyBlue,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Flat Equalizer Hook by fzer0x",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    StatusIndicator(isActive = isModuleActive)

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow("Module Status", if (isModuleActive) "Active" else "Inactive")
                    InfoRow("Target App Version", targetAppVersion)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isModuleActive) {
                    "The module is active and should be working."
                } else {
                    "Module is inactive. Please activate it in LSPosed and reboot your device."
                },
                color = if (isModuleActive) Color.Green else Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun StatusIndicator(isActive: Boolean) {
    val color = if (isActive) Color.Green else Color.Red
    val text = if (isActive) "ACTIVE" else "INACTIVE"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color)
                .border(2.dp, Color.White, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp)
        Text(text = value, color = DeepSkyBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
