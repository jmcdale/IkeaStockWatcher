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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
        default: L?
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

    inline fun <reified I, reified L : List<I>> flowJsonableList(key: String): Flow<List<I>> {
        val type = Types.newParameterizedType(L::class.java, I::class.java)
        return flowJsonableList(key, type)
    }

    fun <T> flowJsonableList(key: String, type: Type): Flow<T> {
        val adapter = moshi.adapter<T>(type)
        return dataStore.data.map { prefs ->
            prefs[stringPreferencesKey(key)]?.let { adapter.fromJson(it) }
        }.filterNot { it == null } as Flow<T>
    }

    inline fun <reified D> flowJsonable(key: String): Flow<D?> {
        return flowJsonable(key, D::class.java)
    }

    inline fun <reified D> flowJsonable(key: String, default: D): Flow<D> {
        return flowJsonable(key, default, D::class.java)
    }

    fun <D> flowJsonable(key: String, type: Class<D>): Flow<D?> {
        val adapter = moshi.adapter(type)
        return dataStore.data.map { prefs ->
            prefs[stringPreferencesKey(key)]?.let { adapter.fromJson(it) }
        }
    }

    fun <D> flowJsonable(key: String, default: D, type: Class<D>): Flow<D> {
        val adapter = moshi.adapter(type)
        return dataStore.data.map { prefs ->
            prefs[stringPreferencesKey(key)]?.let { adapter.fromJson(it) } ?: default
        }
    }

    companion object {
        const val DEFAULT_STORAGE = "data"
    }
}

@JsonClass(generateAdapter = true)
data class LocalStorageTestData(val number: Int, val string: String)

class LocalStorageTest(private val localStorage: LocalStorage) {

    suspend fun runTests() {
        Timber.d("JOSH - Running LocalStorageTest tests")

        try {
            testSaveLoadJsonable()
            testSaveLoadJsonableList()
            testLoadUnsavedJsonable()
            testLoadUnsavedJsonableList()
            testLoadUnsavedListWithNullDefault()
            testFlow()
        } catch (e: Exception) {
            Timber.e(e, "JOSH - error")
        }
    }

    suspend fun testSaveLoadJsonable() {
        Timber.d("JOSH - LocalStorageTest - saveLoadJsonable")
        localStorage.saveJsonable(TEST_KEY_ONE, DATA_ONE)
        localStorage.saveJsonable(TEST_KEY_TWO, DATA_TWO)

        val one = localStorage.loadJsonable<LocalStorageTestData?>(TEST_KEY_ONE, null)
        val two = localStorage.loadJsonable<LocalStorageTestData?>(TEST_KEY_TWO, null)

        require(one == DATA_ONE)
        require(two == DATA_TWO)
        Timber.d("JOSH - LocalStorageTest - saveLoadJsonable - PASS")
    }

    suspend fun testSaveLoadJsonableList() {
        Timber.d("JOSH - LocalStorageTest - saveLoadJsonableList")
        localStorage.saveJsonableList(TEST_KEY_LIST_ONE, DATA_LIST_ONE)

        val three =
            localStorage.loadJsonableList<LocalStorageTestData, List<LocalStorageTestData>>(
                TEST_KEY_LIST_ONE,
                emptyList()
            )

        require(three == DATA_LIST_ONE)
        Timber.d("JOSH - LocalStorageTest - saveLoadJsonableList - PASS")
    }

    suspend fun testLoadUnsavedJsonable() {
        Timber.d("JOSH - LocalStorageTest - loadUnsavedJsonable")
        val unsaved = localStorage.loadJsonable<LocalStorageTestData?>(TEST_KEY_THREE, null)

        require(unsaved == null)
        Timber.d("JOSH - LocalStorageTest - loadUnsavedJsonable - PASS")
    }

    suspend fun testLoadUnsavedJsonableList() {
        Timber.d("JOSH - LocalStorageTest - loadUnsavedJsonableList")
        val unsavedList =
            localStorage.loadJsonableList<LocalStorageTestData, List<LocalStorageTestData>>(
                TEST_KEY_THREE,
                emptyList()
            )

        requireNotNull(unsavedList)
        require(unsavedList.isEmpty())
        Timber.d("JOSH - LocalStorageTest - loadUnsavedJsonableList - PASS")
    }

    suspend fun testLoadUnsavedListWithNullDefault() {
        Timber.d("JOSH - LocalStorageTest - loadUnsavedListWithNullDefault")
        val unsavedListNull =
            localStorage.loadJsonableList<LocalStorageTestData, List<LocalStorageTestData>>(
                TEST_KEY_THREE,
                null
            )

        require(unsavedListNull == null)
        Timber.d("JOSH - LocalStorageTest - loadUnsavedListWithNullDefault - PASS")
    }

    suspend fun testFlow() {
        val flow = localStorage.flowJsonable<LocalStorageTestData>(TEST_KEY_FLOW_ONE)
        GlobalScope.launch {
            flow.collect {
                Timber.d("JOSH - Flow - $it")
            }
        }

        GlobalScope.launch {
            localStorage.saveJsonable(TEST_KEY_FLOW_ONE, DATA_ONE)
            delay(3000)
            localStorage.saveJsonable(TEST_KEY_FLOW_ONE, DATA_TWO)
            delay(3000)
            localStorage.saveJsonable(TEST_KEY_FLOW_ONE, DATA_THREE)
        }
    }

    companion object {
        private const val TEST_KEY_ONE = "TEST_KEY_ONE"
        private const val TEST_KEY_TWO = "TEST_KEY_TWO"
        private const val TEST_KEY_THREE = "TEST_KEY_THREE"

        private const val TEST_KEY_LIST_ONE = "TEST_KEY_LIST_ONE"

        private const val TEST_KEY_FLOW_ONE = "TEST_KEY_FLOW_ONE"
        private const val TEST_KEY_FLOW_TWO = "TEST_KEY_FLOW_TWO"
        private const val TEST_KEY_FLOW_THREE = "TEST_KEY_FLOW_THREE"

        private val DATA_ONE = LocalStorageTestData(1, "A")
        private val DATA_TWO = LocalStorageTestData(2, "B")
        private val DATA_THREE = LocalStorageTestData(3, "C")

        private val DATA_LIST_ONE = listOf(DATA_ONE, DATA_TWO, DATA_THREE)
    }
}