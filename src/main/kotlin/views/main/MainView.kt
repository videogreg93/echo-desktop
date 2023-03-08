package views.main

import javafx.geometry.Orientation
import tornadofx.*

class MainView() : View() {

    private val userViewModel: MainViewModel by inject()

    init {
        title = userViewModel.currentUser.givenName
    }

    override val root = borderpane {
        prefWidth = 800.0
        prefHeight = 800.0
        top = flowpane {
            style {
                paddingHorizontal = 15
                paddingVertical = 30
            }
            button("Record")
            button("Export") {
                action {
                    userViewModel.currentTemplate.inputs.forEach {
                        println(it.text)
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