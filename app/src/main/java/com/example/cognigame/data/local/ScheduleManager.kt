package com.example.cognigame.data.local

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

class ScheduleManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cognigame_schedule", Context.MODE_PRIVATE)

    fun saveCompletedItems(items: List<Pair<String, Boolean>>) {
        val jsonArray = JSONArray()
        items.forEach { (time, completed) ->
            val obj = org.json.JSONObject()
            obj.put("time", time)
            obj.put("completed", completed)
            jsonArray.put(obj)
        }
        prefs.edit().putString("completed_items", jsonArray.toString()).apply()
    }

    fun getCompletedItems(): Map<String, Boolean> {
        val result = mutableMapOf<String, Boolean>()
        val json = prefs.getString("completed_items", null) ?: return result
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                result[obj.getString("time")] = obj.getBoolean("completed")
            }
        } catch (e: Exception) {
            // ignore
        }
        return result
    }
}
