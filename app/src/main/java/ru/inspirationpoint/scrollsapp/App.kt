package ru.inspirationpoint.scrollsapp

import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration

class App: Application() {

    companion object {
        var ctx: Context? = null
        var realm: Realm? = null
        fun initRealm() {
            val realmName = "Scrolls"
            val config = RealmConfiguration.Builder()
                .name(realmName)
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
//                .compactOnLaunch()
//                .inMemory()
                .build()
            realm = Realm.getInstance(config)
        }

        fun stopRealm() {
            realm?.close()
        }
    }

    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext
        Realm.init(this)
    }


    
}