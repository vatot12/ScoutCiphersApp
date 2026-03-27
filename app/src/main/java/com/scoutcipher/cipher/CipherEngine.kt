package com.scoutcipher.cipher

// ═══════════════════════════════════════════════════════════════════════════════
// Arabic Cipher Engine
// All ciphers operate on the Arabic alphabet (أ to ي = 28 letters)
// ═══════════════════════════════════════════════════════════════════════════════

object ArabicAlphabet {
    // 28 letters of the Arabic alphabet in order
    val letters = listOf(
        'أ', 'ب', 'ت', 'ث', 'ج', 'ح', 'خ', 'د', 'ذ', 'ر',
        'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ع', 'غ', 'ف',
        'ق', 'ك', 'ل', 'م', 'ن', 'ه', 'و', 'ي'
    )
    val size = letters.size // 28
    val charToIndex = letters.withIndex().associate { (i, c) -> c to i }

    fun isArabic(c: Char) = c in charToIndex
}

// ── 1. Caesar (تشفير قيصر) ───────────────────────────────────────────────────
object CaesarCipher {
    fun encode(text: String, shift: Int): String {
        val s = ((shift % ArabicAlphabet.size) + ArabicAlphabet.size) % ArabicAlphabet.size
        return text.map { c ->
            val idx = ArabicAlphabet.charToIndex[c]
            if (idx != null) ArabicAlphabet.letters[(idx + s) % ArabicAlphabet.size]
            else c
        }.joinToString("")
    }
    fun decode(text: String, shift: Int) = encode(text, -shift)
}

// ── 2. Atbash Arabic (المرآة) ─────────────────────────────────────────────────
object AtbashCipher {
    fun process(text: String): String {
        return text.map { c ->
            val idx = ArabicAlphabet.charToIndex[c]
            if (idx != null) ArabicAlphabet.letters[ArabicAlphabet.size - 1 - idx]
            else c
        }.joinToString("")
    }
}

// ── 3. Vigenère Arabic (فيجنير) ───────────────────────────────────────────────
object VigenereCipher {
    fun encode(text: String, keyword: String): String {
        val keyLetters = keyword.filter { ArabicAlphabet.isArabic(it) }
        if (keyLetters.isEmpty()) return text
        var ki = 0
        return text.map { c ->
            val idx = ArabicAlphabet.charToIndex[c]
            if (idx != null) {
                val keyIdx = ArabicAlphabet.charToIndex[keyLetters[ki % keyLetters.length]]!!
                ki++
                ArabicAlphabet.letters[(idx + keyIdx) % ArabicAlphabet.size]
            } else c
        }.joinToString("")
    }

    fun decode(text: String, keyword: String): String {
        val keyLetters = keyword.filter { ArabicAlphabet.isArabic(it) }
        if (keyLetters.isEmpty()) return text
        var ki = 0
        return text.map { c ->
            val idx = ArabicAlphabet.charToIndex[c]
            if (idx != null) {
                val keyIdx = ArabicAlphabet.charToIndex[keyLetters[ki % keyLetters.length]]!!
                ki++
                ArabicAlphabet.letters[((idx - keyIdx) + ArabicAlphabet.size) % ArabicAlphabet.size]
            } else c
        }.joinToString("")
    }
}

// ── 4. Morse Arabic (مورس عربي) ───────────────────────────────────────────────
// Uses ITU morse extensions for Arabic letters
object MorseCipher {
    private val arabicToMorse = mapOf(
        'أ' to ".-",   'ب' to "-...", 'ت' to "-.-.", 'ث' to "-.-",
        'ج' to ".---", 'ح' to "....", 'خ' to "---", 'د' to "-..",
        'ذ' to "---..", 'ر' to ".-.", 'ز' to "--.",  'س' to "...",
        'ش' to "----", 'ص' to "-..-", 'ض' to "...-", 'ط' to "..-",
        'ظ' to "-.--", 'ع' to ".-.-", 'غ' to "--.",  'ف' to "..-.",
        'ق' to "--.-", 'ك' to "-.-",  'ل' to ".-..", 'م' to "--",
        'ن' to "-.",   'ه' to ".....", 'و' to ".--",  'ي' to "..",
        '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--",
        '4' to "....-", '5' to ".....", '6' to "-....", '7' to "--...",
        '8' to "---..", '9' to "----."
    )
    private val morseToArabic = arabicToMorse.entries.associate { (k, v) -> v to k }

    fun encode(text: String): String {
        return text.map { c ->
            when {
                c == ' ' -> "/"
                arabicToMorse.containsKey(c) -> arabicToMorse[c]!!
                else -> "?"
            }
        }.joinToString(" ")
    }

    fun decode(text: String): String {
        return text.split(" / ").joinToString(" ") { word ->
            word.trim().split(" ").joinToString("") { code ->
                when {
                    code.isEmpty() -> ""
                    code == "/" -> " "
                    morseToArabic.containsKey(code) -> morseToArabic[code].toString()
                    else -> "?"
                }
            }
        }
    }
}

// ── 5. Rail Fence (السياج) ────────────────────────────────────────────────────
object RailFenceCipher {
    fun encode(text: String, rails: Int): String {
        val clean = text.replace(" ", "")
        if (rails <= 1 || rails >= clean.length) return clean
        val fence = Array(rails) { StringBuilder() }
        var rail = 0; var dir = 1
        for (c in clean) {
            fence[rail].append(c)
            if (rail == 0) dir = 1
            else if (rail == rails - 1) dir = -1
            rail += dir
        }
        return fence.joinToString(" | ") { it.toString() }
    }

    fun decode(text: String, rails: Int): String {
        val clean = text.replace(Regex("\\s*\\|\\s*"), "").replace(" ", "")
        val n = clean.length
        if (rails <= 1 || n == 0) return clean
        val pattern = IntArray(n)
        var rail = 0; var dir = 1
        for (i in 0 until n) {
            pattern[i] = rail
            if (rail == 0) dir = 1
            else if (rail == rails - 1) dir = -1
            rail += dir
        }
        val indices = (0 until n).sortedWith(compareBy({ pattern[it] }, { it }))
        val out = CharArray(n)
        for (i in 0 until n) out[indices[i]] = clean[i]
        return String(out)
    }
}

// ── 6. Polybius Arabic (مربع بوليبيوس) ───────────────────────────────────────
// 5×6 grid to fit 28 Arabic letters (last 2 cells empty)
object PolybiusCipher {
    // 5 rows × 6 cols = 30 cells, 28 letters fill the first 28
    private val rows = 5
    private val cols = 6

    fun encode(text: String): String {
        return text.filter { ArabicAlphabet.isArabic(it) || it == ' ' }.map { c ->
            if (c == ' ') return@map "/"
            val idx = ArabicAlphabet.charToIndex[c]!!
            val row = idx / cols + 1
            val col = idx % cols + 1
            "$row$col"
        }.joinToString(" ")
    }

    fun decode(text: String): String {
        return text.replace("/", " / ").trim().split(" ").joinToString("") { token ->
            when {
                token == "/" -> " "
                token.length == 2 && token.all { it.isDigit() } -> {
                    val row = token[0].digitToInt() - 1
                    val col = token[1].digitToInt() - 1
                    val idx = row * cols + col
                    if (idx in 0 until ArabicAlphabet.size) ArabicAlphabet.letters[idx].toString()
                    else "?"
                }
                else -> "?"
            }
        }
    }

    fun getGrid(): List<List<Char?>> {
        val grid = mutableListOf<List<Char?>>()
        for (r in 0 until rows) {
            val row = mutableListOf<Char?>()
            for (c in 0 until cols) {
                val idx = r * cols + c
                row.add(if (idx < ArabicAlphabet.size) ArabicAlphabet.letters[idx] else null)
            }
            grid.add(row)
        }
        return grid
    }

    val rowCount = rows
    val colCount = cols
}

// ── 7. Reverse (العكس) ────────────────────────────────────────────────────────
object ReverseCipher {
    fun process(text: String) = text.reversed()
}

// ── 8. Number Substitution / Abjad (الأبجد) ──────────────────────────────────
// Maps each Arabic letter to its positional number (1-28)
object NumberSubCipher {
    fun encode(text: String): String {
        return text.map { c ->
            val idx = ArabicAlphabet.charToIndex[c]
            if (idx != null) (idx + 1).toString()
            else if (c == ' ') "0"
            else c.toString()
        }.joinToString("-")
    }

    fun decode(text: String): String {
        return text.split("-").joinToString("") { token ->
            when {
                token == "0" -> " "
                token.toIntOrNull() != null -> {
                    val num = token.toInt()
                    if (num in 1..ArabicAlphabet.size) ArabicAlphabet.letters[num - 1].toString()
                    else "?"
                }
                else -> token
            }
        }
    }
}

// ── Cipher Registry ───────────────────────────────────────────────────────────
enum class CipherType {
    CAESAR, ATBASH, VIGENERE, MORSE, RAIL_FENCE, POLYBIUS, REVERSE, NUMBER_SUB
}

data class CipherInfo(
    val type: CipherType,
    val nameAr: String,
    val nameEn: String,
    val descAr: String,
    val emoji: String,
    val difficulty: Difficulty,
    val hasShiftParam: Boolean = false,
    val hasKeyParam: Boolean = false,
    val hasRailsParam: Boolean = false,
)

enum class Difficulty { EASY, MEDIUM, HARD }

val ALL_CIPHERS = listOf(
    CipherInfo(CipherType.CAESAR,     "قيصر",        "Caesar",          "إزاحة كل حرف بعدد ثابت في الأبجدية العربية",                "🏛️", Difficulty.EASY,   hasShiftParam = true),
    CipherInfo(CipherType.ATBASH,     "المرآة",       "Atbash",          "عكس ترتيب الأبجدية: الأول يصبح الأخير والعكس",             "🔄", Difficulty.EASY),
    CipherInfo(CipherType.REVERSE,    "المقلوب",      "Reverse",         "اقرأ النص من اليسار إلى اليمين",                            "🪞", Difficulty.EASY),
    CipherInfo(CipherType.NUMBER_SUB, "الأرقام",      "Number Sub",      "استبدل كل حرف برقمه في الأبجدية (1-28)",                   "🔢", Difficulty.EASY),
    CipherInfo(CipherType.MORSE,      "مورس",         "Morse",           "نقاط وشرطات للتواصل اللاسلكي",                             "📡", Difficulty.MEDIUM),
    CipherInfo(CipherType.VIGENERE,   "فيجنير",       "Vigenère",        "تشفير بكلمة مفتاح تتكرر على النص كله",                     "🔑", Difficulty.MEDIUM, hasKeyParam = true),
    CipherInfo(CipherType.RAIL_FENCE, "السياج",       "Rail Fence",      "اكتب النص على شكل متعرج ثم اقرأه سطراً سطراً",             "🚂", Difficulty.MEDIUM, hasRailsParam = true),
    CipherInfo(CipherType.POLYBIUS,   "بوليبيوس",    "Polybius",        "شبكة 5×6 تعطي كل حرف رقمين: الصف والعمود",                "🔲", Difficulty.HARD),
)

data class CipherParams(
    val shift: Int = 3,
    val keyword: String = "كشاف",
    val rails: Int = 3
)

fun applyCipher(type: CipherType, text: String, params: CipherParams, encode: Boolean): String {
    return when (type) {
        CipherType.CAESAR     -> if (encode) CaesarCipher.encode(text, params.shift) else CaesarCipher.decode(text, params.shift)
        CipherType.ATBASH     -> AtbashCipher.process(text)
        CipherType.VIGENERE   -> if (encode) VigenereCipher.encode(text, params.keyword) else VigenereCipher.decode(text, params.keyword)
        CipherType.MORSE      -> if (encode) MorseCipher.encode(text) else MorseCipher.decode(text)
        CipherType.RAIL_FENCE -> if (encode) RailFenceCipher.encode(text, params.rails) else RailFenceCipher.decode(text, params.rails)
        CipherType.POLYBIUS   -> if (encode) PolybiusCipher.encode(text) else PolybiusCipher.decode(text)
        CipherType.REVERSE    -> ReverseCipher.process(text)
        CipherType.NUMBER_SUB -> if (encode) NumberSubCipher.encode(text) else NumberSubCipher.decode(text)
    }
}
