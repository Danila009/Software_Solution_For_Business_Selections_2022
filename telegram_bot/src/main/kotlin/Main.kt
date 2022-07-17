import bot.TelegramBot

fun main(){

    val viewModel = MainViewModel()

    val bot = TelegramBot(
        viewModel
    ).create()

    bot.startPolling()
}