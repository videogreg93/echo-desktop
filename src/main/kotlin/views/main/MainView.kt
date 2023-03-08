package views.main

import SignInView
import javafx.geometry.Orientation
import tornadofx.*

class MainView() : View() {

    private val userViewModel: MainViewModel by inject()
    private val controller: MainViewController by inject()

    init {
        title = "${userViewModel.currentUser.givenName} - ${userViewModel.currentTemplate.name}"
    }

    override val root = borderpane {
        prefWidth = 800.0
        prefHeight = 800.0
        top = vbox {
            menubar {
                menu("File") {
                    item("Save") {
                        isDisable = true
                    }
                    item("Sign Out") {
                        action {
                            close()
                            find(SignInView::class).openWindow()
                        }
                    }
                }
                menu("Edit") {
                    item("Copy")
                    item("Paste")
                }
            }
            hbox(spacing = 16) {
                style {
                    paddingHorizontal = 15
                    paddingVertical = 30
                }
                button("Record")
                button("Export") {
                    action {
                        controller.export(userViewModel.currentTemplate.inputs, userViewModel.currentTemplate.templateFile)
                    }
                }
            }
        }
        center = vbox {
            scrollpane(fitToWidth = true) {
                style {
                    paddingHorizontal = 30
                }
                form {
                    userViewModel.currentTemplate.inputs.forEach { voiceField ->
                        fieldset(labelPosition = Orientation.VERTICAL) {
                            field(voiceField.label) {
                                textfield(voiceField.text)
                            }
                        }
                    }
                }
            }
        }
    }
}