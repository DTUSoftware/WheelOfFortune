package dk.dtu.s215827.wheeloffortune

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import kotlin.random.Random

class PlayerViewModel : ViewModel() {
    val currentWord = MutableStateFlow("")
    val currentCategory = MutableStateFlow("")
    val currentPossibleEarning = MutableStateFlow(0)
    val revealedLetters = MutableStateFlow(emptyList<Char>())
    val lives = MutableStateFlow(0)
    val points = MutableStateFlow(0)
    val status = MutableStateFlow(0)

//    val alreadyPlayedWords = MutableStateFlow(emptyList<String>())

    val randomSeed = Random(System.currentTimeMillis())

    var wordsTotal = 0
    var wordsMap = HashMap<String, List<String>>()

    init {
        val wordFile = this::class.java.classLoader?.getResource("words.json")?.readText()
        val categories = wordFile?.let { JSONObject(it) }

        categories?.keys()?.forEach { key ->
            val categoryWords = categories.getJSONArray(key)
            wordsTotal += categoryWords.length()

            var categoryWordsList = emptyList<String>()
            var i = 0
            while (i < categoryWords.length()) {
                val word = categoryWords.get(i) as String
                categoryWordsList = categoryWordsList.plus(word)
                i++
            }
            wordsMap[key] = categoryWordsList
        }
    }

    fun newGame() {
        setLives(5)
        setPoints(0)
        setPlaying()
        newWord()
    }

    fun setNotPlaying() {
        this.status.value = 0
    }

    fun setPlaying() {
        this.status.value = 1
    }

    fun setWon() {
        this.status.value = 2
        checkDone()
    }

    fun setLost() {
        this.status.value = 3
        checkDone()
    }

    fun setDone() {
        this.status.value = 4
    }

    fun checkDone(): Boolean {
        if (wordsTotal <= 0) {
            setDone()
            return true
        }
        return false
    }

    fun newWord() {
        if (!checkDone()) {
            this.revealedLetters.value = emptyList()
            val category = wordsMap.keys.elementAt((0 until wordsMap.size).random(randomSeed))
            val words = wordsMap[category]

            if (!words.isNullOrEmpty()) {
                val word = words[(words.indices).random(randomSeed)]

                this.currentWord.value = word.uppercase()
                this.currentCategory.value = category
//                this.alreadyPlayedWords.value = this.alreadyPlayedWords.value.plus(this.currentWord.value)
                wordsMap[category] = words.minus(word)
                if (wordsMap[category]?.isEmpty() == true) {
                    wordsMap.remove(category)
                }
                wordsTotal--
            }
        }
    }

    fun guessChar(char: Char) {
        if (isCharInWord(char) && !isRevealed(char)) {
            revealChar(char)
            addPoints(currentPossibleEarning.value)
            setPossibleEarnings(0)

            if (revealedLetters.value.size == currentWord.value.replace(" ", "").toList()
                    .distinct().size
            ) {
                setWon()
            }
        } else {
            subtractLives(1)
        }
    }

    fun isCharInWord(char: Char): Boolean {
        // ignoreCase technically not needed now
        return this.currentWord.value.contains(char, ignoreCase = true)
    }

    fun isRevealed(char: Char): Boolean {
        return this.revealedLetters.value.contains(char)
    }

    fun revealChar(char: Char) {
        this.revealedLetters.value = this.revealedLetters.value.plus(char)
    }

    fun addPoints(points: Int) {
        this.points.value += points
    }

    fun subtractPoints(points: Int) {
        this.points.value -= points
    }

    fun setPoints(points: Int) {
        this.points.value = points
    }

    fun setPossibleEarnings(points: Int) {
        this.currentPossibleEarning.value = points
    }

    fun doBankruptcy() {
        this.points.value = 0
        setLost()
    }

    fun addLives(lives: Int) {
        this.lives.value += lives
    }

    fun subtractLives(lives: Int) {
        this.lives.value -= lives
        if (this.lives.value <= 0) {
            setLost()
        }
    }

    fun setLives(lives: Int) {
        this.lives.value = lives
    }

}