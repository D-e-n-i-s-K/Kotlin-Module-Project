import java.util.Scanner
import kotlin.system.exitProcess

val menu = MenuNavigation()
fun main() {


    menu.showViewMenu(MenuArchiveCollectionView())
}


class MenuArchiveCollectionView : ViewInterface {
    override val title: String = "Список Архивов:"
    override val mapOfMenu: MutableMap<String, () -> Unit> = mutableMapOf()

    override fun prepareToView() {
        mapOfMenu["Создать архив"] = {MenuNavigation().showCreateMenu(MenuCreateArchive())}
        for (key in NoteArchiveCollection.noteArchiveCollection.keys) {
            mapOfMenu[NoteArchiveCollection.noteArchiveCollection[key]!!.name] = {menu.showViewMenu(MenuViewArchive(key))}
        }
        mapOfMenu["Выход"] = { exitProcess(0) }
    }

}

interface CreateInterface {
    val title: String
    var tempData: String
    val backToMenu: () -> Unit

    fun createFromInput ()
}

class MenuCreateArchive : CreateInterface {
    override val title = "Введите название Архива:"
    override var tempData = ""
    override val backToMenu = {menu.showViewMenu(MenuArchiveCollectionView())}

    override fun createFromInput() {
        println(title)
        tempData = menu.getUserInput()
        NoteArchiveCollection.addToCollection(tempData)
        backToMenu.invoke()
    }

}

class MenuCreateNote (private val key: Int) : CreateInterface {
    override val title = "Введите текст заметки"
    override var tempData = ""
    override val backToMenu: () -> Unit = {menu.showViewMenu(MenuViewArchive(key))}

    override fun createFromInput() {
        println(title)
        tempData = menu.getUserInput()
        NoteArchiveCollection.noteArchiveCollection[key]!!.addToNoteArchive(tempData)
//        println("Создана заметка: ")
//        println(tempData)
        backToMenu.invoke()
    }
}

class MenuNavigation {

    fun showViewMenu (menuPage: ViewInterface) {

        menuPage.prepareToView()
        println(menuPage.title)
        var count = 0
        val tempMenu = mutableMapOf<Int, String>()

        for (element in menuPage.mapOfMenu) {
            count++
            tempMenu[count] = element.key
            println("$count. ${element.key}")

        }

        println("Введите пункт меню")

        var userInput: String = getUserInput()

        while (!checkUserInput(userInput, count)) {
            println("Введено неверное значение, введите число")
            for (key in tempMenu.keys) {
                println("$key. ${tempMenu[key]}")
            }
            userInput = getUserInput()
        }

        menuPage.mapOfMenu[tempMenu[userInput.toInt()]]?.invoke()
    }


    private fun checkUserInput(userInput: String, count: Int): Boolean {
        return ((isInputInt(userInput)) && (userInput.toInt() in 0..count) ) // && (userInput != "0")
    }

    private fun isInputInt(str: String): Boolean {
        return if (str.isEmpty() && str.length <= 3) false else str.all { Character.isDigit(it) }
    }

    fun showCreateMenu (menuPage: CreateInterface) {
        menuPage.createFromInput()
    }

    fun getUserInput () : String {
        return Scanner(System.`in`).nextLine()
    }


}

class MenuViewArchive (val key: Int) : ViewInterface {
    override val title = "Список Заметок:"
    override val mapOfMenu: MutableMap<String, () -> Unit> = mutableMapOf()

    override fun prepareToView () {
        val tempNoteArchive: NoteArchive? = NoteArchiveCollection.noteArchiveCollection[key]

        mapOfMenu["Создать Заметку"] = {menu.showCreateMenu(MenuCreateNote(key))}
        for (key in tempNoteArchive?.noteArchive!!.keys) {
            mapOfMenu[tempNoteArchive.noteArchive[key]!!.name] = {menu.showCreateMenu(MenuViewNote(this.key,
                tempNoteArchive.noteArchive[key]!!.name))}
        }
        mapOfMenu["Выход"] = {menu.showViewMenu(MenuArchiveCollectionView())}
    }
}

class MenuViewNote (val key: Int, val text: String) : CreateInterface {
    override val title = "Введите 1 для выхода"
    override var tempData = ""
    override val backToMenu: () -> Unit = {menu.showViewMenu(MenuViewArchive(key))}

    override fun createFromInput() {
        do {
            println("Текст заметки:")
            println(text)
            println("")
            println(title)
            tempData = menu.getUserInput()
        } while (tempData != "1")
        backToMenu.invoke()
    }



}

data class Note (
    val name: String
    //val textOfNote: String
) {

}

class NoteArchive(
    val name: String,
    val noteArchive: MutableMap<Int, Note> = mutableMapOf()
) {
    fun addToNoteArchive (str: String) {
        noteArchive[(noteArchive.size + 1)] = Note(str)
    }

}

object NoteArchiveCollection {

    val noteArchiveCollection: MutableMap<Int, NoteArchive> = mutableMapOf()

    fun addToCollection (name: String) {
        noteArchiveCollection[noteArchiveCollection.size] = NoteArchive(name)
    }

}

interface ViewInterface {
    val title: String
    val mapOfMenu: MutableMap<String, () -> Unit>
    fun prepareToView ()
}