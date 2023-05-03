package ru.inspirationpoint.scrollsapp.ui.fragments

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.inspirationpoint.scrollsapp.App
import ru.inspirationpoint.scrollsapp.R
import ru.inspirationpoint.scrollsapp.Spell
import ru.inspirationpoint.scrollsapp.User
import ru.inspirationpoint.scrollsapp.ui.MainActivity


class EffectFragment : Fragment(R.layout.fragment_effect) {

    var spellId = 0
    lateinit var player: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            spellId = bundle.getInt("id", 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spell = App.realm!!.where(Spell::class.java).containsValue("id", spellId).findFirst()!!
        view.findViewById<TextView>(R.id.tv_title).text = spell.name
        val descText = view.findViewById<TextView>(R.id.tv_desc)
        descText.text = spell.effect
        descText.movementMethod = ScrollingMovementMethod()
        val afd: AssetFileDescriptor = requireActivity().assets.openFd(spell.sound)
        player = MediaPlayer()
        player.setDataSource(afd.fileDescriptor,afd.startOffset,afd.length)
        player.prepare()
        player.start()
        App.realm!!.executeTransaction { realm ->
            val u = realm.where(User::class.java).findFirst()!!
            u.currentMana -= spell.manaCost
            (requireActivity() as MainActivity).currentMana.value = u.currentMana
        }
    }

}