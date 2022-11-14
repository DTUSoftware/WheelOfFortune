package dk.dtu.s215827.wheeloffortune

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import kotlin.random.Random

// Status of the game
enum class GameStatus {
    ERROR,
    NOT_PLAYING,
    NEW_GAME,
    PLAYING,
    WON,
    LOST,
    DONE,
    WHEEL_SPINNING,
    TURN_DONE_CORRECT,
    TURN_DONE_WRONG,
    TURN_DONE_LOST
}

// Actions from button or wheel
enum class Action {
    NEW_GAME,
    SPIN,
    REPOPULATE
}

// Class for possible wheel results
class WheelResult(var viewModel: PlayerViewModel? = null, var type: Int, var points: Int? = null) {
    fun applyResult() {
        when (type) {
            // Point result
            0 -> {
                points?.let { viewModel?.setPossibleEarnings(it) }
                viewModel?.setPlaying()
            }

            // Lose a turn result
            1 -> {
                viewModel?.loseATurn()
            }

            // Bankruptcy result
            2 -> {
                viewModel?.doBankruptcy()
            }
        }
    }
}

class PlayerViewModel : ViewModel() {
    // We would like a unidirectional data flow, where the UI can pass events, and we update the state

    // States used for stateflow - we make sure we can only edit them through the viewmodel
    // and then we use the StateFlow for passing the values to the UI, with read-only access
    // https://developer.android.com/codelabs/basic-android-kotlin-compose-viewmodel-and-state#4
    private val _currentWord = MutableStateFlow("")
    val currentWord: StateFlow<String> = _currentWord.asStateFlow()

    private val _currentCategory = MutableStateFlow("")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    private val _currentPossibleEarning = MutableStateFlow(0)
    val currentPossibleEarning: StateFlow<Int> = _currentPossibleEarning.asStateFlow()

    private val _revealedLetters = MutableStateFlow(emptyList<Char>())
    val revealedLetters: StateFlow<List<Char>> = _revealedLetters.asStateFlow()

    private val _lives = MutableStateFlow(0)
    val lives: StateFlow<Int> = _lives.asStateFlow()

    private val _points = MutableStateFlow(0)
    val points: StateFlow<Int> = _points.asStateFlow()

    private val _status = MutableStateFlow(GameStatus.NOT_PLAYING)
    val status: StateFlow<GameStatus> = _status.asStateFlow()

    private val _wheelPosition = MutableStateFlow(0f)
    val wheelPosition: StateFlow<Float> = _wheelPosition.asStateFlow()

    private val _currentWheelResult = MutableStateFlow(WheelResult(null, -1))
    val currentWheelResult: StateFlow<WheelResult> = _currentWheelResult.asStateFlow()

    // Possible wheel positions
    private val wheelPositions = HashMap<Float, WheelResult>()
    private val randomSeed = Random(System.currentTimeMillis())
    private var wordsTotal = 0
    private var wordsMap = HashMap<String, List<String>>()

    init {
        populateWords()

        // populate wheel positions
        wheelPositions[0f] = WheelResult(this, 0, 750)
        wheelPositions[15f] = WheelResult(this, 0, 500)
        wheelPositions[30f] = WheelResult(this, 0, 400)
        wheelPositions[45f] = WheelResult(this, 0, 300)
        wheelPositions[60f] = WheelResult(this, 0, 900)
        wheelPositions[75f] = WheelResult(this, 2) // bankrupt
        wheelPositions[90f] = WheelResult(this, 0, 550)
        wheelPositions[105f] = WheelResult(this, 0, 200)
        wheelPositions[120f] = WheelResult(this, 0, 350)
        wheelPositions[135f] = WheelResult(this, 0, 900)
        wheelPositions[150f] = WheelResult(this, 0, 150)
        wheelPositions[165f] = WheelResult(this, 0, 150)
        wheelPositions[180f] = WheelResult(this, 2) // bankrupt
        wheelPositions[195f] = WheelResult(this, 0, 600)
        wheelPositions[210f] = WheelResult(this, 0, 250)
        wheelPositions[225f] = WheelResult(this, 0, 300)
        wheelPositions[240f] = WheelResult(this, 0, 700)
        wheelPositions[255f] = WheelResult(this, 0, 100)
        wheelPositions[270f] = WheelResult(this, 0, 450)
        wheelPositions[285f] = WheelResult(this, 0, 5000)
        wheelPositions[300f] = WheelResult(this, 0, 800)
        wheelPositions[315f] = WheelResult(this, 0, 250)
        wheelPositions[330f] = WheelResult(this, 0, 600)
        wheelPositions[345f] = WheelResult(this, 0, 350)

        // Set current position to 0 degrees
        _currentWheelResult.value = wheelPositions[0f]!!
    }

    fun populateWords() {
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
    }

    fun newGame() {
        setLives(5)
        setPoints(0)
        newWord()
        setNewGame()
    }

    fun spinWheel() {
        _status.value = GameStatus.WHEEL_SPINNING

        _wheelPosition.value = (wheelPositions.keys).random(randomSeed).toFloat()

        val wheelResult = getWheelResult()
        if (wheelResult != null) {
            _currentWheelResult.value = wheelResult
        }
    }

    fun getWheelResult(): WheelResult? {
        return wheelPositions[wheelPosition.value]
    }

    fun loseATurn() {
        setTurnDoneLost()
        subtractLives(1)
    }

    fun setNewGame() {
        this._status.value = GameStatus.NEW_GAME
    }

    fun setNotPlaying() {
        this._status.value = GameStatus.NOT_PLAYING
    }

    fun setPlaying() {
        this._status.value = GameStatus.PLAYING
    }

    fun setWon() {
        this._status.value = GameStatus.WON
        checkDone()
    }

    fun setLost() {
        this._status.value = GameStatus.LOST
        checkDone()
    }

    fun setDone() {
        this._status.value = GameStatus.DONE
    }

    fun setTurnDoneCorrect() {
        this._status.value = GameStatus.TURN_DONE_CORRECT
    }

    fun setTurnDoneWrong() {
        this._status.value = GameStatus.TURN_DONE_WRONG
    }

    fun setTurnDoneLost() {
        this._status.value = GameStatus.TURN_DONE_LOST
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
            this._revealedLetters.value = emptyList()
            val category = wordsMap.keys.elementAt((0 until wordsMap.size).random(randomSeed))
            val words = wordsMap[category]

            if (!words.isNullOrEmpty()) {
                val word = words[(words.indices).random(randomSeed)]

                this._currentWord.value = word.uppercase()
                this._currentCategory.value = category
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
            // If a single char was guessed
            if (guessWord.length == 1) {
                val char = guessWord[0]
                // if it is in the word and not already revealed, reveal it - else wrong
                if (isCharInWord(char) && !isRevealed(char)) {
                    revealChar(char)
                    addPoints(_currentPossibleEarning.value * countCharInWord(char))
                    setPossibleEarnings(0)

                    if (_revealedLetters.value.size == _currentWord.value.replace(" ", "").toList()
                            .distinct().size
                    ) {
                        setWon()
                    } else {
                        setTurnDoneCorrect()
                    }
                } else {
                    setTurnDoneWrong()
                    subtractLives(1)
                }
                // This is not used in this game, since you only guess a single char,
                // but by enabling more than one char in the text-field, this could be used
            } else {
                if (guessWord.replace(" ", "") == _currentWord.value.replace(" ", "")) {
                    // reveal all chars
                    var i = 0
                    while (i < guessWord.length) {
                        val char = guessWord[i]
                        if (isCharInWord(char) && !isRevealed(char)) {
                            revealChar(char)
                            addPoints(_currentPossibleEarning.value * countCharInWord(char))
                        }
                        i++
                    }
                    setPossibleEarnings(0)
                    setWon()
                } else {
                    setTurnDoneWrong()
                    subtractLives(1)
                }
            }
        }
    }

    fun isCharInWord(char: Char): Boolean {
        // ignoreCase technically not needed now
        return this._currentWord.value.contains(char, ignoreCase = true)
    }

    fun countCharInWord(char: Char): Int {
        return this._currentWord.value.count { it == char }
    }

    fun isRevealed(char: Char): Boolean {
        return this._revealedLetters.value.contains(char)
    }

    fun revealChar(char: Char) {
        this._revealedLetters.value = this._revealedLetters.value.plus(char)
    }

    fun addPoints(points: Int) {
        this._points.value += points
    }

    fun subtractPoints(points: Int) {
        this._points.value -= points
    }

    fun setPoints(points: Int) {
        this._points.value = points
    }

    fun setPossibleEarnings(points: Int) {
        this._currentPossibleEarning.value = points
    }

    fun doBankruptcy() {
        setTurnDoneLost()
        this._points.value = 0
//        setLost()
    }

    fun addLives(lives: Int) {
        this._lives.value += lives
    }

    fun subtractLives(lives: Int) {
        this._lives.value -= lives
        if (this._lives.value <= 0) {
            setLost()
        }
    }

    fun setLives(lives: Int) {
        this._lives.value = lives
    }
}
