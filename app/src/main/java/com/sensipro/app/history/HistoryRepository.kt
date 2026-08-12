package com.sensipro.app.history

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.historyDataStore by preferencesDataStore(name = "sensipro_history")

class HistoryRepository(private val context: Context) {

    private object Keys {
        val ENTRIES = stringSetPreferencesKey("entries")
    }

    val historyFlow: Flow<List<HistoryEntry>> = context.historyDataStore.data.map { prefs ->
        val raw = prefs[Keys.ENTRIES] ?: emptySet()
        raw.mapNotNull { HistoryEntry.decode(it) }
            .sortedByDescending { it.timestampMillis }
    }

    suspend fun addEntry(entry: HistoryEntry) {
        context.historyDataStore.edit { prefs ->
            val current = prefs[Keys.ENTRIES]?.toMutableSet() ?: mutableSetOf()
            current.add(entry.encode())
            prefs[Keys.ENTRIES] = current
        }
    }

    suspend fun deleteEntry(id: String) {
        context.historyDataStore.edit { prefs ->
            val current = prefs[Keys.ENTRIES]?.toMutableSet() ?: mutableSetOf()
            current.removeAll { HistoryEntry.decode(it)?.id == id }
            prefs[Keys.ENTRIES] = current
        }
    }

    suspend fun clearAll() {
        context.historyDataStore.edit { prefs ->
            prefs[Keys.ENTRIES] = emptySet()
        }
    }

    fun newId(): String = UUID.randomUUID().toString()
}
