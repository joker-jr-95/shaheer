package com.aait.shaheer.data_layer.cache


import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Single

import android.content.Context
import com.aait.shaheer.helper.DomainIntegration
import com.google.gson.Gson
import com.kareem.datalayer.model.common.UserModel

val preferencesGateway by lazy { PreferencesGateway() }

const val PREFERENCES_NAME = "PREFERENCES_NAME"

class PreferencesGateway {

    inline fun <reified T : Any> save(key: String, value: T): Single<T> {
        return Single.fromCallable {
            DomainIntegration.getApplication()
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .apply { putValue(key, value) }
                .apply()
            value
        }
    }

    inline fun <reified T : Any> load(key: String, defaultValue: T): Single<T> {
        return Single.fromCallable {
            DomainIntegration.getApplication()
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .run { getValue(key, defaultValue) }
        }
    }

    fun clearAll(){
        DomainIntegration.getApplication()
            .getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
    fun isSaved(key: String): Single<Boolean> {
        return Single.fromCallable {
            DomainIntegration.getApplication()
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .contains(key)
        }
    }

    fun remove(key: String): Completable {
        return Completable.fromAction {
            DomainIntegration.getApplication()
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(key)
                .apply()
        }
    }

}

inline fun <reified T : Any> SharedPreferences.Editor.putValue(
    key: String,
    value: T
) {
    when (T::class) {
        Boolean::class -> putBoolean(key, value as Boolean)
        Int::class -> putInt(key, value as Int)
        Long::class -> putLong(key, value as Long)
        Float::class -> putFloat(key, value as Float)
        String::class -> putString(key, value as String)
        UserModel::class->putString(key, Gson().toJson(value))

        else -> throw UnsupportedOperationException("not supported preferences type")
    }
}

inline fun <reified T : Any?> SharedPreferences.getValue(
    key: String,
    defaultValue: T?
): T? {
    return when (T::class) {
        Boolean::class -> getBoolean(key, defaultValue as Boolean) as T
        Int::class -> getInt(key, defaultValue as Int) as T
        Long::class -> getLong(key, defaultValue as Long) as T
        Float::class -> getFloat(key, defaultValue as Float) as T
        String::class -> getString(key, defaultValue as String) as T
        UserModel::class->Gson().fromJson(getString(key,null), UserModel::class.java) as T?
        else -> throw UnsupportedOperationException("not supported preferences type")
    }
}