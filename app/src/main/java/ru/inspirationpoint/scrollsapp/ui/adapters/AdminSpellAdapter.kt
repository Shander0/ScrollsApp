package ru.inspirationpoint.scrollsapp.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import ru.inspirationpoint.scrollsapp.App
import ru.inspirationpoint.scrollsapp.R
import ru.inspirationpoint.scrollsapp.Spell
import ru.inspirationpoint.scrollsapp.ui.fragments.AdminFragment

class AdminSpellAdapter(fr: AdminFragment): RecyclerView.Adapter<AdminSpellAdapter.AdminViewHolder>() {

    var items: RealmResults<Spell>? = null
    var fragment: AdminFragment = fr
    private val listener: OnAdminClickListener = fr

    fun setSpells(it: RealmResults<Spell>) {
        items = it
        notifyDataSetChanged()
    }

    fun update() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val spellItem: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_spell, parent, false)
        return AdminViewHolder(fragment.requireActivity(), spellItem)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        items?.get(position)?.let { holder.setItem(it)}
    }

    override fun getItemCount(): Int {
        return items?.size!!
    }

    inner class AdminViewHolder(private var activity: Activity, view: View):
        RecyclerView.ViewHolder(view) {
        private var spellNameText: TextView = view.findViewById(R.id.spell_name)
        private var spellView: View = view
        private var spellPicture: ImageView = view.findViewById(R.id.spell_pic)
        private var spellManaText: TextView = view.findViewById(R.id.spell_cost)
        private var spellIsActive:CheckBox = view.findViewById(R.id.spell_check)
        private var spellCount: TextView = view.findViewById(R.id.spell_count)

        fun setItem(spell: Spell) {
            spellNameText.text = spell.name
            val points = spell.pointsCost
            spellManaText.text = "Очки: $points"
            spellPicture.setImageResource(
                activity.resources.getIdentifier(
                    spell.pic,
                    "drawable",
                    activity.packageName
                )
            )
            if (spell.id == 60001 || spell.id == 20007) {
                spellIsActive.visibility = View.GONE
                spellCount.visibility = View.VISIBLE
                spellCount.text = "x ${spell.manaCost}"
                spellView.setOnLongClickListener {
                    App.realm!!.executeTransaction { realm ->
                        val s = realm.where(Spell::class.java).containsValue("id", spell.id)
                            .findFirst()!!
                        if (s.manaCost > 1) {
                            s.isActive = true
                            s.manaCost--
                            s.pointsCost--
                        } else {
                            s.isActive = false
                            s.manaCost = 0
                            s.pointsCost--
                        }
                    }
                    setSpells(App.realm!!.where(Spell::class.java).findAll())
                    listener.onSpellClick(spell)
                    true
                }
            } else {
                spellIsActive.visibility = View.VISIBLE
                spellCount.visibility = View.GONE
                spellIsActive.isChecked = spell.isActive
                spellIsActive.isClickable = false
            }
            spellView.setOnClickListener {
                if (spell.id != 60001 && spell.id != 20007) {
                    App.realm!!.executeTransaction { realm ->
                        val s = realm.where(Spell::class.java).containsValue("id", spell.id)
                            .findFirst()!!
                        s.isActive = !spell.isActive
                    }
                } else {
                    App.realm!!.executeTransaction { realm ->
                        val s = realm.where(Spell::class.java).containsValue("id", spell.id)
                            .findFirst()!!
                        s.manaCost++
                        s.pointsCost++
                        s.isActive = true
                    }
                }
                setSpells(App.realm!!.where(Spell::class.java).findAll())
                listener.onSpellClick(spell)
            }
        }

    }

    interface OnAdminClickListener {
        fun onSpellClick(spell:Spell)
    }
}