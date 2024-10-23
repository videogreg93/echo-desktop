package views.settings

import i18n.Messages
import javafx.geometry.Pos
import javafx.scene.control.TextInputDialog
import managers.settings.SettingsManager
import managers.text.text
import models.SettingsConfigModel
import tornadofx.*

class SettingsView : View(Messages.settingsTitle.text) {
    private val viewModel by inject<SettingsViewModel>()

    init {
        viewModel.item = SettingsConfigModel(SettingsManager.loadSettings().phrases)
    }

    override val root = borderpane {
        center = vbox {
            label(Messages.settingsPhrasesTitle.text) {
                alignment = Pos.CENTER
                style {
                    fontSize = Dimension(16.0, Dimension.LinearUnits.pt)
                }
            }
            val listView = listview(viewModel.phrases)
            hbox {
                button("+") {
                    action {
                        val inputDialog = TextInputDialog("").apply {
                            headerText = "Enter a new word"
                        }
                        val result = inputDialog.showAndWait()
                        if (result.isPresent) {
                            viewModel.phrases.value =
                                viewModel.phrases.toMutableList().apply { add(result.get()) }.toObservable()
//                        viewModel.phrases.add(result.get())
                        }
                    }
                }
                button("-") {
                    enableWhen(listView.selectionModel.selectedItems.sizeProperty.eq(1))
                    action {
                        listView.selectionModel.selectedItem?.let {
                            viewModel.phrases.value =
                                viewModel.phrases.toMutableList().apply { remove(it) }.toObservable()
                        }
                    }
                }
            }
        }

        bottom = hbox {
            button("apply") {
                enableWhen(viewModel.dirty)
                action {
                    viewModel.commit()
                }
            }
            button("undo") {
                enableWhen(viewModel.dirty)
                action {
                    viewModel.rollback()
                }
            }
        }
    }
}
