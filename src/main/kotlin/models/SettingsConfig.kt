package models

import javafx.beans.property.SimpleListProperty
import kotlinx.serialization.Serializable
import tornadofx.*

@Serializable
data class SettingsConfig(
    val phrases: List<String> = observableListOf()
)
class SettingsConfigModel {
    val phrasesProperty = SimpleListProperty<String>()
    val phrases by phrasesProperty
}
