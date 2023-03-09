import i18n.Messages
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.TextFormatter
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import managers.TemplateManager
import managers.UserManager
import managers.text.TextBoy
import managers.text.text
import tornadofx.*
import views.main.MainView
import views.main.MainViewModel


class SignInView : View() {
    val permitLength = 5
    val practiceNumber = SimpleStringProperty(this, "username", config.string("username"))
    val rememberMe = SimpleBooleanProperty(this, "rememberMe", config.boolean("rememberMe", false))
    val signInEnable: BooleanBinding = practiceNumber.length().eq(permitLength)
    val practiceNumberFilter: (TextFormatter.Change) -> Boolean = { change ->
        !change.isAdded || change.controlNewText.let {
            it.isInt() && it.length in 0..permitLength
        }
    }

    init {
        title = Messages.signIn.text
    }

    override val root = vbox {
        prefWidth = 400.0
        prefHeight = 200.0
        hbox(alignment = Pos.CENTER) {
            label(Messages.sign_in_title.text) {
                alignment = Pos.CENTER
                style {
                    fontWeight = FontWeight.BOLD
                    fontSize = Dimension(20.0, Dimension.LinearUnits.pt)
                }
            }
        }
        form {
            fieldset(labelPosition = Orientation.VERTICAL) {
                field(Messages.practiceNumberLabel.text) {
                    textfield(practiceNumber) {
                        filterInput(practiceNumberFilter)
                    }
                }
                field("") {
                    checkbox(Messages.rememberMe.text, rememberMe)
                }
            }
            button(Messages.signIn.text) {
                enableWhen(signInEnable)
                shortcut("Enter")
                action {
                    val user = UserManager().getUser(practiceNumber.get())
                    if (user != null) {
                        val file = chooseFile(
                            Messages.chooseATemplate.text,
                            filters = arrayOf(
                                FileChooser.ExtensionFilter("Echo file", "*.echo")
                            ),
                        ).firstOrNull()
                        if (file != null) {
                            // Sign in successful, get everything and go to main view
                            with(config) {
                                set("rememberMe" to rememberMe.value)
                                if (rememberMe.value) {
                                    set("username" to practiceNumber.value)
                                } else {
                                    set("username", "")
                                }
                                save()
                            }
                            val signinScope = Scope()
                            val model = MainViewModel(user, TemplateManager().loadTemplate(file))
                            setInScope(model, signinScope)
                            close()
                            find(MainView::class, signinScope).openWindow()
                        }
                    } else {
                        error(
                            Messages.genericError.text,
                            TextBoy.getMessage(Messages.errorNoUserFound, practiceNumber.get())
                        )
                    }
                }
            }
        }
    }
}