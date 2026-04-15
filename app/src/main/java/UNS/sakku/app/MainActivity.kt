package uns.sakku.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uns.sakku.UtamaActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HalamanAuth(
                    onAuthSuccess = {
                        // Menggunakan INTENT untuk pindah ke UtamaActivity
                        val intent = Intent(this, UtamaActivity::class.java)
                        startActivity(intent)
                        finish() // finish() digunakan agar user tidak bisa 'back' ke halaman login setelah masuk
                    }
                )
            }
        }
    }
}

@Composable
fun HalamanAuth(onAuthSuccess: () -> Unit) {
    var isLoginMode by remember { mutableStateOf(true) }
    var namaLengkap by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isLoginMode) "Welcome Back" else "Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = if (isLoginMode) "Silakan login untuk melanjutkan" else "Daftar untuk mulai mengelola klip", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))

        if (!isLoginMode) {
            OutlinedTextField(value = namaLengkap, onValueChange = { namaLengkap = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAuthSuccess,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(text = if (isLoginMode) "Login" else "Register", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = if (isLoginMode) "Belum punya akun? " else "Sudah punya akun? ", color = Color.Gray, fontSize = 14.sp)
            Text(text = if (isLoginMode) "Daftar di sini" else "Login di sini", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { isLoginMode = !isLoginMode; email = ""; password = ""; namaLengkap = "" })
        }
    }
}

@Preview
@Composable
fun PreviewAuthScreen() {
    MaterialTheme {
        HalamanAuth(onAuthSuccess = {})
    }
}