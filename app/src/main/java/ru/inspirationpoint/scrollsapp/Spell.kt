package ru.inspirationpoint.scrollsapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Spell(var id: Int = 1,
                 var name: String = "",
                 var desc:String = "",
                 var effect:String = "",
                 var manaCost: Int = 0,
                 var pointsCost: Int = 0,
                 var pic:String = "",
                 var sound:String = "",
                 var isActive:Boolean = false): RealmObject() {

}