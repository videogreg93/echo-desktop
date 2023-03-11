package views.main

import SignInView
import i18n.Messages
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextField
import javafx.scene.layout.Region
import javafx.stage.FileChooser
import managers.text.text
import models.VoiceField.Size.*
import tornadofx.*
import views.settings.SettingsView
import java.awt.Desktop

// TODO init speech manager with run async
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
                    item(Messages.settingsTitle.text) {
                        action {
                            openInternalWindow(find<SettingsView>())
                        }
                    }
                }
                menu("Edit") {
                    item("Record")
                }
            }
            hbox(spacing = 16) {
                style {
                    paddingHorizontal = 15
                    paddingVertical = 30
                }
                button(controller.recordingButtonText) {
                    // Keep focus on textfield when clicking on this button.
                    this.focusTraversableProperty().value = false
                    shortcut("Ctrl+R")
                    action {
                        userViewModel.startingText.value = controller.selectedTextField.text
                        controller.onRecordButtonClicked()
                    }
                }
                button("Export") {
                    shortcut("Ctrl+E")
                    disableWhen(controller.isRecording)
                    action {
                        val fileName = userViewModel.currentTemplate.inputs.firstOrNull { it.isFileName }?.let {
                            it.text.value + ".docx"
                        } ?: "generated.docx"
                        val outputFile = chooseFile(
                            "Save",
                            filters = arrayOf(
                                FileChooser.ExtensionFilter("Docx file", "*.docx")
                            ),
                            mode = FileChooserMode.Save
                        ) {
                            this.initialFileName = fileName
                        }.firstOrNull()
                        if (outputFile != null) {
                            runAsync {
                                controller.export(
                                    outputFile,
                                    userViewModel.currentTemplate.inputs,
                                    userViewModel.currentTemplate.templateFile
                                )
                            } ui { file ->
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
                combobox(values = controller.microphoneInputs, property = controller.selectedMicrophoneInput) {
                    visibleWhen(controller.hasMultipleMicrophones)
                }
            }
        }
        center = vbox {
            scrollpane(fitToWidth = true) {
                style {
                    paddingHorizontal = 30
                }
                form {
                    userViewModel.currentTemplate.inputs.forEachIndexed { index, voiceField ->
                        fieldset(labelPosition = Orientation.VERTICAL) {
                            field(voiceField.label) {
                                val tf = textfield(voiceField.text) {
                                    when (voiceField.size) {
                                        SMALL -> {
                                            /* no-op */
                                        }
                                        MEDIUM -> {
                                            setPrefSize(prefWidth, 128.0)
                                            prefHeight(Region.USE_COMPUTED_SIZE)
                                            alignment = Pos.TOP_LEFT
                                        }
                                        LARGE -> {
                                            setPrefSize(prefWidth, 170.0)
                                            prefHeight(Region.USE_COMPUTED_SIZE)
                                            alignment = Pos.TOP_LEFT
                                        }
                                    }
                                }
                                if (index == 0) {
                                    controller.selectedTextField = tf
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    init {
        title = "${userViewModel.currentUser.givenName} - ${userViewModel.currentTemplate.name}"
        controller.onChangeInputCallback = ::setupSpeechCallbacks
    }

    override fun onBeforeShow() {
        super.onBeforeShow()
        root.scene.focusOwnerProperty().onChange {
            (it as? TextField)?.let {
                userViewModel.startingText.value = it.text
                controller.selectedTextField = it
            }
        }
        setupSpeechCallbacks()
    }

    private fun setupSpeechCallbacks() {
        controller.speechManager.addRecognizingListener {
            controller.selectedTextField.text = userViewModel.startingText.value + it
        }
        controller.speechManager.addRecognizedListener {
            val newText = userViewModel.startingText.value + it
            controller.selectedTextField.text = newText
            userViewModel.startingText.value = newText
        }
        println("Speech Callbacks set.")
    }
}