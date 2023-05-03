package ru.inspirationpoint.scrollsapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.inspirationpoint.scrollsapp.App
import ru.inspirationpoint.scrollsapp.App.Companion.realm
import ru.inspirationpoint.scrollsapp.QRProto.Companion.cmdField
import ru.inspirationpoint.scrollsapp.QRProto.Companion.commandAdmin
import ru.inspirationpoint.scrollsapp.QRProto.Companion.commandNewSpell
import ru.inspirationpoint.scrollsapp.QRProto.Companion.commandPotion
import ru.inspirationpoint.scrollsapp.QRProto.Companion.commandRegen
import ru.inspirationpoint.scrollsapp.R
import ru.inspirationpoint.scrollsapp.Spell
import ru.inspirationpoint.scrollsapp.User
import ru.inspirationpoint.scrollsapp.ui.adapters.AdminSpellAdapter
import ru.inspirationpoint.scrollsapp.ui.adapters.SpellsAdapter
import ru.inspirationpoint.scrollsapp.ui.fragments.*
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity(), SpellsAdapter.OnSpellChooseListener {

    lateinit var currentSpell:Spell
    val currentMana = MutableLiveData(0)
    lateinit var qrBtn: TextView
    lateinit var backBtn: TextView
    val handler = Handler()

    enum class FragmentsList(val fragmentName: String) {
        Admin("AdminFragment"),
        SpellsList("ListFragment"),
        SpellDetails("DetailsFragment"),
        Scan("ScanFragment"),
        Effect("EffectsFragment"),
        NewSpell("NewSpellFragment")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        qrBtn = findViewById(R.id.btn_qr)
        qrBtn.setOnClickListener { transactFragments(
            FragmentsList.Scan,
            null
        ) }

        backBtn = findViewById(R.id.btn_qr_back)
        backBtn.setOnClickListener { transactFragments(
            FragmentsList.SpellsList,
            null
        ) }
        currentMana.observe(this, Observer {
            val u = realm!!.where(User::class.java).findFirst()!!
            findViewById<TextView>(R.id.mana_text).text = "${currentMana.value}/${u.maxMana}"
        })
    }

    fun scanResultGained(result: String) {
        val resultArray = result.split("_")
        val commandPair = resultArray[0].split("=")
        if (commandPair[0] == cmdField) {
            when (commandPair[1]) {
                commandAdmin -> {
                    transactFragments(FragmentsList.Admin, null)
                }
                commandNewSpell -> {
                    runOnUiThread {
                        val valuePair = resultArray[1].split("=")
                        if (valuePair[1] != "0") {
                            realm!!.executeTransaction { realm ->
                                val s =
                                    realm.where(Spell::class.java).containsValue("id", valuePair[1].toInt())
                                        .findFirst()!!
                                if (valuePair[1].toInt() != 60001 && valuePair[1].toInt() != 20007) {
                                    s.isActive = true
                                } else {
                                    s.manaCost++
                                    s.pointsCost++
                                    s.isActive = true
                                }
                            }
                        }
                        transactFragments(FragmentsList.SpellsList, null)
                    }
                }
                commandPotion -> {
                    val valuePair = resultArray[1].split("=")
                    if (valuePair[1] == "0") {
                        runOnUiThread {
                            realm!!.executeTransaction { realm ->
                                val u = realm.where(User::class.java).findFirst()!!
                                u.currentMana = u.maxMana
                                u.lastTime = System.currentTimeMillis()
                                currentMana.value = u.currentMana
                            }
                            transactFragments(FragmentsList.SpellsList, null)
                        }
                    } else {
                        runOnUiThread {
                            realm!!.executeTransaction { realm ->
                                val u = realm.where(User::class.java).findFirst()!!
                                if (u.currentMana < u.maxMana - 2) {
                                    u.currentMana += 3

                                } else {
                                    u.currentMana = u.maxMana
                                }
                                u.lastTime = System.currentTimeMillis()
                                currentMana.value = u.currentMana
                            }
                            transactFragments(FragmentsList.SpellsList, null)
                        }

                    }
                }
                commandRegen -> {

                }
            }
        }
    }



    fun transactFragments(initName: FragmentsList, bundle: Bundle?) {
        when (initName) {
            FragmentsList.Admin -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<AdminFragment>(R.id.fragment)
                }
                backBtn.visibility = View.VISIBLE
                qrBtn.visibility = View.GONE
            }
            FragmentsList.NewSpell -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<NewSpellFragment>(R.id.fragment)
                }
                backBtn.visibility = View.VISIBLE
                qrBtn.visibility = View.GONE
            }
            FragmentsList.SpellDetails -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<SpellDetailsFragment>(R.id.fragment, "details", bundle)
                }
                backBtn.visibility = View.GONE
                qrBtn.visibility = View.VISIBLE
            }
            FragmentsList.SpellsList -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<SpellsFragment>(R.id.fragment)
                }
                backBtn.visibility = View.GONE
                qrBtn.visibility = View.VISIBLE
            }
            FragmentsList.Effect -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<EffectFragment>(R.id.fragment, "Effect", bundle)
                }
                backBtn.visibility = View.VISIBLE
                qrBtn.visibility = View.GONE
            }
            FragmentsList.Scan -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<ScanFragment>(R.id.fragment)
                    backBtn.visibility = View.VISIBLE
                    qrBtn.visibility = View.GONE
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        App.initRealm()
//        addChangeListenerToRealm(App.realm!!)
//        realm!!.executeTransaction { realm ->
//            realm.deleteAll()
//        }
        if (realm!!.where(Spell::class.java).count() < 2) {
            fillSpells()
        }
        if (realm!!.where(User::class.java).count() < 1) {
            realm!!.executeTransaction { realm ->
                realm.createObject(User::class.java).lastTime = System.currentTimeMillis()
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 111)
        }
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<SpellsFragment>(R.id.fragment)
        }
        restartManaTimer()
    }

    fun restartManaTimer() {
        val user = realm!!.where(User::class.java).findFirst()!!
        val timer = (user.regenTimer*1000).toLong()
        val lastTimer = user.lastTime
        val currentTimer = System.currentTimeMillis()
        val u = realm!!.where(User::class.java).findFirst()!!
        currentMana.value = u.currentMana
        if (currentTimer - lastTimer > timer) {
            val ticks = ((currentTimer - lastTimer) / (timer)).toInt()
            for (i in 0..ticks) {
                increaseMana()
            }
        }
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(object : Runnable {
            override fun run() {
                increaseMana()
                handler.postDelayed(this, timer)
            }
        }, timer)
    }

//    fun addChangeListenerToRealm(realm : Realm) {
//        // all tasks in the realm
//        val tasks : RealmResults<Spell> = realm.where(Spell::class.java).findAllAsync()
//        tasks.addChangeListener { _, changeSet ->
//            // process deletions in reverse order if maintaining parallel data structures so indices don't change as you iterate
//            val deletions = changeSet.deletionRanges
//            for (i in deletions.indices.reversed()) {
//                val range = deletions[i]
//                Log.wtf("QUICKSTART", "Deleted range: ${range.startIndex} to ${range.startIndex + range.length - 1}")
//            }
//            val insertions = changeSet.insertionRanges
//            for (range in insertions) {
//                Log.wtf("QUICKSTART", "Inserted range: ${range.startIndex} to ${range.startIndex + range.length - 1}")
//            }
//            val modifications = changeSet.changeRanges
//            for (range in modifications) {
//                Log.wtf("QUICKSTART", "Updated range: ${range.startIndex} to ${range.startIndex + range.length - 1}")
//            }
//        }
//    }

    fun fillSpells() {
        Log.wtf("FILL CALLED", "+++")
        realm!!.executeTransaction { realm ->
            try {
                val inputStream: InputStream = assets.open("initialSpells.json")
                realm.createAllFromJson(Spell::class.java, inputStream)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    fun increaseMana() {
        val user = realm!!.where(User::class.java).findFirst()!!
        if (user.currentMana < user.maxMana) {
            realm!!.executeTransaction { realm ->
                val u = realm.where(User::class.java).findFirst()!!
                u.currentMana++
                u.lastTime = System.currentTimeMillis()
                currentMana.value = u.currentMana
            }
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
        App.stopRealm()
    }

    override fun onSpellChoose(item: Spell) {
        currentSpell = item
        val bundle = Bundle()
        bundle.putInt("id", item.id)
        transactFragments(FragmentsList.SpellDetails, bundle)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}