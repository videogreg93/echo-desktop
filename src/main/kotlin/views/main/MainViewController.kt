package views.main

import getResource
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.TextField
import managers.FileManager
import managers.speech.SpeechManager
import managers.speech.SpeechManagerImpl
import models.VoiceField
import org.apache.commons.io.FileUtils
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xwpf.usermodel.XWPFDocument
import tornadofx.*
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

class MainViewController : Controller() {

    val speechManager: SpeechManager = SpeechManagerImpl.instance
    var isRecording = SimpleBooleanProperty(false)
    val recordingButtonText = SimpleStringProperty("Record")
    val selectedMicrophoneInput = SimpleObjectProperty(speechManager.currentInputDevice).apply {
        onChange {
            it?.let { speechManager.setInputDevice(it) }
            onChangeInputCallback()
        }
    }
    val microphoneInputs = FXCollections.observableArrayList(speechManager.getSupportedInputDevices())
    val hasMultipleMicrophones = microphoneInputs.sizeProperty.booleanBinding {
        val size = (it?.toInt())
        size != null && size > 0
    }

    /**
     * The last text field the user clicked on. Voice recognized text will be applied here.
     */
    lateinit var selectedTextField: TextField

    var onChangeInputCallback: () -> Unit = {}

    fun onRecordButtonClicked() {
        isRecording.value = !isRecording.value
        recordingButtonText.value = if (isRecording.value) {
            speechManager.startContinuousRecognitionAsync()
            "Recording..."
        } else {
            speechManager.stopContinuousRecognitionAsync()
            "Record"
        }
    }

    fun export(output: File, inputs: List<VoiceField>, templateFile: String): File {
        replaceIdsInDocument(
            inputs = inputs.map {
                it.id to it.text.value
            },
            input = File("resources", templateFile),
            output = FileOutputStream(output)
        )
        return output
    }

    private fun replaceIdsInDocument(inputs: List<Pair<String, String>>, input: File, output: FileOutputStream) {
        try {
            /**
             * if uploaded doc then use HWPF else if uploaded Docx file use
             * XWPFDocument
             */
            // Create folders and copy documents
            if (!FileManager.mainFolder.toFile().exists()) Files.createDirectories(FileManager.mainFolder)
            if (!FileManager.template.exists()) {
                getResource("protocolOperatoireTemplate.docx")?.let {
                    FileUtils.copyInputStreamToFile(it, FileManager.template)
                }
            }
            if (!FileManager.BEMtemplate.exists()) {
                getResource("BEMTemplate.docx")?.let {
                    FileUtils.copyInputStreamToFile(it, FileManager.BEMtemplate)
                }
            }
            if (!FileManager.BEMtemplate.exists()) {
                getResource("BEMTemplate.docx")?.let {
                    FileUtils.copyInputStreamToFile(it, FileManager.BEMtemplate)
                }
            }
            val doc = XWPFDocument(
                OPCPackage.open(input)
            )
            for (p in doc.paragraphs) {
                val runs = p.runs
                if (runs != null) {
                    for (r in runs) {
                        var text = r.getText(0)
                        inputs.forEach { input ->
                            val key = input.first
                            if (text != null && text.contains(key)) {
                                text = text.replace(key, input.second)
                                r.setText(text, 0)
                            }
                        }
                    }
                }
            }
            for (tbl in doc.tables) {
                for (row in tbl.rows) {
                    for (cell in row.tableCells) {
                        for (p in cell.paragraphs) {
                            for (r in p.runs) {
                                var text = r.getText(0)
                                inputs.forEach { input ->
                                    val key = input.first
                                    if (text != null && text.contains(key)) {
                                        text = text.replace(key, input.second)
                                        r.setText(text, 0)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            doc.write(output)
            output.close()
        } finally {
        }
    }
}