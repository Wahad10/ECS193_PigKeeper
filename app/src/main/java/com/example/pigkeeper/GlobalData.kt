package com.example.pigkeeper

import android.app.Application
import kotlin.collections.mutableMapOf

class GlobalData: Application() {
    lateinit var players : ArrayList<String>
    var sittingOut = mutableMapOf<String, Boolean>()
    var score = ArrayList<Int>(0)
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