package managers

import models.User

class UserManager {
    // Dummy users
    private val users = listOf(
        User("11223", "Test", "User"),
        User("11111", "AnotherTest", "User"),
        User("04260", "Karl", "Fournier"))

    fun getUser(permit: String): User? {
        return users.firstOrNull { it.username == permit }
    }
}