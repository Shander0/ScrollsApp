package ru.inspirationpoint.scrollsapp

import io.realm.RealmObject

open class User (var regenTimer:Int = 300,
                 var currentMana:Int = 0,
                 var maxMana:Int = 3,
                 var lastTime:Long = 0) :RealmObject()