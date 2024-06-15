package org.d3if3156.skyroom.ui.screen

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if3156.skyroom.BuildConfig
import org.d3if3156.skyroom.R
import org.d3if3156.skyroom.model.User
import org.d3if3156.skyroom.navigation.Screen
import org.d3if3156.skyroom.network.UserDataStore
import org.d3if3156.skyroom.ui.theme.SKYROOMTheme

@Composable
fun LoginScreen(navController: NavHostController, userDataStore: UserDataStore) {
    val coroutineScope = rememberCoroutineScope()
    val poppinsblack = FontFamily(Font(R.font.poppinsblack))
    val poppinsregular = FontFamily(Font(R.font.poppinsregular))

    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())
    val isLoggedIn by userDataStore.isLoggedInFlow.collectAsState(false)

    if (isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    var rotation by remember { mutableStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 1000) // Adjust the duration as needed
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.app_name),
                fontFamily = poppinsblack,
                style = TextStyle(color = Color.Black, fontSize = 50.sp)
            )
            Text(text = stringResource(R.string.masuk_dengangoogle),
                fontFamily = poppinsregular,
                style = TextStyle(color = Color.Black, fontSize = 16.sp)
            )
            Icon(
                painter = painterResource(R.drawable.icons8_google),
                contentDescription = stringResource(R.string.masuk_dengangoogle),
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(48.dp)
                    .rotate(animatedRotation)
                    .clickable {
                        coroutineScope.launch {
                            rotation += 360f
                            if (user.email.isEmpty()) {
                                val result = withContext(Dispatchers.IO) {
                                    signIn(context, dataStore)
                                }
                                if (result) {
                                    navController.navigate(Screen.Home.route)
                                } else {
                                    Log.d("SIGN-IN", "Sign in failed")
                                }
                            } else {
                                Log.d("SIGN-IN", "User: $user")
                                navController.navigate(Screen.Home.route)
                            }
                        }
                    }
            )
        }
    }
}
private suspend fun signIn(context: Context, dataStore: UserDataStore): Boolean {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    return try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
        true
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
        false
    }
}
private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl), isLoggedIn = true) // Menambahkan parameter isLoggedIn
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}
@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun LoginPreview() {
    SKYROOMTheme {
        LoginScreen(rememberNavController(), UserDataStore(LocalContext.current))
    }
}