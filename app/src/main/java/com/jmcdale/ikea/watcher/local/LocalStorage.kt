package com.jmcdale.ikea.watcher.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jmcdale.ikea.watcher.local.LocalStorage.Companion.DEFAULT_STORAGE
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import java.lang.reflect.Type

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DEFAULT_STORAGE)

class LocalStorage(private val context: Context, private val moshi: Moshi) {

    private val dataStore: DataStore<Preferences>
        get() = context.dataStore

    suspend inline fun <reified V, reified L : List<V>> saveJsonableList(
        key: String,
        value: L
    ): Boolean {
        val type = Types.newParameterizedType(L::class.java, V::class.java)
        return saveJsonable(key, value, type)
    }

    suspend inline fun <reified V> saveJsonable(key: String, value: V): Boolean {
        return saveJsonable(key, value, V::class.java)
    }

    suspend fun <V> saveJsonable(key: String, value: V, type: Class<V>): Boolean {
        val adapter = moshi.adapter(type)
        // TODO try/catch exceptions
        dataStore.edit { it[stringPreferencesKey(key)] = adapter.toJson(value) }
        return true
    }

    suspend fun <V> saveJsonable(key: String, value: V, type: Type): Boolean {
        val adapter = moshi.adapter<V>(type)
        // TODO try/catch exceptions
        dataStore.edit { it[stringPreferencesKey(key)] = adapter.toJson(value) }
        return true
    }

    suspend inline fun <reified I, reified L : List<I>> loadJsonableList(
        key: String,
        default: L
    ): L? {
        val type = Types.newParameterizedType(L::class.java, I::class.java)
        return loadJsonable(key, default, type)
    }

    suspend inline fun <reified D> loadJsonable(key: String, default: D): D? {
        return loadJsonable(key, default, D::class.java)
    }

    suspend fun <D> loadJsonable(key: String, default: D?, type: Class<D>): D? {
        return dataStore.data.firstOrNull()?.let { prefs ->
            val adapter = moshi.adapter(type)
            prefs[stringPreferencesKey(key)]?.let { adapter.fromJson(it) }
        } ?: default
    }

    suspend fun <V> loadJsonable(key: String, default: V?, type: Type): V? {
        return dataStore.data.firstOrNull()?.let { prefs ->
            val adapter = moshi.adapter<V>(type)
            prefs[stringPreferencesKey(key)]?.let { adapter.fromJson(it) }
        } ?: default
    }

    companion object {
        const val DEFAULT_STORAGE = "data"
    }
}

@JsonClass(generateAdapter = true)
data class LocalStorageTestData(val number: Int, val string: String)

class LocalStorageTest(private val localStorage: LocalStorage) {
    suspend fun runTests() {
        Timber.d("JOSH - Running LocalStorageTest")
        localStorage.saveJsonable(TEST_KEY_ONE, DATA_ONE)
        localStorage.saveJsonable(TEST_KEY_TWO, DATA_TWO)
        localStorage.saveJsonableList(TEST_KEY_LIST_ONE, DATA_LIST_ONE)

        Timber.d("JOSH - Data Saved")

        try {
            Timber.d("JOSH - Loading One")
            val one = localStorage.loadJsonable<LocalStorageTestData?>(TEST_KEY_ONE, null)
            Timber.d("JOSH - Loading Two")
            val two = localStorage.loadJsonable<LocalStorageTestData?>(TEST_KEY_TWO, null)
            Timber.d("JOSH - Loading Three")
            val three =
                localStorage.loadJsonableList<LocalStorageTestData, List<LocalStorageTestData>>(
                    TEST_KEY_LIST_ONE,
                    emptyList()
                )

            Timber.d("JOSH - Loading Unsaved")
            val unsaved = localStorage.loadJsonable<LocalStorageTestData?>(TEST_KEY_THREE, null)

            Timber.d("JOSH - Loading Unsaved List")
            val unsavedList =
                localStorage.loadJsonableList<LocalStorageTestData, List<LocalStorageTestData>>(
                    TEST_KEY_THREE,
                    emptyList()
                )

            Timber.d("JOSH - Data Loaded")


            require(one == DATA_ONE)
            require(two == DATA_TWO)
            require(three == DATA_LIST_ONE)
            require(unsaved == null)
            requireNotNull(unsavedList)
            require(unsavedList.isEmpty())

            Timber.d("JOSH - Data Validated")


        } catch (e: Exception) {
            Timber.e(e, "JOSH - error")
        }

    }

    companion object {
        private const val TEST_KEY_ONE = "TEST_KEY_ONE"
        private const val TEST_KEY_TWO = "TEST_KEY_TWO"
        private const val TEST_KEY_THREE = "TEST_KEY_THREE"

        private const val TEST_KEY_LIST_ONE = "TEST_KEY_LIST_ONE"

        private val DATA_ONE = LocalStorageTestData(1, "A")
        private val DATA_TWO = LocalStorageTestData(2, "B")
        private val DATA_THREE = LocalStorageTestData(3, "C")

        private val DATA_LIST_ONE = listOf(DATA_ONE, DATA_TWO, DATA_THREE)
    }
}