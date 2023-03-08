package views.main

import SignInView
import javafx.geometry.Orientation
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextField
import javafx.scene.layout.Region
import tornadofx.*
import java.awt.Desktop

class MainView() : View() {

    private val userViewModel: MainViewModel by inject()
    private val controller: MainViewController by inject()

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
                button(controller.recordingButtonText) {
                    action {
                        controller.onRecordButtonClicked()
                    }
                }
                button("Export") {
                    disableWhen(controller.isRecording)
                    action {
                        val file = controller.export(
                            userViewModel.currentTemplate.inputs,
                            userViewModel.currentTemplate.templateFile
                        )
                        if (file != null) {
                            val alert = Alert(
                                Alert.AlertType.CONFIRMATION,
                                "File saved at ${file.absoluteFile}. Would you like to open the file?",
                                ButtonType.OK, ButtonType.CANCEL
                            ).apply {
                                headerText = "Export successful!"
                                isResizable = true
                                dialogPane.setPrefSize(400.0, 200.0)
                                dialogPane.minHeight(Region.USE_PREF_SIZE)
                            }
                            val buttonClicked = alert.showAndWait()
                            if (buttonClicked.isPresent && buttonClicked.get() == ButtonType.OK) {
                                Desktop.getDesktop().open(file)
                            }
                        }
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

    init {
        title = "${userViewModel.currentUser.givenName} - ${userViewModel.currentTemplate.name}"
    }

    override fun onBeforeShow() {
        super.onBeforeShow()
        root.scene.focusOwnerProperty().onChange {
            (it as? TextField)?.let {
                userViewModel.startingText.value = it.text
            }
        }
        controller.speechManager.addRecognizingListener {
            (root.scene.focusOwner as? TextField)?.text = userViewModel.startingText.value + it
        }
        controller.speechManager.addRecognizedListener {
            val newText = userViewModel.startingText.value + it
            (root.scene.focusOwner as? TextField)?.text = newText
            userViewModel.startingText.value = newText
        }
    }
}