package com.scoutcipher

import android.app.Application
import androidx.lifecycle.*
import com.scoutcipher.cipher.*
import com.scoutcipher.util.AppRepository
import com.scoutcipher.util.ChallengeStats
import com.scoutcipher.util.HistoryEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AppRepository(application)

    // ── Cipher selection & params ─────────────────────────────────────────────
    val selectedCipher = MutableLiveData(CipherType.CAESAR)
    val cipherParams   = MutableLiveData(CipherParams())

    // ── Encode / Decode ───────────────────────────────────────────────────────
    val encodeInput  = MutableLiveData("")
    val encodeOutput = MutableLiveData("")
    val decodeInput  = MutableLiveData("")
    val decodeOutput = MutableLiveData("")

    fun encode() {
        val text   = encodeInput.value ?: return
        val type   = selectedCipher.value ?: return
        val params = cipherParams.value ?: CipherParams()
        if (text.isBlank()) return
        val result = applyCipher(type, text, params, encode = true)
        encodeOutput.value = result
        val info = ALL_CIPHERS.first { it.type == type }
        saveHistory(HistoryEntry(
            cipherName = info.nameAr,
            input      = text,
            output     = result,
            direction  = "تشفير"
        ))
    }

    fun decode() {
        val text   = decodeInput.value ?: return
        val type   = selectedCipher.value ?: return
        val params = cipherParams.value ?: CipherParams()
        if (text.isBlank()) return
        val result = applyCipher(type, text, params, encode = false)
        decodeOutput.value = result
        val info = ALL_CIPHERS.first { it.type == type }
        saveHistory(HistoryEntry(
            cipherName = info.nameAr,
            input      = text,
            output     = result,
            direction  = "فك تشفير"
        ))
    }

    fun clearEncode() { encodeInput.value = ""; encodeOutput.value = "" }
    fun clearDecode() { decodeInput.value = ""; decodeOutput.value = "" }

    // ── History ───────────────────────────────────────────────────────────────
    val history = repo.historyFlow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private fun saveHistory(entry: HistoryEntry) {
        viewModelScope.launch { repo.addHistory(entry) }
    }
    fun clearHistory() { viewModelScope.launch { repo.clearHistory() } }

    // ── Challenge ─────────────────────────────────────────────────────────────
    val stats = repo.statsFlow.stateIn(viewModelScope, SharingStarted.Lazily, ChallengeStats())

    val challengeEncoded      = MutableLiveData("")
    val challengeCipherName   = MutableLiveData("")
    val challengeAnswer       = MutableLiveData("")
    val challengeResultText   = MutableLiveData("")
    val challengeResultOk     = MutableLiveData<Boolean?>(null)

    private val CHALLENGE_MESSAGES = listOf(
        "التقِ عند الشجرة الكبيرة",
        "احضر البوصلة والخريطة",
        "المعسكر في الشمال",
        "الإشارة عند منتصف الليل",
        "نقطة التجمع برافو",
        "النسر هبط بسلام",
        "شرف الكشاف اليوم",
        "فك الشفرة للفوز",
        "احتفظ بالسر",
        "أرسل الإشارة الآن"
    )

    private var currentPlain = ""
    private var currentCipherType: CipherType = CipherType.CAESAR
    private var currentParams = CipherParams()

    fun generateChallenge() {
        val eligibleTypes = listOf(
            CipherType.CAESAR, CipherType.ATBASH, CipherType.REVERSE,
            CipherType.NUMBER_SUB, CipherType.VIGENERE
        )
        val type   = eligibleTypes[Random.nextInt(eligibleTypes.size)]
        val msg    = CHALLENGE_MESSAGES[Random.nextInt(CHALLENGE_MESSAGES.size)]
        val params = when (type) {
            CipherType.CAESAR   -> CipherParams(shift = Random.nextInt(1, 14))
            CipherType.VIGENERE -> CipherParams(keyword = listOf("كشاف", "سلام", "نور", "قمر").random())
            else                -> CipherParams()
        }
        val encoded = applyCipher(type, msg, params, encode = true)
        currentPlain      = msg
        currentCipherType = type
        currentParams     = params
        challengeEncoded.value    = encoded
        challengeCipherName.value = ALL_CIPHERS.first { it.type == type }.nameAr
        challengeResultText.value = ""
        challengeResultOk.value   = null
        challengeAnswer.value     = ""
    }

    fun checkAnswer() {
        val ans = challengeAnswer.value?.trim() ?: ""
        if (ans.isEmpty() || currentPlain.isEmpty()) return
        val isCorrect = ans == currentPlain.trim()
        viewModelScope.launch {
            val s = stats.value
            val newStats = if (isCorrect) s.copy(
                score   = s.score + 10,
                correct = s.correct + 1,
                streak  = s.streak + 1
            ) else s.copy(
                wrong  = s.wrong + 1,
                streak = 0
            )
            repo.updateStats(newStats)
        }
        challengeResultText.value = if (isCorrect) "✅ إجابة صحيحة! +10 نقطة" else "❌ ليست الإجابة الصحيحة، حاول مجدداً"
        challengeResultOk.value   = isCorrect
    }

    fun revealAnswer() {
        challengeResultText.value = "الإجابة: $currentPlain"
        challengeResultOk.value   = null
        viewModelScope.launch {
            val s = stats.value
            repo.updateStats(s.copy(streak = 0))
        }
    }
}

class MainViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(app) as T
    }
}
