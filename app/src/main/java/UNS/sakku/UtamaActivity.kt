package UNS.sakku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

class UtamaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HalamanUtama(
                    onNavigateToProfile = {
                        // INTENT untuk pindah ke ProfileActivity
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun HalamanUtama(onNavigateToProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "SAK-KU",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(150.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center
        ) {
            Text(text = "Banner Klip Terbarumu", color = Color.Gray, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Halo, Selamat Datang!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Kelola semua klip videomu dengan mudah di sini. Pilih menu profil untuk melihat detail akun dan statistik perkembanganmu.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNavigateToProfile,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(text = "Go To Profile", color = Color.DarkGray, fontWeight = FontWeight.Bold)
        }


        Spacer(modifier = Modifier.height(16.dp))
        // 1. Ambil context dari Compose (Taruh di dalam fungsi @Composable, sebelum Button)
        val context = LocalContext.current

        // 2. Buat Button dengan penulisan onClick yang benar
        Button(
            onClick = {
                // Logika intent ditaruh di dalam kurung kurawal onClick
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = "https://github.com/mrafialfarrel/SAK-KU.git".toUri()
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(text = "Go To GitHub", color = Color.DarkGray, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHalamanUtama() {
    MaterialTheme {
        HalamanUtama(onNavigateToProfile = {})
    }
}
