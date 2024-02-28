package com.example.pigkeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.session.PlaybackState.CustomAction
import android.view.View
import android.widget.ToggleButton
//import java.lang.Error
import kotlin.Error

class RulesActivity : AppCompatActivity() {


    var specialTurnCaseSelected = false; //if any special turn case button is selected, return True

    enum class Consequence {
        DOUBLE_POINTS,
        LOSE_TURN,
        MUST_ROLL_AGAIN,
        RESET_TURN_SCORE,
        RESET_SCORE_TO_ZERO,
        NO_EFFECT
    }

    enum class SpecialRuleCase(val listOfConsequence:List<Consequence>) {
        ROLL_7(listOf(Consequence.LOSE_TURN)),
        SNAKE_EYES(listOf(Consequence.RESET_SCORE_TO_ZERO)),
        BOX_CARS(listOf(Consequence.NO_EFFECT)),
        DOUBLE(listOf(Consequence.DOUBLE_POINTS, Consequence.MUST_ROLL_AGAIN)),
        TRIPLE_DOUBLE(listOf(Consequence.RESET_SCORE_TO_ZERO)),
        OFF_TABLE(listOf(Consequence.LOSE_TURN))
    }


    lateinit var globalVariable : GlobalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)


        globalVariable = GlobalData.instance

        //Save rules to globalData

        //Back button, returns to Main Menu Screen
        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener{startActivity(Intent(this@RulesActivity, MainActivity::class.java))}

        //Special Turn Case Buttons
        val buttonRoll7 = findViewById<Button>(R.id.buttonRoll7)
        val buttonSnakeEyes = findViewById<Button>(R.id.buttonSnakeEyes)
        val buttonBoxCars = findViewById<Button>(R.id.buttonBoxCars)
        val buttonDouble = findViewById<Button>(R.id.buttonDouble)
        val button3xDoubles = findViewById<Button>(R.id.button3xDoubles)
        val buttonOffTable = findViewById<Button>(R.id.buttonOffTable)

        //Consequence Buttons
        val button2XPoints = findViewById<Button>(R.id.buttonDoublePoints)
        val buttonLoseTurn = findViewById<Button>(R.id.buttonLoseTurn)
        val buttonRollAgain = findViewById<Button>(R.id.buttonMustRollAgain)
        val buttonResetTurnScore = findViewById<Button>(R.id.buttonResetTurnScore)
        val buttonResetScoreToO = findViewById<Button>(R.id.buttonResetScoreTo0)
        val buttonNoEffect = findViewById<Button>(R.id.buttonNoEffect)


        var _2xPointsIsActive = false
        var LoseTurnIsActive = false
        var MustRollAgainIsActive = false
        var ResetTurnScoreIsActive = false
        var ResetScoreTo0IsActive = false
        var NoEffectIsActive = false

        var Roll7IsActive = false
        var SnakeEyesIsActive = false
        var BoxCarsIsActive = false
        var DoubleIsActive = false
        var _3xDoubleIsActive = false
        var OffTableIsActive = false


        fun toggleButtonColor(button: Button, boolean: Boolean){
            if (boolean == true) {
                button.backgroundTintList = ColorStateList.valueOf(Color.rgb(0,204,0))
            } else {
                button.backgroundTintList = ColorStateList.valueOf(Color.rgb(103,80,164))
            }
        }

        fun toggleAllButtonColors() {
            toggleButtonColor(buttonRoll7, Roll7IsActive)
            toggleButtonColor(buttonSnakeEyes, SnakeEyesIsActive)
            toggleButtonColor(buttonBoxCars, BoxCarsIsActive)
            toggleButtonColor(buttonDouble, DoubleIsActive)
            toggleButtonColor(button3xDoubles, _3xDoubleIsActive)
            toggleButtonColor(buttonOffTable, OffTableIsActive)

            toggleButtonColor(button2XPoints, _2xPointsIsActive)
            toggleButtonColor(buttonLoseTurn, LoseTurnIsActive)
            toggleButtonColor(buttonRollAgain, MustRollAgainIsActive)
            toggleButtonColor(buttonResetTurnScore, ResetTurnScoreIsActive)
            toggleButtonColor(buttonResetScoreToO, ResetScoreTo0IsActive)
            toggleButtonColor(buttonNoEffect, NoEffectIsActive)
        }



        //create Map, where SPCs are keys and list of consequences are value
        val rulesMap: MutableMap<SpecialRuleCase, MutableList<Consequence>> = mutableMapOf()
        rulesMap[SpecialRuleCase.ROLL_7] = mutableListOf(Consequence.LOSE_TURN)
        rulesMap[SpecialRuleCase.SNAKE_EYES] = mutableListOf(Consequence.RESET_SCORE_TO_ZERO)
        rulesMap[SpecialRuleCase.BOX_CARS] = mutableListOf(Consequence.NO_EFFECT)
        rulesMap[SpecialRuleCase.DOUBLE] = mutableListOf(Consequence.DOUBLE_POINTS, Consequence.MUST_ROLL_AGAIN)
        rulesMap[SpecialRuleCase.TRIPLE_DOUBLE] = mutableListOf(Consequence.RESET_SCORE_TO_ZERO)
        rulesMap[SpecialRuleCase.OFF_TABLE] = mutableListOf(Consequence.LOSE_TURN)

        fun getMapKey(code: Int): SpecialRuleCase {
            return when (code) {
                1 -> SpecialRuleCase.ROLL_7
                2 -> SpecialRuleCase.SNAKE_EYES
                3 -> SpecialRuleCase.BOX_CARS
                4 -> SpecialRuleCase.DOUBLE
                5 -> SpecialRuleCase.TRIPLE_DOUBLE
                else -> SpecialRuleCase.OFF_TABLE
            }
        }

        var currentSpecialRoleCase = SpecialRuleCase.ROLL_7 //initialized

        fun printAllTable() { //for debugging
            println("ROLL_7: " + rulesMap[SpecialRuleCase.ROLL_7])
            println("SNAKE_EYES: " + rulesMap[SpecialRuleCase.SNAKE_EYES])
            println("BOX_CARS: " + rulesMap[SpecialRuleCase.BOX_CARS])
            println("DOUBLE: " + rulesMap[SpecialRuleCase.DOUBLE])
            println("TRIPLE_DOUBLE: " + rulesMap[SpecialRuleCase.TRIPLE_DOUBLE])
            println("OFF_TABLE: " + rulesMap[SpecialRuleCase.OFF_TABLE])
            println("-----------------------")
        }

        fun deactivateAllSpecialRuleCases() {
            Roll7IsActive = false
            SnakeEyesIsActive = false
            BoxCarsIsActive = false
            DoubleIsActive = false
            _3xDoubleIsActive = false
            OffTableIsActive = false
        }

        fun deactivateAllConsequences() {
            _2xPointsIsActive = false
            LoseTurnIsActive = false
            MustRollAgainIsActive = false
            ResetTurnScoreIsActive = false
            ResetScoreTo0IsActive = false
            NoEffectIsActive = false
            //println("Status of LoseTurnIsActive after deactivatingAllConsequences: $LoseTurnIsActive")
        }

        fun toggleConsequenceIsActive(consequence: Consequence) {
            when (consequence) {
                Consequence.DOUBLE_POINTS -> {
                    _2xPointsIsActive = true
                }
                Consequence.LOSE_TURN -> {
                    LoseTurnIsActive = true
                }
                Consequence.MUST_ROLL_AGAIN -> {
                    MustRollAgainIsActive = true
                }
                Consequence.RESET_TURN_SCORE -> {
                    ResetTurnScoreIsActive = true
                }
                Consequence.RESET_SCORE_TO_ZERO -> {
                    ResetScoreTo0IsActive = true
                }
                Consequence.NO_EFFECT -> {
                    NoEffectIsActive = true
                }
            }
        }

        button2XPoints.setOnClickListener {
            _2xPointsIsActive = !_2xPointsIsActive //toggle isActive bool

            if (_2xPointsIsActive == false) {
                if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.DOUBLE_POINTS) == true) {
                    rulesMap[currentSpecialRoleCase]?.remove(Consequence.DOUBLE_POINTS)
                }
            }
            else if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.DOUBLE_POINTS) == false) {
                rulesMap[currentSpecialRoleCase]?.add(Consequence.DOUBLE_POINTS)
            }

            printAllTable() //for testing output
            toggleAllButtonColors()
        }

        buttonLoseTurn.setOnClickListener {
            LoseTurnIsActive = !LoseTurnIsActive

            if (LoseTurnIsActive == false) {
                if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.LOSE_TURN) == true) {

                    rulesMap[currentSpecialRoleCase]?.remove(Consequence.LOSE_TURN)
                }
            }
            else if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.LOSE_TURN) == false) {
                rulesMap[currentSpecialRoleCase]?.add(Consequence.LOSE_TURN)
            }
            printAllTable() //for testing output
            toggleAllButtonColors()
        }

        buttonRollAgain.setOnClickListener {
            MustRollAgainIsActive = !MustRollAgainIsActive

            if (MustRollAgainIsActive == false) {
                if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.MUST_ROLL_AGAIN) == true) {
                    rulesMap[currentSpecialRoleCase]?.remove(Consequence.MUST_ROLL_AGAIN)
                }
            }
            else if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.MUST_ROLL_AGAIN) == false) {
                rulesMap[currentSpecialRoleCase]?.add(Consequence.MUST_ROLL_AGAIN)
            }
            printAllTable() //for testing output
            toggleAllButtonColors()
        }

        buttonResetTurnScore.setOnClickListener {
            ResetTurnScoreIsActive = !ResetTurnScoreIsActive

            if (ResetTurnScoreIsActive == false) {
                if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.RESET_TURN_SCORE) == true) {
                    rulesMap[currentSpecialRoleCase]?.remove(Consequence.RESET_TURN_SCORE)
                }
            }
            else if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.RESET_TURN_SCORE) == false) {
                rulesMap[currentSpecialRoleCase]?.add(Consequence.RESET_TURN_SCORE)
            }
            printAllTable() //for testing output
            toggleAllButtonColors()
        }

        buttonResetScoreToO.setOnClickListener {
            ResetScoreTo0IsActive = !ResetScoreTo0IsActive

            if (ResetScoreTo0IsActive == false) {
                if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.RESET_SCORE_TO_ZERO) == true) {
                    rulesMap[currentSpecialRoleCase]?.remove(Consequence.RESET_SCORE_TO_ZERO)
                }
            }
            else if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.RESET_SCORE_TO_ZERO) == false) {
                rulesMap[currentSpecialRoleCase]?.add(Consequence.RESET_SCORE_TO_ZERO)
            }
            printAllTable() //for testing output
            toggleAllButtonColors()
        }

        buttonNoEffect.setOnClickListener {
            NoEffectIsActive = !NoEffectIsActive

            if (NoEffectIsActive == false) {
                if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.NO_EFFECT) == true) {
                    rulesMap[currentSpecialRoleCase]?.remove(Consequence.NO_EFFECT)
                }
            }
            else if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.NO_EFFECT) == false) {
                rulesMap[currentSpecialRoleCase]?.add(Consequence.NO_EFFECT)
            }
            printAllTable() //for testing output
            toggleAllButtonColors()
        }


        buttonRoll7.setOnClickListener {
            deactivateAllSpecialRuleCases() //toggles off all SRCs
            Roll7IsActive = true

            deactivateAllConsequences() //toggles off all consequences
            for (consequence in rulesMap[SpecialRuleCase.ROLL_7]!!) { //toggles on all consequences associated with ROLL_7 as active and changes color
                toggleConsequenceIsActive(consequence)
            }

            toggleButtonColor(buttonRoll7, Roll7IsActive) //toggles color of Roll 7 button
            currentSpecialRoleCase = SpecialRuleCase.ROLL_7
            toggleAllButtonColors()
        }

        buttonSnakeEyes.setOnClickListener {
            deactivateAllSpecialRuleCases()
            SnakeEyesIsActive = true

            deactivateAllConsequences() //toggles off all consequences
            for (consequence in rulesMap[SpecialRuleCase.SNAKE_EYES]!!) { //toggles on all consequences associated with SNAKE_EYES as active and changes color
                toggleConsequenceIsActive(consequence)
            }

            toggleButtonColor(buttonSnakeEyes, SnakeEyesIsActive) //toggles color of Roll 7 button
            currentSpecialRoleCase = SpecialRuleCase.SNAKE_EYES
            toggleAllButtonColors()
        }

        buttonBoxCars.setOnClickListener {
            deactivateAllSpecialRuleCases()
            BoxCarsIsActive = true

            deactivateAllConsequences() //toggles off all consequences
            for (consequence in rulesMap[SpecialRuleCase.BOX_CARS]!!) {
                toggleConsequenceIsActive(consequence)
            }

            toggleButtonColor(buttonBoxCars, BoxCarsIsActive)
            currentSpecialRoleCase = SpecialRuleCase.BOX_CARS
            toggleAllButtonColors()
        }

        buttonDouble.setOnClickListener {
            deactivateAllSpecialRuleCases()
            DoubleIsActive = true

            deactivateAllConsequences() //toggles off all consequences
            for (consequence in rulesMap[SpecialRuleCase.DOUBLE]!!) {
                toggleConsequenceIsActive(consequence)
            }

            toggleButtonColor(buttonDouble, DoubleIsActive)
            currentSpecialRoleCase = SpecialRuleCase.DOUBLE
            toggleAllButtonColors()
        }

        button3xDoubles.setOnClickListener {
            deactivateAllSpecialRuleCases()
            _3xDoubleIsActive = true

            deactivateAllConsequences() //toggles off all consequences
            for (consequence in rulesMap[SpecialRuleCase.TRIPLE_DOUBLE]!!) { //toggles on all consequences associated with ROLL_7 as active and changes color
                toggleConsequenceIsActive(consequence)
            }

            toggleButtonColor(button3xDoubles, _3xDoubleIsActive) //toggles color of Roll 7 button
            currentSpecialRoleCase = SpecialRuleCase.TRIPLE_DOUBLE
            toggleAllButtonColors()
        }

        buttonOffTable.setOnClickListener {
            deactivateAllSpecialRuleCases()
            OffTableIsActive = true

            deactivateAllConsequences() //toggles off all consequences
            for (consequence in rulesMap[SpecialRuleCase.OFF_TABLE]!!) { //toggles on all consequences associated with ROLL_7 as active and changes color
                toggleConsequenceIsActive(consequence)
            }

            toggleButtonColor(buttonOffTable, OffTableIsActive) //toggles color of Roll 7 button
            currentSpecialRoleCase = SpecialRuleCase.OFF_TABLE
            toggleAllButtonColors()
        }

    }


    fun onSpecialTurnCaseClick(view: View) {
        specialTurnCaseSelected = !specialTurnCaseSelected
    }



    override fun onPause() {
        super.onPause()
        globalVariable.saveData()
    }

}