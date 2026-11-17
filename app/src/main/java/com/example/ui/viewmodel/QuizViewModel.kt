package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferences
import com.example.data.local.UserPrefsState
import com.example.data.model.Attempt
import com.example.data.model.Question
import com.example.data.repository.QuizRepository
import com.example.util.SoundEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = QuizRepository(db.questionDao(), db.attemptDao())
    private val userPreferences = UserPreferences(application)
    val soundEngine = SoundEngine(application)

    val allQuestions: StateFlow<List<Question>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttempts: StateFlow<List<Attempt>> = repository.allAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPrefs: StateFlow<UserPrefsState> = userPreferences.userPrefsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPrefsState())

    val studentName: StateFlow<String> = userPreferences.userPrefsFlow
        .map { it.studentName }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "بطلنا المستقبلي")

    val studentAvatar: StateFlow<String> = userPreferences.userPrefsFlow
        .map { it.studentAvatar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "🌟")

    val isProfileCompleted: StateFlow<Boolean> = userPreferences.userPrefsFlow
        .map { it.isProfileCompleted }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val soundEffectsEnabled: StateFlow<Boolean> = userPreferences.userPrefsFlow
        .map { it.soundEffectsEnabled }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val voiceReadAloudEnabled: StateFlow<Boolean> = userPreferences.userPrefsFlow
        .map { it.voiceReadAloudEnabled }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val dailyStreak: StateFlow<Int> = userPreferences.userPrefsFlow
        .map { it.dailyStreak }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val isDailyChallengeDoneToday: StateFlow<Boolean> = userPreferences.userPrefsFlow
        .map {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            it.dailyChallengeCompletedDate == today
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Current quiz play states
    private val _selectedSubject = MutableStateFlow<String?>(null)
    val selectedSubject: StateFlow<String?> = _selectedSubject.asStateFlow()

    private val _isDailyChallenge = MutableStateFlow(false)
    val isDailyChallenge: StateFlow<Boolean> = _isDailyChallenge.asStateFlow()

    private val _activeUnit = MutableStateFlow("")
    val activeUnit: StateFlow<String> = _activeUnit.asStateFlow()

    private val _activeQuestions = MutableStateFlow<List<Question>>(emptyList())
    val activeQuestions: StateFlow<List<Question>> = _activeQuestions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _starsCount = MutableStateFlow(0)
    val starsCount: StateFlow<Int> = _starsCount.asStateFlow()

    private val _comboStreak = MutableStateFlow(0)
    val comboStreak: StateFlow<Int> = _comboStreak.asStateFlow()

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished: StateFlow<Boolean> = _quizFinished.asStateFlow()

    // Interactive answering feedback states
    private val _showFeedbackDialog = MutableStateFlow(false)
    val showFeedbackDialog: StateFlow<Boolean> = _showFeedbackDialog.asStateFlow()

    private val _isCurrentAnswerCorrect = MutableStateFlow(false)
    val isCurrentAnswerCorrect: StateFlow<Boolean> = _isCurrentAnswerCorrect.asStateFlow()

    private val _currentExplanation = MutableStateFlow("")
    val currentExplanation: StateFlow<String> = _currentExplanation.asStateFlow()

    private val _currentCorrectAnswer = MutableStateFlow("")
    val currentCorrectAnswer: StateFlow<String> = _currentCorrectAnswer.asStateFlow()

    // Hint state
    private val _hintUsed = MutableStateFlow(false)
    val hintUsed: StateFlow<Boolean> = _hintUsed.asStateFlow()

    private val _eliminatedOption = MutableStateFlow<String?>(null)
    val eliminatedOption: StateFlow<String?> = _eliminatedOption.asStateFlow()

    // Option-based states
    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption.asStateFlow()

    // Scrambled & Arranged values
    private val _scrambledLetters = MutableStateFlow<List<String>>(emptyList())
    val scrambledLetters: StateFlow<List<String>> = _scrambledLetters.asStateFlow()

    private val _arrangedLetters = MutableStateFlow<List<String>>(emptyList())
    val arrangedLetters: StateFlow<List<String>> = _arrangedLetters.asStateFlow()

    // Matching columns states
    private val _leftMatchItems = MutableStateFlow<List<String>>(emptyList())
    val leftMatchItems: StateFlow<List<String>> = _leftMatchItems.asStateFlow()

    private val _rightMatchItems = MutableStateFlow<List<String>>(emptyList())
    val rightMatchItems: StateFlow<List<String>> = _rightMatchItems.asStateFlow()

    private val _matchedPairs = MutableStateFlow<Map<String, String>>(emptyMap())
    val matchedPairs: StateFlow<Map<String, String>> = _matchedPairs.asStateFlow()

    private val _selectedLeftItem = MutableStateFlow<String?>(null)
    val selectedLeftItem: StateFlow<String?> = _selectedLeftItem.asStateFlow()

    private val _selectedRightItem = MutableStateFlow<String?>(null)
    val selectedRightItem: StateFlow<String?> = _selectedRightItem.asStateFlow()

    init {
        viewModelScope.launch {
            repository.preloadDefaultQuestionsIfEmpty()
            userPreferences.recordActivityToday()
        }
        viewModelScope.launch {
            userPreferences.userPrefsFlow.collect { prefs ->
                soundEngine.isSoundEnabled = prefs.soundEffectsEnabled
                soundEngine.isTtsEnabled = prefs.voiceReadAloudEnabled
            }
        }
    }

    fun updateStudentProfile(name: String, avatar: String) {
        viewModelScope.launch {
            userPreferences.saveProfile(name, avatar)
        }
    }

    fun toggleSoundEffects() {
        viewModelScope.launch {
            userPreferences.setSoundEffectsEnabled(!soundEffectsEnabled.value)
        }
    }

    fun toggleVoiceReadAloud() {
        viewModelScope.launch {
            val next = !voiceReadAloudEnabled.value
            userPreferences.setVoiceReadAloudEnabled(next)
            if (!next) {
                soundEngine.stopSpeaking()
            }
        }
    }

    fun clearDatabaseHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun addCustomQuestion(
        subject: String,
        unit: String,
        questionText: String,
        hintEmoji: String,
        options: List<String>,
        correctAnswer: String,
        explanation: String,
        difficulty: String
    ) {
        viewModelScope.launch {
            val cleanOptions = options.map { it.trim() }.filter { it.isNotEmpty() }.joinToString(";")
            val question = Question(
                subject = subject,
                unit = unit.ifBlank { "Custom Practice" },
                questionType = "MULTIPLE_CHOICE",
                questionText = questionText.trim(),
                hintImage = hintEmoji.ifBlank { "🌟" },
                options = cleanOptions,
                correctAnswer = correctAnswer.trim(),
                explanation = explanation.trim().ifBlank { "Great job answering this question!" },
                difficulty = difficulty
            )
            repository.insertQuestion(question)
        }
    }

    fun deleteQuestion(id: Long) {
        viewModelScope.launch {
            repository.deleteQuestionById(id)
        }
    }

    fun playQuestionAudio() {
        val currentQuestion = getCurrentQuestion() ?: return
        val isEnglish = currentQuestion.subject != "اللغة العربية" && currentQuestion.subject != "Arabic"
        soundEngine.speak(currentQuestion.questionText, isEnglish, force = true)
    }

    fun stopAudio() {
        soundEngine.stopSpeaking()
    }

    fun playClickSound() {
        soundEngine.playClickSound()
    }

    fun startQuiz(subject: String) {
        val normalized = when (subject.trim()) {
            "English", "الإنجليزية" -> "English"
            "Mathematics", "Math", "الرياضيات" -> "Mathematics"
            "Science", "العلوم" -> "Science"
            "Social Studies", "الدراسات الاجتماعية" -> "Social Studies"
            "Arabic", "اللغة العربية" -> "اللغة العربية"
            "Islamic Studies", "التربية الإسلامية" -> "Islamic Studies"
            else -> subject
        }
        _isDailyChallenge.value = false
        _selectedSubject.value = normalized
        resetQuizPlayStates()

        viewModelScope.launch {
            val questionsForSubject = repository.getQuestionsBySubject(normalized).first()
            if (questionsForSubject.isNotEmpty()) {
                _activeQuestions.value = questionsForSubject.shuffled().take(5)
                prepareActiveQuestionStates()
            } else {
                _activeQuestions.value = emptyList()
            }
        }
    }

    fun startDailyChallenge() {
        _isDailyChallenge.value = true
        _selectedSubject.value = "Daily Challenge"
        resetQuizPlayStates()

        viewModelScope.launch {
            val all = repository.allQuestions.first()
            if (all.isNotEmpty()) {
                val mixed = all.groupBy { it.subject }
                    .values
                    .mapNotNull { it.randomOrNull() }
                    .shuffled()
                    .take(5)
                _activeQuestions.value = if (mixed.size >= 3) mixed else all.shuffled().take(5)
                prepareActiveQuestionStates()
            } else {
                _activeQuestions.value = emptyList()
            }
        }
    }

    private fun resetQuizPlayStates() {
        _currentIndex.value = 0
        _score.value = 0
        _starsCount.value = 0
        _comboStreak.value = 0
        _quizFinished.value = false
        _showFeedbackDialog.value = false
        _selectedOption.value = null
        _arrangedLetters.value = emptyList()
        _matchedPairs.value = emptyMap()
        _selectedLeftItem.value = null
        _selectedRightItem.value = null
        _hintUsed.value = false
        _eliminatedOption.value = null
    }

    fun getCurrentQuestion(): Question? {
        val index = _currentIndex.value
        val questions = _activeQuestions.value
        return if (index in questions.indices) questions[index] else null
    }

    private fun prepareActiveQuestionStates() {
        val question = getCurrentQuestion() ?: return
        _selectedOption.value = null
        _selectedLeftItem.value = null
        _selectedRightItem.value = null
        _matchedPairs.value = emptyMap()
        _hintUsed.value = false
        _eliminatedOption.value = null
        _activeUnit.value = question.unit

        when (question.questionType) {
            "ARRANGE_LETTERS" -> {
                val letters = question.options.split(";").filter { it.isNotEmpty() }
                _scrambledLetters.value = letters.shuffled()
                _arrangedLetters.value = emptyList()
            }
            "MATCH_COLUMNS" -> {
                val pairs = question.options.split(";").filter { it.isNotEmpty() }
                val left = mutableListOf<String>()
                val right = mutableListOf<String>()
                pairs.forEach {
                    val parts = it.split("-")
                    if (parts.size == 2) {
                        left.add(parts[0])
                        right.add(parts[1])
                    }
                }
                _leftMatchItems.value = left.shuffled()
                _rightMatchItems.value = right.shuffled()
            }
        }
    }

    fun useHint() {
        val question = getCurrentQuestion() ?: return
        if (_hintUsed.value) return
        _hintUsed.value = true
        playClickSound()

        when (question.questionType) {
            "MULTIPLE_CHOICE", "FILL_BLANK" -> {
                val wrongOptions = question.options.split(";")
                    .filter { it.isNotBlank() && !it.trim().equals(question.correctAnswer.trim(), ignoreCase = true) }
                if (wrongOptions.isNotEmpty()) {
                    val eliminated = wrongOptions.random()
                    _eliminatedOption.value = eliminated
                    if (_selectedOption.value == eliminated) {
                        _selectedOption.value = null
                    }
                }
            }
            "ARRANGE_LETTERS" -> {
                val cleanExpected = normalizeForArrange(question.correctAnswer)
                if (_arrangedLetters.value.isEmpty() && cleanExpected.isNotEmpty()) {
                    val firstChar = cleanExpected.first().toString()
                    val matchingTile = _scrambledLetters.value.firstOrNull {
                        normalizeForArrange(it) == firstChar
                    }
                    if (matchingTile != null) {
                        addArrangedLetter(matchingTile)
                    }
                }
            }
        }
    }

    fun addArrangedLetter(letter: String) {
        val currentArranged = _arrangedLetters.value.toMutableList()
        currentArranged.add(letter)
        _arrangedLetters.value = currentArranged

        val remainingScrambled = _scrambledLetters.value.toMutableList()
        remainingScrambled.remove(letter)
        _scrambledLetters.value = remainingScrambled
    }

    fun removeLastArrangedLetter() {
        val currentArranged = _arrangedLetters.value.toMutableList()
        if (currentArranged.isEmpty()) return
        val removed = currentArranged.removeAt(currentArranged.lastIndex)
        _arrangedLetters.value = currentArranged

        val remainingScrambled = _scrambledLetters.value.toMutableList()
        remainingScrambled.add(removed)
        _scrambledLetters.value = remainingScrambled
    }

    fun resetArrangedLetters() {
        val question = getCurrentQuestion() ?: return
        val letters = question.options.split(";").filter { it.isNotEmpty() }
        _scrambledLetters.value = letters.shuffled()
        _arrangedLetters.value = emptyList()
    }

    fun selectOption(option: String) {
        if (option == _eliminatedOption.value) return
        _selectedOption.value = option
    }

    fun selectLeftMatch(item: String) {
        _selectedLeftItem.value = if (_selectedLeftItem.value == item) null else item
        checkColumnMatchInstant()
    }

    fun selectRightMatch(item: String) {
        _selectedRightItem.value = if (_selectedRightItem.value == item) null else item
        checkColumnMatchInstant()
    }

    private fun checkColumnMatchInstant() {
        val left = _selectedLeftItem.value
        val right = _selectedRightItem.value
        if (left != null && right != null) {
            val currentMap = _matchedPairs.value.toMutableMap()
            // Remove any existing key mapped to the same right value so pairings stay 1-to-1
            currentMap.entries.removeAll { it.value == right }
            currentMap[left] = right
            _matchedPairs.value = currentMap

            _selectedLeftItem.value = null
            _selectedRightItem.value = null
        }
    }

    fun unmatchPair(leftKey: String) {
        val currentMap = _matchedPairs.value.toMutableMap()
        currentMap.remove(leftKey)
        _matchedPairs.value = currentMap
    }

    private fun normalizeForArrange(input: String): String {
        val arabicDiacritics = Regex("[\\u064B-\\u065F\\u0670]")
        return input.replace(arabicDiacritics, "")
            .replace("-", "")
            .replace(" ", "")
            .trim()
            .lowercase(Locale.ROOT)
    }

    fun submitAnswer() {
        val question = getCurrentQuestion() ?: return
        var isCorrect = false

        when (question.questionType) {
            "ARRANGE_LETTERS" -> {
                val compiledString = normalizeForArrange(_arrangedLetters.value.joinToString(""))
                val expectedString = normalizeForArrange(question.correctAnswer)
                isCorrect = compiledString == expectedString
            }
            "MATCH_COLUMNS" -> {
                val pairs = question.options.split(";").filter { it.isNotEmpty() }
                var fullyMatching = true
                var matchCount = 0
                pairs.forEach {
                    val parts = it.split("-")
                    if (parts.size == 2) {
                        val expectedRight = parts[1]
                        val actualRight = _matchedPairs.value[parts[0]]
                        if (actualRight == expectedRight) {
                            matchCount++
                        } else {
                            fullyMatching = false
                        }
                    }
                }
                isCorrect = fullyMatching && matchCount == pairs.size
            }
            else -> {
                val ans = _selectedOption.value
                isCorrect = ans != null && ans.trim().equals(question.correctAnswer.trim(), ignoreCase = true)
            }
        }

        _isCurrentAnswerCorrect.value = isCorrect
        _currentExplanation.value = question.explanation
        _currentCorrectAnswer.value = question.correctAnswer

        val isEnglish = question.subject != "اللغة العربية" && question.subject != "Arabic"
        if (isCorrect) {
            _score.value += 1
            _comboStreak.value += 1
            val comboBonus = if (_comboStreak.value >= 3) 1 else 0
            _starsCount.value += (3 + comboBonus)
            soundEngine.playCorrectSound(isEnglish)
        } else {
            _comboStreak.value = 0
            soundEngine.playIncorrectSound()
        }

        _showFeedbackDialog.value = true
    }

    fun dismissFeedbackAndGoNext() {
        _showFeedbackDialog.value = false
        val nextIndex = _currentIndex.value + 1
        val total = _activeQuestions.value.size

        if (nextIndex < total) {
            _currentIndex.value = nextIndex
            prepareActiveQuestionStates()
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        if (_isDailyChallenge.value && !isDailyChallengeDoneToday.value) {
            _starsCount.value += 5
            viewModelScope.launch {
                userPreferences.markDailyChallengeCompleted()
            }
        }

        _quizFinished.value = true
        val currentSubj = _selectedSubject.value ?: ""
        val isEnglish = currentSubj != "اللغة العربية" && currentSubj != "Arabic"
        soundEngine.playCelebrationSound(isEnglish)

        viewModelScope.launch {
            userPreferences.recordActivityToday()
            val total = _activeQuestions.value.size
            val earnedStars = _starsCount.value
            val wrongCount = (total - _score.value).coerceAtLeast(0)
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val dateStr = formatter.format(Date())

            val attempt = Attempt(
                studentName = studentName.value,
                subject = _selectedSubject.value ?: "General",
                score = _score.value,
                totalQuestions = total,
                starsEarned = earnedStars,
                wrongQuestionsCount = wrongCount,
                dateString = dateStr
            )
            repository.insertAttempt(attempt)
        }
    }

    fun saveDragDropAttempt(subject: String, score: Int, total: Int, stars: Int) {
        viewModelScope.launch {
            userPreferences.recordActivityToday()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val dateStr = formatter.format(Date())
            val attempt = Attempt(
                studentName = studentName.value,
                subject = subject,
                score = score,
                totalQuestions = total,
                starsEarned = stars,
                wrongQuestionsCount = (total - score).coerceAtLeast(0),
                dateString = dateStr
            )
            repository.insertAttempt(attempt)
        }
    }

    fun saveMiniGameAttempt(gameName: String, score: Int, total: Int, stars: Int) {
        viewModelScope.launch {
            userPreferences.recordActivityToday()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val dateStr = formatter.format(Date())
            val attempt = Attempt(
                studentName = studentName.value,
                subject = gameName,
                score = score,
                totalQuestions = total,
                starsEarned = stars,
                wrongQuestionsCount = (total - score).coerceAtLeast(0),
                dateString = dateStr
            )
            repository.insertAttempt(attempt)
        }
    }

    fun saveStoryAttempt(storyTitle: String, stars: Int) {
        viewModelScope.launch {
            userPreferences.recordActivityToday()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val dateStr = formatter.format(Date())
            val attempt = Attempt(
                studentName = studentName.value,
                subject = storyTitle,
                score = 5,
                totalQuestions = 5,
                starsEarned = stars,
                wrongQuestionsCount = 0,
                dateString = dateStr
            )
            repository.insertAttempt(attempt)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundEngine.shutdown()
    }
}
