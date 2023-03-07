package views.main

import tornadofx.*

class MainView() : View() {

    val userViewModel: MainViewModel by inject()

    init {
        title = userViewModel.currentUser.givenName
    }

    override val root = vbox {

    }
}