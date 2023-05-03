package ru.inspirationpoint.scrollsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.inspirationpoint.scrollsapp.App
import ru.inspirationpoint.scrollsapp.R
import ru.inspirationpoint.scrollsapp.Spell
import ru.inspirationpoint.scrollsapp.ui.MainActivity
import ru.inspirationpoint.scrollsapp.ui.adapters.SpellsAdapter

class SpellsFragment: Fragment(R.layout.fragment_spells) {
    lateinit var adapter: SpellsAdapter
    lateinit var recycler: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SpellsAdapter(activity as MainActivity)
        recycler = view.findViewById(R.id.spells_list)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        adapter.setSpells(App.realm!!.where(Spell::class.java).containsValue("isActive", true).findAll())
        (activity as MainActivity).currentMana.observe(activity as MainActivity,
            { item -> adapter.update(item) })
    }

    override fun onResume() {
        super.onResume()
        adapter.setSpells(App.realm!!.where(Spell::class.java).containsValue("isActive", true).findAll())
    }

}