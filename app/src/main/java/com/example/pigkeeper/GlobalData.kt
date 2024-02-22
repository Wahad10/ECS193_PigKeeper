package com.example.pigkeeper

import android.app.Application
import kotlin.collections.mutableMapOf

class GlobalData: Application() {
    lateinit var players : ArrayList<String>
    var sittingOut = mutableMapOf<String, Boolean>()
    //var score = ArrayList<Int>(0)
    //ADDED A HASHMAP HERE, NEED TO REPLACE ALL OCCURENCES OF SCORE WITH THIS HASHMAP
    var nameToScore = HashMap<String, Int>(0)

    //Add rules here, idk what data structure you want




    companion object {
        lateinit var instance: GlobalData
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


}