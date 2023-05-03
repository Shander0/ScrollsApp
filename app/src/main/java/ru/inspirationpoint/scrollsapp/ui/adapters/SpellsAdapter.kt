package ru.inspirationpoint.scrollsapp.ui.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.realm.RealmList
import io.realm.RealmResults
import ru.inspirationpoint.scrollsapp.R
import ru.inspirationpoint.scrollsapp.Spell
import ru.inspirationpoint.scrollsapp.ui.MainActivity

class SpellsAdapter(activity: MainActivity): RecyclerView.Adapter<SpellsAdapter.SpellsViewHolder>() {

    var items: RealmResults<Spell>? = null
    var mActivity: MainActivity = activity
    var currentMana = 0
    private val listener: OnSpellChooseListener = activity

    fun setSpells(it: RealmResults<Spell>) {
        items = it
        notifyDataSetChanged()
    }

    fun update(mana: Int) {
        currentMana = mana
        notifyDataSetChanged()
    }

    fun clear() {
        items!!.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellsViewHolder {
        val spellItem: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spell, parent, false)
        return SpellsViewHolder(mActivity, spellItem)
    }

    override fun onBindViewHolder(holder: SpellsViewHolder, position: Int) {
        items?.get(position)?.let { holder.setItem(it, currentMana)}
    }

    override fun getItemCount(): Int {
        return items?.size!!
    }

    inner class SpellsViewHolder(private var activity: Activity, view: View):
        ViewHolder(view) {

        private var spellNameText: TextView = view.findViewById(R.id.spell_name)
        private var spellView: View = view
        private var spellPicture: ImageView = view.findViewById(R.id.spell_pic)
        private var spellManaText: TextView = view.findViewById(R.id.spell_cost)
        private var inactiveView: View = view.findViewById(R.id.inactive_view)

        fun setItem(spell: Spell, currentMana: Int) {
            spellNameText.text = spell.name
            val mana = spell.manaCost
            if (spell.id == 60001 || spell.id == 20007) {
                spellManaText.text = "Количество: $mana"
            } else {
                spellManaText.text = "Стоимость: $mana"
            }
            spellPicture.setImageResource(
                activity.resources.getIdentifier(
                    spell.pic,
                    "drawable",
                    activity.packageName
                )
            )
            if (mana > currentMana) {
                inactiveView.visibility = View.VISIBLE
            } else {
                inactiveView.visibility = View.GONE
                spellView.setOnClickListener { listener.onSpellChoose(spell) }
            }

        }

    }

    interface OnSpellChooseListener {
        fun onSpellChoose(item: Spell)
    }
}