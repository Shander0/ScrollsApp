package ru.inspirationpoint.scrollsapp.ui.fragments

import android.R.attr.defaultValue
import android.R.attr.key
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.inspirationpoint.scrollsapp.App
import ru.inspirationpoint.scrollsapp.R
import ru.inspirationpoint.scrollsapp.Spell
import ru.inspirationpoint.scrollsapp.ui.MainActivity


class SpellDetailsFragment: Fragment(R.layout.fragment_info_spell) {

    private var spellId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            spellId = bundle.getInt("id", 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spell = App.realm!!.where(Spell::class.java).containsValue("id", spellId).findFirst()
        if (spell != null) {
            view.findViewById<ImageView>(R.id.spell_picture_big).setImageResource(
                activity?.resources!!.getIdentifier(
                    spell.pic,
                    "drawable",
                    activity?.packageName
                )
            )
            view.findViewById<TextView>(R.id.text_cast).visibility = if (spell.id == 60001 || spell.id == 20007) View.GONE else View.VISIBLE
            view.findViewById<TextView>(R.id.spell_title).text = spell.name + "\nМана: " + spell.manaCost
            val descText = view.findViewById<TextView>(R.id.spell_desc)
            descText.text = spell.desc
            descText.movementMethod = ScrollingMovementMethod()
            view.findViewById<TextView>(R.id.text_back).setOnClickListener { (activity as MainActivity)
                .transactFragments(MainActivity.FragmentsList.SpellsList, null)  }
            view.findViewById<TextView>(R.id.text_cast).setOnClickListener {
                (activity as MainActivity).transactFragments(MainActivity.FragmentsList.Effect, Bundle().apply { putInt("id", spellId)  })
            }
        }
    }

}