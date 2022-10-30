package dk.dtu.s215827.wheeloffortune

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import kotlin.random.Random

class WheelResult(var type: Int, var points: Int? = null) {
}

class PlayerViewModel : ViewModel() {
    val currentWord = MutableStateFlow("")
    val currentCategory = MutableStateFlow("")
    val currentPossibleEarning = MutableStateFlow(0)
    val revealedLetters = MutableStateFlow(emptyList<Char>())
    val lives = MutableStateFlow(0)
    val points = MutableStateFlow(0)
    val status = MutableStateFlow(0)
    val wheelPosition = MutableStateFlow(0f)
    val currentWheelResult = MutableStateFlow(WheelResult(-1))

    val wheelPositions = HashMap<Float, WheelResult>()

//    val alreadyPlayedWords = MutableStateFlow(emptyList<String>())

    val randomSeed = Random(System.currentTimeMillis())

    var wordsTotal = 0
    var wordsMap = HashMap<String, List<String>>()

    init {
        // Load wordfile and words
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

        // populate wheel positions
        wheelPositions[0f] = WheelResult(0, 750)
        wheelPositions[15f] = WheelResult(1) // lose a turn
        wheelPositions[30f] = WheelResult(0, 400)
        wheelPositions[45f] = WheelResult(0, 300)
        wheelPositions[60f] = WheelResult(0, 900)
        wheelPositions[75f] = WheelResult(2) // bankrupt
        wheelPositions[90f] = WheelResult(0, 550)
        wheelPositions[105f] = WheelResult(0, 200)
        wheelPositions[120f] = WheelResult(0, 350)
        wheelPositions[135f] = WheelResult(0, 900)
        wheelPositions[150f] = WheelResult(0, 150)
        wheelPositions[165f] = WheelResult(0, 150)
        wheelPositions[180f] = WheelResult(2) // bankrupt
        wheelPositions[195f] = WheelResult(0, 600)
        wheelPositions[210f] = WheelResult(0, 250)
        wheelPositions[225f] = WheelResult(0, 300)
        wheelPositions[240f] = WheelResult(0, 700)
        wheelPositions[255f] = WheelResult(0, 100)
        wheelPositions[270f] = WheelResult(0, 450)
        wheelPositions[285f] = WheelResult(0, 5000)
        wheelPositions[300f] = WheelResult(0, 800)
        wheelPositions[315f] = WheelResult(0, 250)
        wheelPositions[330f] = WheelResult(0, 600)
        wheelPositions[345f] = WheelResult(0, 350)

        currentWheelResult.value = wheelPositions[0f]!!
    }

    fun newGame() {
        setLives(5)
        setPoints(0)
        spinWheel()
    }

    fun spinWheel() {
        status.value = 5

        wheelPosition.value = (wheelPositions.keys).random().toFloat()

        val wheelResult = getWheelResult()
        if (wheelResult != null) {
            currentWheelResult.value = wheelResult
        }
    }

    fun getWheelResult(): WheelResult? {
        return wheelPositions[wheelPosition.value]
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

    fun guess(guessWord: String) {
        if (guessWord.isNotEmpty()) {
            if (guessWord.length == 1) {
                val char = guessWord[0]
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
            else {
                if (guessWord.replace(" ", "") == currentWord.value.replace(" ", "")) {
                    // reveal all chars
                    var i = 0
                    while (i < guessWord.length) {
                        val char = guessWord[i]
                        if (isCharInWord(char) && !isRevealed(char)) {
                            revealChar(char)
                        }
                        i++
                    }
                    addPoints(currentPossibleEarning.value)
                    setPossibleEarnings(0)
                    setWon()
                }
                else {
                    subtractLives(1)
                }
            }

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