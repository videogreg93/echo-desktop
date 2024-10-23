package managers.speech

import com.microsoft.cognitiveservices.speech.CancellationReason
import com.microsoft.cognitiveservices.speech.PhraseListGrammar
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import managers.AudioManager
import managers.settings.SettingsManager
import org.hera.echo_desktop.BuildConfig

class SpeechManagerImpl(private val audioManager: AudioManager) : SpeechManager {

    private val subscriptionKey = BuildConfig.SPEECH_API_KEY
    private val regionCode = "eastus"

    private var recognizer: SpeechRecognizer
    override var currentInputDevice = audioManager.getDefaultInputDevice()

    init {
        println("Init SpeechManager")
        recognizer = setupSpeech()
    }

    private fun setupSpeech(): SpeechRecognizer {
        val speechConfig = SpeechConfig.fromSubscription(
            subscriptionKey,
            regionCode,
        )
        speechConfig.enableDictation()
        speechConfig.speechRecognitionLanguage = "fr-CA"
        val audioConfig = currentInputDevice?.let {
            AudioConfig.fromMicrophoneInput(it.id)
        } ?: AudioConfig.fromDefaultMicrophoneInput()
        val recognizer = SpeechRecognizer(speechConfig, audioConfig)
        recognizer.canceled.addEventListener { s, e ->
            println("CANCELED: Reason=" + e.getReason());

            if (e.getReason() == CancellationReason.Error) {
                println("CANCELED: ErrorCode=" + e.getErrorCode());
                println("CANCELED: ErrorDetails=" + e.getErrorDetails());
                println("CANCELED: Did you update the subscription info?");
            }
        }
        recognizer.sessionStopped.addEventListener { s, e ->
            println("\n    Session stopped event.")
        }
        recognizer.sessionStarted.addEventListener { s, e ->
            println("Session started")
        }
        recognizer.sessionStopped.addEventListener { s, e ->
            println("Session stopped")
        }
        return recognizer
    }

    override fun addRecognizingListener(listener: (String) -> Unit) {
        recognizer.recognizing.addEventListener { any, speechRecognitionEventArgs ->
            listener(speechRecognitionEventArgs.result.text)
        }
    }

    override fun addRecognizedListener(listener: (String) -> Unit) {
        recognizer.recognized.addEventListener { any, speechRecognitionEventArgs ->
            listener(speechRecognitionEventArgs.result.text)
        }
    }

    override fun startContinuousRecognitionAsync() {
        setPhraseList(SettingsManager.settings.value.phrases)
        println("Set phrases to ${SettingsManager.settings.value.phrases}")
        recognizer.startContinuousRecognitionAsync()
    }

    override fun stopContinuousRecognitionAsync() {
        recognizer.stopContinuousRecognitionAsync()
    }

    override fun getSupportedInputDevices(): List<AudioManager.InputDevice> {
        return audioManager.getInputDevices()
    }

    override fun setInputDevice(device: AudioManager.InputDevice) {
        currentInputDevice = device
        println("Changing device to $device")
        recognizer = setupSpeech()
    }

    override fun setPhraseList(phrases: List<String>) {
        val phraselist = PhraseListGrammar.fromRecognizer(recognizer)
        phrases.forEach {
            phraselist.addPhrase(it)
        }
    }

    companion object {
        val instance: SpeechManager by lazy {
            if (BuildConfig.SPEECH_ENABLED) {
                SpeechManagerImpl(AudioManager())
            } else {
                SpeechManagerAbstract()
            }
        }
    }
}