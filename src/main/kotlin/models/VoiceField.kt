package models

import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class VoiceField(
    val label: String,
    val size: Size = Size.SMALL,
    val id: String,
    val isFileName: Boolean = false,
    val isUsername: Boolean = false,
    val isPermitNumber: Boolean = false,
    val isAbbreviation: Boolean = false,
    val isHidden: Boolean = false,
    @Transient
    val text: SimpleStringProperty = SimpleStringProperty(""),
) {
    enum class Size {
        SMALL, MEDIUM, LARGE
    }
}