package com.sensipro.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sensipro.app.data.AppContainer
import com.sensipro.app.navigation.SensiProNavGraph
import com.sensipro.app.settings.AppLanguage
import com.sensipro.app.settings.SettingsRepository
import com.sensipro.app.ui.MainViewModel
import com.sensipro.app.ui.theme.SensiProTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val language = readStoredLanguage(newBase)
        val locale = Locale(language.tag)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    private fun readStoredLanguage(context: Context): AppLanguage {
        return try {
            runBlocking {
                SettingsRepository(context.applicationContext).settingsFlow.first().language
            }
        } catch (e: Exception) {
            AppLanguage.TAJIK
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = AppContainer(applicationContext)

        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(applicationContext, container)
            )

            SensiProTheme {
                SensiProNavGraph(viewModel = viewModel)
            }
        }
    }
}
