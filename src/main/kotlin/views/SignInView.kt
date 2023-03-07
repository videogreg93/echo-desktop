import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.TextFormatter
import javafx.scene.text.FontWeight
import managers.UserManager
import tornadofx.*
import views.main.MainView
import views.main.MainViewModel


class SignInView : View() {
    val permitLength = 5
    val practiceNumber = SimpleStringProperty()
    val rememberMe = SimpleBooleanProperty()
    val signInEnable: BooleanBinding = practiceNumber.length().eq(permitLength)
    val practiceNumberFilter: (TextFormatter.Change) -> Boolean = { change ->
        !change.isAdded || change.controlNewText.let {
            it.isInt() && it.length in 0..permitLength
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
                action {
                    val user = UserManager().getUser(practiceNumber.get())
                    if (user != null) {
                        val signinScope = Scope()
                        val model = MainViewModel(user)
                        setInScope(model, signinScope)
                        close()
                        find(MainView::class, signinScope).openWindow()
                    } else {
                        error("Error", "No user found for #${practiceNumber.get()}")
                    }
                }
            }
        }
    }
}