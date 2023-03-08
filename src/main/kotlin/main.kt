import tornadofx.*

class EchoApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch<MainApplication>(args)
        }
    }
}