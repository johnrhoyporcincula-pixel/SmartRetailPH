package com.example.smartretailph.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object ReceiptsRepository {
    private const val PREFS_NAME = "receipts_prefs"
    private const val KEY_RECEIPTS = "receipts"

    private lateinit var prefs: SharedPreferences

    private val _receipts = MutableStateFlow<Map<String, String>>(emptyMap())
    val receipts: StateFlow<Map<String, String>> = _receipts.asStateFlow()

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        load()
        isInitialized = true
    }

    fun saveReceipt(orderId: String, receiptText: String) {
        val updated = _receipts.value.toMutableMap()
        updated[orderId] = receiptText
        _receipts.value = updated
        persist(updated)
    }

    private fun load() {
        val json = prefs.getString(KEY_RECEIPTS, null) ?: return
        runCatching {
            val array = JSONArray(json)
            val map = mutableMapOf<String, String>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                map[obj.getString("orderId")] = obj.getString("text")
            }
            _receipts.value = map
        }
    }

    private fun persist(map: Map<String, String>) {
        val array = JSONArray()
        map.forEach { (orderId, text) ->
            val obj = JSONObject()
            obj.put("orderId", orderId)
            obj.put("text", text)
            array.put(obj)
        }
        prefs.edit { putString(KEY_RECEIPTS, array.toString()) }
    }
}
