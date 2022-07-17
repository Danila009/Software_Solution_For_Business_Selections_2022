package bot

import MainViewModel
import api.getRandomString
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.polls.PollType
import common.dispatchersIO
import kotlinx.coroutines.flow.onEach

private const val KEY = "5505723005:AAELXyDdUzxaBLlO0mOEBAQSx5ayLqEHvG4"

class TelegramBot(
    private val viewModel: MainViewModel
) {

    private var chatId:ChatId.Id? = null

    fun create():Bot{
        return bot {

            token = KEY

            dispatch {

                setUpCallbacks()

                command("start"){

                    chatId = ChatId.fromId(message.chat.id)

                    val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = "new user",
                                callbackData = "new_user"
                            ),
                            InlineKeyboardButton.CallbackData(
                                text = "All Employees",
                                callbackData = "get_all_employees"
                            ),
                            InlineKeyboardButton.CallbackData(
                                text = "Poll",
                                callbackData = "poll"
                            )
                        )
                    )

                    bot.sendMessage(
                        chatId = chatId!!,
                        text = "Hello",
                        replyMarkup = inlineKeyboardMarkup
                    )
                }

                pollAnswer {
                }
            }
        }
    }

    private fun Dispatcher.setUpCallbacks() {
        callbackQuery(callbackData = "new_user"){
            val fioMessage = bot.sendMessage(chatId = chatId!!, text = "Виддите ФИО")

            fioMessage.fold({
                text {

                    val fio = this.text

                    viewModel.getEmployeesCheckById(fio)
                    viewModel.responseEmployeesCheckById.onEach { response ->
                        response?.let {
                            if (it){
                                val password = getRandomString(6)
                                val passwordMessage = bot.sendMessage(chatId = chatId!!, text = "" +
                                        "Ввидите: $password")
                                passwordMessage.fold({
                                    text {
                                        if (password == this.text){
                                            bot.sendMessage(chatId = chatId!!, text = "ВЫ зарегистрированы")
                                        }else {
                                            bot.sendMessage(chatId = chatId!!, text = "не правильно (")
                                        }
                                    }
                                },{

                                })
                            }else {
                                bot.sendMessage(chatId = chatId!!, text = "User нет (")
                            }
                        }
                    }.dispatchersIO()
                }
            },{

            })
        }

        callbackQuery(callbackData = "get_all_employees") {
            viewModel.getAllEmployees()
            viewModel.responseEmployees.onEach { response ->
                repeat(response.size){ index ->
                    val item = response[index]
                    bot.sendMessage(
                        chatId = chatId!!,
                        text = """
                            ${item.firstName} ${item.lastName} ${item.lastPatronomic}
                            Phone: ${item.phone}
                            Post: ${item.post.name}
                        """.trimIndent()
                    )
                }
            }.dispatchersIO()
        }

        callbackQuery("poll") {
            bot.sendPoll(
                chatId = chatId!!,
                type = PollType.QUIZ,
                question = "Java or Kotlin?",
                options = listOf("Java", "Kotlin"),
                correctOptionId = 1,
                isAnonymous = false
            )
        }
    }
}