package managers

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import runCommand
import java.io.File

/**
 * Uses an external executable to fetch device ids and names
 */
class AudioManager {

    fun getInputDevices(): List<InputDevice> {
        return try {
            "C:\\Users\\grego\\IdeaProjects\\echo-desktop\\getDeviceIds.exe".runCommand(File("C:\\Users\\grego\\IdeaProjects\\echo-desktop"))?.let {
                return if (it.isBlank()) {
                    emptyList()
                } else {
                    val list: List<InputDevice> = Json.decodeFromString(it)
                    println(list)
                    list
                }
            } ?: emptyList()
        } catch (e: Exception) {
            println("Could not get device ids, will use default input device.")
            e.printStackTrace()
            emptyList()
        }
    }

    fun getDefaultInputDevice(): InputDevice? {
        return getInputDevices().firstOrNull()
    }

    @Serializable
    data class InputDevice(val name: String, val id: String) {
        override fun toString(): String {
            return name
        }
    }
}