package com.example.cognigame.data.local

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class MemoryBookManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cognigame_memory_book", Context.MODE_PRIVATE)

    fun saveMembers(members: List<Triple<String, String, String>>) {
        val jsonArray = JSONArray()
        members.forEach { (name, relationship, colorName) ->
            val obj = JSONObject()
            obj.put("name", name)
            obj.put("relationship", relationship)
            obj.put("color", colorName)
            jsonArray.put(obj)
        }
        prefs.edit().putString("members", jsonArray.toString()).apply()
    }

    fun getMembers(): List<Triple<String, String, String>> {
        val result = mutableListOf<Triple<String, String, String>>()
        val json = prefs.getString("members", null) ?: return result
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                result.add(Triple(
                    obj.getString("name"),
                    obj.getString("relationship"),
                    obj.getString("color")
                ))
            }
        } catch (e: Exception) {
            // ignore
        }
        return result
    }
}
