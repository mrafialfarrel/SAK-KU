package uns.sakku.feature.auth.presentation

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.core.Routes
import uns.sakku.core.LocalBackStack
import uns.sakku.ui.theme.ThemeMode
import uns.sakku.feature.auth.presentation.components.HalamanAuth

/**
 * Stateful Composable: Bertanggung jawab atas ViewModel dan Side Effects
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel()
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
        onRegisterClick = viewModel::register,
    )
}
// --- PREVIEW ---
@Preview(showBackground = true, name = "Light Mode - Login")
@Composable
fun PreviewAuthScreenLight() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        HalamanAuth(
            onLoginClick = { _, _ -> }, onRegisterClick = { _, _, _ -> },
            composeIsLoginMode = true
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode - Login")
@Composable
fun PreviewAuthScreenDark() {
    FinanceAppTheme(ThemeMode.DARK) {
        HalamanAuth(onLoginClick = { _, _ -> }, onRegisterClick = { _, _, _ -> },
            composeIsLoginMode = true)
    }
}

@Preview(showBackground = true, name = "Light Mode - Register")
@Composable
fun PreviewAuthScreenRegisterLight() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        HalamanAuth(composeIsLoginMode = false, onLoginClick = { _, _ -> }, onRegisterClick = { _, _, _ -> },
            )
    }
}

@Preview(showBackground = true, name = "Dark Mode - Register")
@Composable
fun PreviewAuthScreenRegisterDark() {
    FinanceAppTheme(ThemeMode.DARK) {
        HalamanAuth(composeIsLoginMode = false, onLoginClick = { _, _ -> }, onRegisterClick = { _, _, _ -> },
            )
    }
}