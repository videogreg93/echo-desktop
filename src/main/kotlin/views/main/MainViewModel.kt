package views.main

import javafx.beans.property.SimpleStringProperty
import models.Template
import models.User
import tornadofx.*

class MainViewModel(
    val currentUser: User,
    val currentTemplate: Template,
) : ViewModel() {
    var startingText = SimpleStringProperty()
}