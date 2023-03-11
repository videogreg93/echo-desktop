package views.settings

import javafx.beans.property.Property
import managers.settings.SettingsManager
import models.SettingsConfig
import models.SettingsConfigModel
import tornadofx.*

class SettingsViewModel : ItemViewModel<SettingsConfigModel>() {

    val phrases = bind(SettingsConfigModel::phrasesProperty)

    override fun onCommit(commits: List<Commit>) {
        SettingsManager.settings.value = SettingsConfig(phrases)
    }

    private fun <T> List<Commit>.findChanged(ref: Property<T>): Pair<T, T>? {
        val commit = find { it.property == ref && it.changed }
        return commit?.let { (it.newValue as T) to (it.oldValue as T) }
    }
}