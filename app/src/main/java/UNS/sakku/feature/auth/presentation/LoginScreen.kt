package uns.sakku.feature.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Import viewModel
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.core.Routes
import uns.sakku.core.LocalBackStack

/**
 * Stateful Composable: Bertanggung jawab atas ViewModel dan Side Effects
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel()
) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            viewModel.resetState()
            Toast.makeText(context, "Login Berhasil", Toast.LENGTH_SHORT).show()

            // Navigasi ke Dashboard. Dashboard akan otomatis membaca status login dari Repository!
            backStack.add(Routes.DashboardRoute)
        }

        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
    }

    HalamanAuth(
        onLoginClick = viewModel::login,
        onRegisterClick = viewModel::register
    )
}

/**
 * Stateless Composable: Form input UI yang buta terhadap logika validasi.
 */
@Composable
fun HalamanAuth(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: (String, String, String) -> Unit,
    composeIsLoginMode: Boolean = true
) {
    // UI State (transient state) disimpan di Composable
    var isLoginMode by remember { mutableStateOf(composeIsLoginMode) }
    var namaLengkap by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isLoginMode) "Welcome Back" else "Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = if (isLoginMode) "Silakan login untuk melanjutkan" else "Daftar untuk mulai mengelola klip", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), textAlign = TextAlign.Center)
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
            onClick = {
                // Semua validasi kini diurus oleh ViewModel!
                // UI hanya bertugas mengirim apa yang diketik pengguna.
                if (isLoginMode) {
                    onLoginClick(email, password)
                } else {
                    onRegisterClick(namaLengkap, email, password)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(text = if (isLoginMode) "Login" else "Register", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = if (isLoginMode) "Belum punya akun? " else "Sudah punya akun? ", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontSize = 14.sp)
            Text(text = if (isLoginMode) "Daftar di sini" else "Login di sini", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { isLoginMode = !isLoginMode; email = ""; password = ""; namaLengkap = "" })
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, name = "Light Mode - Login")
@Composable
fun PreviewAuthScreenLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanAuth(onLoginClick = { _, _ -> }, onRegisterClick = { _, _, _ -> })
    }
}

@Preview(showBackground = true, name = "Dark Mode - Login")
@Composable
fun PreviewAuthScreenDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanAuth(onLoginClick = { _, _ -> }, onRegisterClick = { _, _, _ -> })
    }
}

@Preview(showBackground = true, name = "Light Mode - Register")
@Composable
fun PreviewAuthScreenRegisterLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanAuth(composeIsLoginMode = false, onLoginClick = { _, _ -> }, onRegisterClick = { _, _, _ -> })
    }
}

@Preview(showBackground = true, name = "Dark Mode - Register")
@Composable
fun PreviewAuthScreenRegisterDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanAuth(composeIsLoginMode = false, onLoginClick = { _, _ -> }, onRegisterClick = { _, _, _ -> })
    }
}