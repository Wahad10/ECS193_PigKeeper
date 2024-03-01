package com.example.pigkeeper

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GlobalData: Application() {
    var players : ArrayList<String> = arrayListOf()
    var sittingOut = mutableMapOf<String, Boolean>()
    //Hashmap for current round score
    var nameToScore = HashMap<String, Int>(0)
    //Hashmap for overall session pot
    var nameToPot = HashMap<String, Int>(0)
    //keep track if the game session was ended, if so brand new game so we reset the pot in roll activity
    var endedGameSession = true //WAS FALSE
    //keep track if the game round was ended, if so resume game goes to new players else resume goes to rollactivity midround
    var endedGameRound = true //WAS FALSE

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
    var mustNextRoll: Boolean = false
    var wasMustNextRoll: Boolean = false
    var lastRollWasForcedNextRoll: Boolean = false
    var mustNextPlayer: Boolean = false
    var wasMustNextPlayer: Boolean = false
    var disableDiceSelect: Boolean = false
    var lastRollWasDouble: Boolean = false
    var consecutiveDoubleRolls: Int = 0
    var previousPlayerLastRollWasDouble: Boolean = false
    var previousPlayerConsecutiveDoubleRolls: Int = 0
    var lastLastRollWasDouble: Boolean = false
    var textConsequenceBuilder = StringBuilder()
    var previousTextConsequence: String = ""
    var previousPreviousTextConsequence: String = ""
    //NEW Roll Screen Variables
    //have to initialize this to something initially
    var currentSpecialRuleCase = RulesActivity.SpecialRuleCase.ROLL_7
    var currentSpecialRuleConsequences: MutableList<RulesActivity.Consequence> = mutableListOf()


    //Add rules here, idk what data structure you want
    var rulesMap: MutableMap<RulesActivity.SpecialRuleCase, MutableList<RulesActivity.Consequence>> = mutableMapOf()


    // Shared preferences
    private val sharedPreferencesName = "MyPrefs"
    private lateinit var sharedPreferences: SharedPreferences



    companion object {
        lateinit var instance: GlobalData
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //Default rules (applies even if user never goes to rules screen)
        //Will be overridden in loaddata if user saved their own rules
        rulesMap[RulesActivity.SpecialRuleCase.ROLL_7] = mutableListOf(RulesActivity.Consequence.RESET_TURN_SCORE, RulesActivity.Consequence.LOSE_TURN)
        rulesMap[RulesActivity.SpecialRuleCase.SNAKE_EYES] = mutableListOf(RulesActivity.Consequence.RESET_SCORE_TO_ZERO, RulesActivity.Consequence.LOSE_TURN)
        rulesMap[RulesActivity.SpecialRuleCase.BOX_CARS] = mutableListOf(RulesActivity.Consequence.DOUBLE_POINTS, RulesActivity.Consequence.MUST_ROLL_AGAIN)
        rulesMap[RulesActivity.SpecialRuleCase.DOUBLE] = mutableListOf(RulesActivity.Consequence.DOUBLE_POINTS, RulesActivity.Consequence.MUST_ROLL_AGAIN)
        rulesMap[RulesActivity.SpecialRuleCase.TRIPLE_DOUBLE] = mutableListOf(RulesActivity.Consequence.RESET_SCORE_TO_ZERO, RulesActivity.Consequence.LOSE_TURN)
        rulesMap[RulesActivity.SpecialRuleCase.OFF_TABLE] = mutableListOf(RulesActivity.Consequence.RESET_TURN_SCORE, RulesActivity.Consequence.LOSE_TURN)
        Log.d("rules me1", rulesMap[RulesActivity.SpecialRuleCase.ROLL_7]!!.toString())

        // Initialize shared preferences
        sharedPreferences = applicationContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

        // Load data from shared preferences
        //Log.d("players me1", players.toString())
        loadData()
        Log.d("rules me2", rulesMap[RulesActivity.SpecialRuleCase.ROLL_7]!!.toString())
        Log.d("players me1", players.toString())
    }

    private fun loadData() {
        // Load players
        //val playersSet = sharedPreferences.getStringSet("players", null)


        //players = playersSet?.toMutableList() as ArrayList<String>? ?: ArrayList()

        // Load other data...
        players = sharedPreferences.getStringSet("players", setOf())?.toList()?.let { ArrayList(it) } ?: ArrayList()
        //sittingOut = sharedPreferences.getString("sittingOut", "{}").let { Gson().fromJson(it, object : TypeToken<MutableMap<String, Boolean>>() {}.type) }
        val sittingOutSize = sharedPreferences.getInt("sittingOut_size", 0)
        sittingOut.clear()
        for (i in 0 until sittingOutSize) {
            val key = sharedPreferences.getString("sittingOut_${i}_key", null)
            val value = sharedPreferences.getBoolean("sittingOut_${i}_value", false)
            if (key != null) {
                sittingOut[key] = value
            }
        }
        val nameToScoreSize = sharedPreferences.getInt("nameToScore_size", 0)
        nameToScore.clear()
        for (i in 0 until nameToScoreSize) {
            val key = sharedPreferences.getString("nameToScore_${i}_key", null)
            val value = sharedPreferences.getInt("nameToScore_${i}_value", 0)
            if (key != null) {
                nameToScore[key] = value
            }
        }
        val nameToPotSize = sharedPreferences.getInt("nameToPot_size", 0)
        nameToPot.clear()
        for (i in 0 until nameToPotSize) {
            val key = sharedPreferences.getString("nameToPot_${i}_key", null)
            val value = sharedPreferences.getInt("nameToPot_${i}_value", 0)
            if (key != null) {
                nameToPot[key] = value
            }
        }
        endedGameSession = sharedPreferences.getBoolean("endedGameSession", false)
        endedGameRound = sharedPreferences.getBoolean("endedGameRound", false)
        rolledOnce = sharedPreferences.getBoolean("rolledOnce", false)
        wasFirstRoll = sharedPreferences.getBoolean("wasFirstRoll", false)
        currentPlayer = sharedPreferences.getString("currentPlayer", "") ?: ""
        currentPlayerLastTurnScore = sharedPreferences.getInt("currentPlayerLastTurnScore", 0)
        currentPlayerLastRollStartScore = sharedPreferences.getInt("currentPlayerLastRollStartScore", 0)
        currentPlayerRollStartScore = sharedPreferences.getInt("currentPlayerRollStartScore", 0)
        currentPlayerNewScore = sharedPreferences.getInt("currentPlayerNewScore", 0)
        previousPlayer = sharedPreferences.getString("previousPlayer", "") ?: ""
        previousPlayerLastScore = sharedPreferences.getInt("previousPlayerLastScore", 0)
        endingPlayer = sharedPreferences.getString("endingPlayer", "") ?: ""
        //NEW
        mustNextRoll = sharedPreferences.getBoolean("mustNextRoll", false)
        wasMustNextRoll = sharedPreferences.getBoolean("wasMustNextRoll", false)
        lastRollWasForcedNextRoll = sharedPreferences.getBoolean("lastRollWasForcedNextRoll", false)
        mustNextPlayer = sharedPreferences.getBoolean("mustNextPlayer", false)
        wasMustNextPlayer = sharedPreferences.getBoolean("wasMustNextPlayer", false)
        disableDiceSelect = sharedPreferences.getBoolean("disableDiceSelect", false)
        lastRollWasDouble = sharedPreferences.getBoolean("lastRollWasDouble", false)
        consecutiveDoubleRolls = sharedPreferences.getInt("consecutiveDoubleRolls", 0)
        previousPlayerLastRollWasDouble = sharedPreferences.getBoolean("previousPlayerLastRollWasDouble", false)
        previousPlayerConsecutiveDoubleRolls = sharedPreferences.getInt("previousPlayerConsecutiveDoubleRolls", 0)
        lastLastRollWasDouble = sharedPreferences.getBoolean("lastLastRollWasDouble", false)
        textConsequenceBuilder = StringBuilder(sharedPreferences.getString("textConsequenceBuilder", ""))
        previousTextConsequence = sharedPreferences.getString("previousTextConsequence", "") ?: ""
        previousPreviousTextConsequence = sharedPreferences.getString("previousPreviousTextConsequence", "") ?: ""

        //get the rules Map back from the JSON String if it is not empty (we had saved a rules Map before)
        val jsonString = sharedPreferences.getString("rulesMap", "")
        if (jsonString != null && jsonString.isNotEmpty()) {
            val type = object : TypeToken<MutableMap<RulesActivity.SpecialRuleCase, MutableList<RulesActivity.Consequence>>>() {}.type
            var previousRulesMap = rulesMap
            rulesMap = Gson().fromJson(jsonString, type)
            if(rulesMap.isEmpty()){
                rulesMap = previousRulesMap
            }
        }
        Log.d("loading rules map", jsonString.toString())
        //get the currentRuleCase and consequence back similar way
        val jsonString2 = sharedPreferences.getString("currentSpecialRuleCase", "")
        if (jsonString2 != null && jsonString2.isNotEmpty()) {
            currentSpecialRuleCase = Gson().fromJson(jsonString2, RulesActivity.SpecialRuleCase::class.java)
        }
        val jsonString3 = sharedPreferences.getString("currentSpecialRuleConsequences", "")
        if (jsonString3 != null && jsonString3.isNotEmpty()) {
            currentSpecialRuleConsequences = Gson().fromJson(jsonString3, object : TypeToken<MutableList<RulesActivity.Consequence>>() {}.type)
        }
    }

    fun saveData() {
        // Save other data...
        val editor = sharedPreferences.edit()
        editor.putStringSet("players", players.toSet())
        //editor.putBoolean("sittingOut", true) // Update with your actual logic for sittingOut
        editor.putInt("sittingOut_size", sittingOut.size)
        var i = 0
        for ((key, value) in sittingOut) {
            editor.putString("sittingOut_${i}_key", key)
            editor.putBoolean("sittingOut_${i}_value", value)
            i++
        }
        editor.putInt("nameToScore_size", nameToScore.size)
        i = 0
        //for ((key, value) in nameToScore) {
        //    editor.putInt("nameToScore_$key", value)
        //}
        for ((key, value) in nameToScore) {
            editor.putString("nameToScore_${i}_key", key)
            editor.putInt("nameToScore_${i}_value", value)
            i++
        }
        editor.putInt("nameToPot_size", nameToPot.size)
        i = 0
        for ((key, value) in nameToPot) {
            editor.putString("nameToPot_${i}_key", key)
            editor.putInt("nameToPot_${i}_value", value)
            i++
        }
        editor.putBoolean("endedGameSession", endedGameSession)
        editor.putBoolean("endedGameRound", endedGameRound)
        editor.putBoolean("rolledOnce", rolledOnce)
        editor.putBoolean("wasFirstRoll", wasFirstRoll)
        editor.putString("currentPlayer", currentPlayer)
        editor.putInt("currentPlayerLastTurnScore", currentPlayerLastTurnScore)
        editor.putInt("currentPlayerLastRollStartScore", currentPlayerLastRollStartScore)
        editor.putInt("currentPlayerRollStartScore", currentPlayerRollStartScore)
        editor.putInt("currentPlayerNewScore", currentPlayerNewScore)
        editor.putString("previousPlayer", previousPlayer)
        editor.putInt("previousPlayerLastScore", previousPlayerLastScore)
        editor.putString("endingPlayer", endingPlayer)
        //NEW
        editor.putBoolean("mustNextRoll", mustNextRoll)
        editor.putBoolean("wasMustNextRoll", wasMustNextRoll)
        editor.putBoolean("lastRollWasForcedNextRoll", lastRollWasForcedNextRoll)
        editor.putBoolean("mustNextPlayer", mustNextPlayer)
        editor.putBoolean("wasMustNextPlayer", wasMustNextPlayer)
        editor.putBoolean("disableDiceSelect", disableDiceSelect)
        editor.putBoolean("lastRollWasDouble", lastRollWasDouble)
        editor.putInt("consecutiveDoubleRolls", consecutiveDoubleRolls)
        editor.putBoolean("previousPlayerLastRollWasDouble", previousPlayerLastRollWasDouble)
        editor.putInt("previousPlayerConsecutiveDoubleRolls", previousPlayerConsecutiveDoubleRolls)
        editor.putBoolean("lastLastRollWasDouble", lastLastRollWasDouble)
        editor.putString("textConsequenceBuilder", textConsequenceBuilder.toString())
        editor.putString("previousTextConsequence", previousTextConsequence)
        editor.putString("previousPreviousTextConsequence", previousPreviousTextConsequence)


        //Use GSON to save rulesMap as a string to Shared Preferences
        if(rulesMap.isNotEmpty()){
            val jsonString = Gson().toJson(rulesMap)
            editor.putString("rulesMap", jsonString)
        }

        //Use GSON to save currentRuleCase and Consequence as a string to Shared Preferences
        val jsonString2 = Gson().toJson(currentSpecialRuleCase)
        editor.putString("currentSpecialRuleCase", jsonString2)
        val jsonString3 = Gson().toJson(currentSpecialRuleConsequences)
        editor.putString("currentSpecialRuleConsequences", jsonString3)

        editor.apply()
    }
}