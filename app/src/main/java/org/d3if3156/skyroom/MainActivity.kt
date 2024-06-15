package org.d3if3156.skyroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.d3if3156.skyroom.navigation.SetupNavGraph
import org.d3if3156.skyroom.network.UserDataStore
import org.d3if3156.skyroom.ui.theme.SKYROOMTheme

class MainActivity : ComponentActivity() {
    private lateinit var userDataStore: UserDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDataStore = UserDataStore(applicationContext)

        setContent {
            SKYROOMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupNavGraph(userDataStore = userDataStore)
                }
            }
        }
    }
}