package ru.inspirationpoint.scrollsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.inspirationpoint.scrollsapp.App
import ru.inspirationpoint.scrollsapp.R
import ru.inspirationpoint.scrollsapp.Spell
import ru.inspirationpoint.scrollsapp.User
import ru.inspirationpoint.scrollsapp.ui.MainActivity
import ru.inspirationpoint.scrollsapp.ui.adapters.AdminSpellAdapter
import kotlin.properties.Delegates

class AdminFragment: Fragment(R.layout.fragment_admin), AdminSpellAdapter.OnAdminClickListener {

    lateinit var currText: TextView
    lateinit var maxText: TextView
    lateinit var regenText: TextView
    lateinit var currBar: SeekBar
    lateinit var maxBar: SeekBar
    lateinit var regenBar: SeekBar
    lateinit var spellsRecycler: RecyclerView
    lateinit var pointsText: TextView
    var maxMana = 0
    var currentPoints = 0
    lateinit var adapter: AdminSpellAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tv_new).setOnClickListener { (activity as MainActivity).transactFragments(MainActivity.FragmentsList.NewSpell, null) }
        val user = App.realm!!.where(User::class.java).findFirst()!!
        currText = view.findViewById(R.id.tv_curr_mana)
        maxText = view.findViewById(R.id.tv_max_mana)
        regenText = view.findViewById(R.id.tv_regen_mana)
        currBar = view.findViewById(R.id.seek_curr)
        maxBar = view.findViewById(R.id.seek_max)
        regenBar = view.findViewById(R.id.seek_regen)
        spellsRecycler = view.findViewById(R.id.admin_spells)
        pointsText = view.findViewById(R.id.tv_points)
        currText.text = "Curr: ${user.currentMana}"
        maxText.text = "Max: ${user.maxMana}"
        regenText.text = "Reg: ${user.regenTimer/60}"
        currBar.max = user.maxMana
        maxMana = user.maxMana
        currBar.progress = user.currentMana
        maxBar.progress = user.maxMana
        regenBar.progress = user.regenTimer/30
        currBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val p1 = p0!!.progress
                if (p1 <= maxMana) {
                    App.realm!!.executeTransaction { realm ->
                        val u = realm.where(User::class.java).findFirst()!!
                        u.currentMana = p1
                        u.lastTime = System.currentTimeMillis()
                        (activity as MainActivity).currentMana.value = u.currentMana
                    }
                    currText.text = "Curr: $p1"
                }
            }
        })
        maxBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val p1 = p0!!.progress
                App.realm!!.executeTransaction { realm ->
                    val u = realm.where(User::class.java).findFirst()!!
                    u.maxMana = p1
                    u.lastTime = System.currentTimeMillis()
                    if (p1 < u.currentMana) {
                        u.currentMana = p1
                        (activity as MainActivity).currentMana.value = p1
                        currText.text = "Curr: $p1"
                    } else {
                        (activity as MainActivity).currentMana.value = u.currentMana
                    }

                }
                maxMana = p1
                currBar.max = p1
                maxText.text = "Max: $p1"
            }
        })
        regenBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val p1 = p0!!.progress
                if (p1 < 20) {
                    App.realm!!.executeTransaction { realm ->
                        val u = realm.where(User::class.java).findFirst()!!
                        u.regenTimer = (p1 + 1) * 30
                        u.lastTime = System.currentTimeMillis()
                        (activity as MainActivity).restartManaTimer()
                    }
                    regenText.text = "Reg: ${((p1+1) / 2.0)}"
                } else {
                    App.realm!!.executeTransaction { realm ->
                        val u = realm.where(User::class.java).findFirst()!!
                        u.regenTimer = 12000
                        u.lastTime = System.currentTimeMillis()
                        (activity as MainActivity).restartManaTimer()
                    }
                    regenText.text = "Reg: 200"
                }
            }
        })
        val spellsActive = App.realm!!.where(Spell::class.java).containsValue("isActive", true).findAll()
        for (spell in spellsActive) {
            currentPoints += spell.pointsCost
        }
        pointsText.text = "Очки мага: $currentPoints"

        adapter = AdminSpellAdapter(this)
        val recycler = view.findViewById<RecyclerView>(R.id.admin_spells)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        adapter.items = App.realm!!.where(Spell::class.java).findAll()
    }

    override fun onSpellClick(spell: Spell) {
        val spellsActive = App.realm!!.where(Spell::class.java).containsValue("isActive", true).findAll()
        currentPoints = 0
        for (s in spellsActive) {
            when (s.id) {
                60001 -> {
                    for ((i, _) in (0 until s.manaCost).withIndex()) {
                        currentPoints += 1 + i
                    }
                }
                20007 -> {
                    for ((i, _) in (0 until s.manaCost).withIndex()) {
                        currentPoints += 3 + i
                    }
                }
                else -> {
                    currentPoints += s.pointsCost
                }
            }

        }
        pointsText.text = "Очки мага: $currentPoints"
    }




}