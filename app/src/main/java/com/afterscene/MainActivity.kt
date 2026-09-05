package com.afterscene

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.afterscene.data.local.AppDatabase
import com.afterscene.data.repository.MediaRepository
import com.afterscene.data.repository.MoodRepository
import com.afterscene.ui.navigation.NavGraph
import com.afterscene.viewmodel.MediaViewModel
import com.afterscene.viewmodel.MoodViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Manual dependency injection
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = MediaRepository(database.mediaDao())
        val moodRepository = MoodRepository()

        setContent {
            val navController = rememberNavController()
            
            // ViewModel instantiations using our factories
            val viewModel: MediaViewModel = viewModel(
                factory = MediaViewModel.Factory(repository)
            )
            val moodViewModel: MoodViewModel = viewModel(
                factory = MoodViewModel.Factory(moodRepository)
            )

            // Dynamic Dark Theme custom-built for AfterScene
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF7E57C2),
                    secondary = Color(0xFFB39DDB),
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E),
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        moodViewModel = moodViewModel
                    )
                }
            }
        }
    }
}

