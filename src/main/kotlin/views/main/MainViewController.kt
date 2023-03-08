package views.main

import getResource
import managers.FileManager
import models.VoiceField
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xwpf.usermodel.XWPFDocument
import tornadofx.*
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class MainViewController: Controller() {

    fun export(inputs: List<VoiceField>, templateFile: String) {
        val fileName = inputs.firstOrNull { it.isFileName }?.let {
            it.text.value + ".docx"
        } ?: "generated.docx"
        val fileChooser = JFileChooser(FileManager.myDocuments).apply {
            selectedFile = File(fileName)
            isAcceptAllFileFilterUsed = false
            val fileFilter = FileNameExtensionFilter("Only .docx files", "docx")
            addChoosableFileFilter(fileFilter)
        }
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            var output = fileChooser.selectedFile
            if (!FilenameUtils.getExtension(fileChooser.name).equals("docx", ignoreCase = true)) {
                output = File(
                    output.parentFile,
                    FilenameUtils.getBaseName(output.name) + ".docx"
                )
            }
            replaceIdsInDocument(
                inputs = inputs.map {
                    it.id to it.text.value
                },
                input = File(FileManager.mainFolder.toFile(), templateFile),
                output = FileOutputStream(output)
            )
        }
    }

    private fun replaceIdsInDocument(inputs: List<Pair<String,String>>, input: File, output: FileOutputStream) {
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