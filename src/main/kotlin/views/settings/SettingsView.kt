package views.settings

import i18n.Messages
import javafx.scene.control.TextInputDialog
import managers.settings.SettingsManager
import managers.text.text
import models.SettingsConfigModel
import tornadofx.*

class SettingsView : View(Messages.settingsTitle.text) {
    private val viewModel by inject<SettingsViewModel>()

    init {
        viewModel.item = SettingsConfigModel()
        viewModel.phrases.addAll(SettingsManager.loadSettings().phrases)
    }

    override val root = borderpane {
        center = vbox {
            listview(viewModel.phrases) {

            }
            button("+") {
                action {
                    val inputDialog = TextInputDialog("").apply {
                        headerText = "Enter a new word"
                    }
                    val result = inputDialog.showAndWait()
                    if (result.isPresent) {
                        viewModel.phrases.add(result.get())
                    }
                }
            }
        }

        bottom = hbox {
            button("apply") {
                action {
                    viewModel.commit()
                }
            }
            button("undo") {
                action {
                    viewModel.rollback()
                }
            }
        }
    }
}
