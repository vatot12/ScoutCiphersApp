package com.scoutcipher.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.scoutcipher.cipher.CipherType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scout_prefs")

data class HistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val cipherName: String,
    val input: String,
    val output: String,
    val direction: String,   // "تشفير" or "فك تشفير"
    val timestamp: String = SimpleDateFormat("HH:mm · dd/MM", Locale.getDefault()).format(Date())
)

data class ChallengeStats(
    val score: Int = 0,
    val correct: Int = 0,
    val wrong: Int = 0,
    val streak: Int = 0
)

class AppRepository(private val context: Context) {

    companion object {
        private val HISTORY_KEY = stringPreferencesKey("history")
        private val STATS_KEY   = stringPreferencesKey("stats")
    }

    // ── History ───────────────────────────────────────────────────────────────
    val historyFlow: Flow<List<HistoryEntry>> = context.dataStore.data.map { prefs ->
        parseHistory(prefs[HISTORY_KEY] ?: "[]")
    }

    suspend fun addHistory(entry: HistoryEntry) {
        context.dataStore.edit { prefs ->
            val list = parseHistory(prefs[HISTORY_KEY] ?: "[]").toMutableList()
            list.add(0, entry)
            if (list.size > 50) list.removeLast()
            prefs[HISTORY_KEY] = serializeHistory(list)
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { it[HISTORY_KEY] = "[]" }
    }

    // ── Stats ─────────────────────────────────────────────────────────────────
    val statsFlow: Flow<ChallengeStats> = context.dataStore.data.map { prefs ->
        parseStats(prefs[STATS_KEY] ?: "{}")
    }

    suspend fun updateStats(stats: ChallengeStats) {
        context.dataStore.edit { prefs ->
            prefs[STATS_KEY] = JSONObject().apply {
                put("score",   stats.score)
                put("correct", stats.correct)
                put("wrong",   stats.wrong)
                put("streak",  stats.streak)
            }.toString()
        }
    }

    // ── JSON helpers ──────────────────────────────────────────────────────────
    private fun serializeHistory(list: List<HistoryEntry>): String {
        val arr = JSONArray()
        list.forEach { e ->
            arr.put(JSONObject().apply {
                put("id",          e.id)
                put("cipherName",  e.cipherName)
                put("input",       e.input)
                put("output",      e.output)
                put("direction",   e.direction)
                put("timestamp",   e.timestamp)
            })
        }
        return arr.toString()
    }

    private fun parseHistory(json: String): List<HistoryEntry> {
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                HistoryEntry(
                    id          = obj.optString("id", UUID.randomUUID().toString()),
                    cipherName  = obj.getString("cipherName"),
                    input       = obj.getString("input"),
                    output      = obj.getString("output"),
                    direction   = obj.getString("direction"),
                    timestamp   = obj.getString("timestamp")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseStats(json: String): ChallengeStats {
        return try {
            val obj = JSONObject(json)
            ChallengeStats(
                score   = obj.optInt("score"),
                correct = obj.optInt("correct"),
                wrong   = obj.optInt("wrong"),
                streak  = obj.optInt("streak")
            )
        } catch (e: Exception) { ChallengeStats() }
    }
}
