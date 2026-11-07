package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthManager
import com.example.ui.PigeonverseViewModel
import com.example.ui.screens.MainScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.theme.PigeonverseTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.example.util.configureAppCheck
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mandated by firebase-ai-logic-android: configure App Check before setContent, ViewModels or Firebase calls
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            PigeonverseTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        if (currentUser == null) {
            val credentialManager = CredentialManager.create(context)
            AuthManager.attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = { currentUser = Firebase.auth.currentUser },
                onUnauthenticated = { /* show sign in screen */ },
                scope = scope
            )
        }
    }

    if (currentUser == null) {
        SignInScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        val viewModel: PigeonverseViewModel = viewModel()
        MainScreen(
            viewModel = viewModel,
            onSignOut = {
                currentUser = null
            }
        )
    }
}
