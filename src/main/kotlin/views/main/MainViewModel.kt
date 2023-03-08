package views.main

import models.Template
import models.User
import tornadofx.*

class MainViewModel(
    val currentUser: User,
    val currentTemplate: Template,
    ): ViewModel() {
}