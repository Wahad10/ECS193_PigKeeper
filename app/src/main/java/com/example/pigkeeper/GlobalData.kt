package com.example.pigkeeper

import android.app.Application
import kotlin.collections.mutableMapOf

class GlobalData: Application() {
    lateinit var players : ArrayList<String>
    var sittingOut = mutableMapOf<String, Boolean>()
    //Hashmap for current round score
    var nameToScore = HashMap<String, Int>(0)
    //Hashmap for overall session pot
    var nameToPot = HashMap<String, Int>(0)
    //keep track if the game session was ended, if so brand new game so we reset the pot in roll activity
    var endedGameSession = false

    //Roll Screen Variables To Keep Track of Current game (in case app closed)
    var rolledOnce: Boolean = false
    var wasFirstRoll: Boolean = false
    var currentPlayer: String = ""
    var currentPlayerLastTurnScore: Int = 0
    var currentPlayerLastRollStartScore: Int = 0
    var currentPlayerRollStartScore: Int = 0
    var currentPlayerNewScore: Int = 0
    var previousPlayer: String = ""
    var previousPlayerLastScore: Int = 0
    var endingPlayer: String = ""


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