package UNS.sakku

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Anggota(val nama: String, val nim: String, val githubUrl: String)

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HalamanProfile(
                    onNavigateBack = { finish() },
                    onLogout = {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun HalamanProfile(onNavigateBack: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current

    val daftarAnggota = listOf(
        Anggota("Jesayas Hutasoit", "L0124101", "https://github.com/JesayasHutasoit"),
        Anggota("Muhammad Raditya Boy Wiratama", "L0124109", "https://github.com/RadityaBoy"),
        Anggota("Muhammad Rafi Al-Farrel", "L0124110", "https://github.com/mrafialfarrel"),
        Anggota("Muhammad Rafian Surya Muqsith", "L0124111", "https://github.com/rafianmuqisth"),
        Anggota("Musyaffa Falih Suryono", "L0124113", "https://github.com/Master-FroSt")
    )

    Column(
        modifier = Modifier.padding(24.dp)
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Foto Profil / Logo Kelompok
        Box(
            modifier = Modifier.size(200.dp).clip(CircleShape).background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Kelompok\nSakku", textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bagian Daftar Anggota
        daftarAnggota.forEach { mhs ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TEXT NAMA (Hanya ini yang bisa diklik)
                Text(
                    text = mhs.nama,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(mhs.githubUrl)
                        }
                        context.startActivity(intent)
                    }
                )

                // Pemisah antar Nama dan NIM
                Text(text = " - ", fontSize = 14.sp, color = Color.Gray)

                // TEXT NIM (Tidak bisa diklik)
                Text(
                    text = mhs.nim,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "S1 Informatika, Angkatan 2024", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Halo! Kami adalah mahasiswa S1 Informatika yang memiliki minat di bidang RPL, Pemweb, dan Mobile Development.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))


        // Tombol Navigasi
        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth(0.8f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(text = "Kembali ke Beranda", color = Color.DarkGray, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onLogout) {
            Text(text = "Logout Akun", color = Color.Red)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHalamanProfile() {
    MaterialTheme {
        HalamanProfile(onNavigateBack = {}, onLogout = {})
    }
}