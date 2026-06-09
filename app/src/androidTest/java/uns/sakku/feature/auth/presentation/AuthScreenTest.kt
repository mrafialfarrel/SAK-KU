package uns.sakku.feature.auth.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uns.sakku.feature.auth.presentation.components.HalamanAuth

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    // Rule untuk Compose UI Testing
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun modeDefaultAdalahLoginDanMenampilkanElemenYangBenar() {
        composeTestRule.setContent {
            HalamanAuth(
                onLoginClick = { _, _ -> },
                onRegisterClick = { _, _, _ -> }
            )
        }

        // Cek judul dan teks
        composeTestRule.onNodeWithText("Welcome Back").assertIsDisplayed()

        // Cek field input yang harusnya ada di mode Login
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()

        // Pastikan field "Nama Lengkap" TIDAK ADA di mode Login
        composeTestRule.onNodeWithText("Nama Lengkap").assertDoesNotExist()
    }

    @Test
    fun klikTeksDaftarMengubahModeMenjadiRegister() {
        composeTestRule.setContent {
            HalamanAuth(
                onLoginClick = { _, _ -> },
                onRegisterClick = { _, _, _ -> }
            )
        }

        // Aksi: Klik teks "Daftar di sini"
        composeTestRule.onNodeWithText("Daftar di sini").performClick()

        // Validasi: UI harus berubah ke mode Register
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nama Lengkap").assertIsDisplayed() // Field ini harus muncul
        composeTestRule.onNodeWithText("Register").assertIsDisplayed() // Tombol berubah
    }

    @Test
    fun klikLoginMemicuCallbackDenganEmailDanPassword() {
        var callbackTerpanggil = false
        var inputEmail = ""
        var inputPassword = ""

        composeTestRule.setContent {
            HalamanAuth(
                onLoginClick = { email, password ->
                    callbackTerpanggil = true
                    inputEmail = email
                    inputPassword = password
                },
                onRegisterClick = { _, _, _ -> }
            )
        }

        // Simulasi mengetik di text field
        composeTestRule.onNodeWithText("Email").performTextInput("test@sakku.com")
        composeTestRule.onNodeWithText("Password").performTextInput("Rahasia123!")

        // Simulasi klik tombol
        composeTestRule.onNodeWithText("Login").performClick()

        // Validasi: Pastikan callback benar-benar berjalan dan membawa data yang diketik
        assertTrue(callbackTerpanggil)
        assertEquals("test@sakku.com", inputEmail)
        assertEquals("Rahasia123!", inputPassword)
    }

    @Test
    fun klikRegisterMemicuCallbackDenganSemuaDataLengkap() {
        var callbackTerpanggil = false
        var inputNama = ""
        var inputEmail = ""
        var inputPassword = ""

        composeTestRule.setContent {
            // Kita inisialisasi awal UI agar langsung berada di mode Register (false)
            HalamanAuth(
                composeIsLoginMode = false,
                onLoginClick = { _, _ -> },
                onRegisterClick = { nama, email, password ->
                    callbackTerpanggil = true
                    inputNama = nama
                    inputEmail = email
                    inputPassword = password
                }
            )
        }

        // Simulasi mengetik di text field
        composeTestRule.onNodeWithText("Nama Lengkap").performTextInput("Budi Santoso")
        composeTestRule.onNodeWithText("Email").performTextInput("budi@sakku.com")
        composeTestRule.onNodeWithText("Password").performTextInput("Budi1234!")

        // Simulasi klik tombol
        composeTestRule.onNodeWithText("Register").performClick()

        // Validasi
        assertTrue(callbackTerpanggil)
        assertEquals("Budi Santoso", inputNama)
        assertEquals("budi@sakku.com", inputEmail)
        assertEquals("Budi1234!", inputPassword)
    }
}