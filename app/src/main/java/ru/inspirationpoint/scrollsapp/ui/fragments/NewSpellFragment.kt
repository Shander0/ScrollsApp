package ru.inspirationpoint.scrollsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import ru.inspirationpoint.scrollsapp.App
import ru.inspirationpoint.scrollsapp.R
import ru.inspirationpoint.scrollsapp.Spell
import ru.inspirationpoint.scrollsapp.ui.MainActivity

class NewSpellFragment: Fragment(R.layout.fragment_new_spell) {

    private var spSchool: SmartMaterialSpinner<String>? = null
    private var schoolList: MutableList<String>? = null
    private var spell = Spell()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spell.id = (App.realm!!.where(Spell::class.java).findAll().size + 2)*10000 + 1
        spell.isActive = true
        spell.pic = "restoration"
        spell.sound = "regen.mp3"

        spSchool = view.findViewById(R.id.spinner1)
        schoolList = ArrayList()

        schoolList?.add("Восстановление")
        schoolList?.add("Иллюзия")
        schoolList?.add("Изменение")
        schoolList?.add("Колдовство")
        schoolList?.add("Разрушение")
        schoolList?.add("Зачарование")

        spSchool?.item = schoolList

        spSchool?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        spell.pic = "restoration"
                        spell.sound = "regen.mp3"
                    }
                    1 -> {
                        spell.pic = "illusion"
                        spell.sound = "illusion.mp3"
                    }
                    2 -> {
                        spell.pic = "alteration"
                        spell.sound = "alter.mp3"
                    }
                    3 -> {
                        spell.pic = "conjuration"
                        spell.sound = "conjur.mp3"
                    }
                    4 -> {
                        spell.pic = "destruction"
                        spell.sound = "destruct.mp3"
                    }
                    5 -> {
                        spell.pic = "mysticism"
                        spell.sound = "mystic.mp3"
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        spSchool?.setSelection(0)
        view.findViewById<TextView>(R.id.btn_save).setOnClickListener {
            spell.name = view.findViewById<EditText>(R.id.edit_name).text.toString()
            spell.desc = view.findViewById<EditText>(R.id.edit_desc).text.toString()
            spell.effect = view.findViewById<EditText>(R.id.edit_effect).text.toString()
            spell.manaCost = view.findViewById<EditText>(R.id.edit_mana).text.toString().toInt()
            spell.pointsCost = view.findViewById<EditText>(R.id.edit_points).text.toString().toInt()
            App.realm!!.executeTransaction {
                it.insert(spell)
            }
            (activity as MainActivity).transactFragments(MainActivity.FragmentsList.Admin, null)
        }
    }

}