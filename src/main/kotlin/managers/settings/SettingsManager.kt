package managers.settings

import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import managers.FileManager
import models.SettingsConfig
import tornadofx.*
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

object SettingsManager {
    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }
    private val settingsFile by lazy {
        Paths.get(FileManager.mainFolder.absolutePathString(), "/settings.json").toFile()
    }

    val settings = SimpleObjectProperty(loadSettings()).apply {
        onChange { newSettings ->
            if (newSettings != null) {
                saveSettings(newSettings)
                println("Updated Settings")
            }
        }
    }

    fun loadSettings(): SettingsConfig {
        return if (settingsFile.exists()) {
            json.decodeFromString(settingsFile.readText())
        } else {
            SettingsConfig()
        }
    }

    fun saveSettings(config: SettingsConfig) {
        val configJson = json.encodeToString(config)
        settingsFile.writeText(configJson)
    }

}