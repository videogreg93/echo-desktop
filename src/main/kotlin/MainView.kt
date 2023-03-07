import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.TextFormatter
import javafx.scene.text.FontWeight
import tornadofx.*


class MyView1 : View() {
    val practiceNumber = SimpleStringProperty()
    val rememberMe = SimpleBooleanProperty()
    val signInEnable: BooleanBinding = practiceNumber.length().eq(6)
    val practiceNumberFilter: (TextFormatter.Change) -> Boolean = { change ->
        !change.isAdded || change.controlNewText.let {
            it.isInt() && it.length in 0..6
        }
    }
    override val root = vbox {
        hbox(alignment = Pos.CENTER) {
            label("Sign In") {
                alignment = Pos.CENTER
                style {
                    fontWeight = FontWeight.BOLD
                    fontSize = Dimension(20.0, Dimension.LinearUnits.pt)
                }
            }
        }
        form {
            fieldset(labelPosition = Orientation.VERTICAL) {
                field("Practice Number") {
                    textfield(practiceNumber) {
                        filterInput(practiceNumberFilter)
                    }
                }
                field("") {
                    checkbox("Remember Me", rememberMe)
                }
            }
            button("Sign In") {
                enableWhen(signInEnable)
            }
        }
    }
}