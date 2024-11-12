package kr.blugon.tjfinder.module

import android.content.Context
import kr.blugon.tjfinder.ui.screen.child.user.SettingType
import kr.blugon.tjfinder.ui.screen.child.user.SettingValueType


object SettingManager {

    private const val PREF_NAME = "setting"

    fun <T> setSetting (context: Context, settingType: SettingType<T>, value: T) {
        val sharedPreferences = context.getSharedPreferences("${PREF_NAME}${settingType.category.name}", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        when (settingType.valueType) {
            SettingValueType.String -> editor.putString(settingType.code, value as String)
            SettingValueType.Int -> editor.putInt(settingType.code, value as Int)
            SettingValueType.Long -> editor.putLong(settingType.code, value as Long)
            SettingValueType.Float -> editor.putFloat(settingType.code, value as Float)
            SettingValueType.Boolean -> editor.putBoolean(settingType.code, value as Boolean)
        }
        editor.apply()
    }

    fun <T> getSetting(context: Context, settingType: SettingType<T>): T {
        val sharedPreferences = context.getSharedPreferences("${PREF_NAME}${settingType.category.name}", Context.MODE_PRIVATE)
        return when (settingType.valueType) {
            SettingValueType.String -> sharedPreferences.getString(settingType.code, null) as T
            SettingValueType.Int -> sharedPreferences.getInt(settingType.code, settingType.defaultValue as Int) as T
            SettingValueType.Long -> sharedPreferences.getLong(settingType.code, settingType.defaultValue as Long) as T
            SettingValueType.Float -> sharedPreferences.getFloat(settingType.code, settingType.defaultValue as Float) as T
            SettingValueType.Boolean -> sharedPreferences.getBoolean(settingType.code, settingType.defaultValue as Boolean) as T
        }
    }
}