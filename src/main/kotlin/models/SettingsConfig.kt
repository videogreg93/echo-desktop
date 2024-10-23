package models

import javafx.beans.property.SimpleListProperty
import kotlinx.serialization.Serializable
import tornadofx.*

@Serializable
data class SettingsConfig(
    val phrases: List<String> = observableListOf()
)
class SettingsConfigModel(initialPhrases: List<String>) {
    val phrasesProperty = SimpleListProperty<String>(initialPhrases.toObservable())
    val phrases by phrasesProperty
}
