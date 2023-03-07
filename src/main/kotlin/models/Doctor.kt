package models

data class User(
    val username: String,
    val firstName: String,
    val lastName: String,
) {
    val givenName: String
        get() = "$firstName $lastName"

    val abbreviation: String
        get() = "${firstName[0]}${lastName[0]}".uppercase()
}
