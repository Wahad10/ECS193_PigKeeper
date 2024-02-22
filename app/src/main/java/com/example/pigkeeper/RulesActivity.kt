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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        var globalVariable = GlobalData.instance
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

        //List of SpecialRuleCase enums. This list is data that passes through application
        //REPLACE WITH MAP. SRCs WILL BE THE KEY, LIST OF CONSEQEUNCES WILL BE THE VALUES
        val SpecialRuleCaseList: List<SpecialRuleCase> = listOf(
            SpecialRuleCase.ROLL_7,
            SpecialRuleCase.SNAKE_EYES,
            SpecialRuleCase.BOX_CARS,
            SpecialRuleCase.DOUBLE,
            SpecialRuleCase.TRIPLE_DOUBLE,
            SpecialRuleCase.OFF_TABLE
        )

        fun toggleButton(button: Button) {
            if (!button.isSelected) {
                button.isSelected = true
                button.backgroundTintList = ColorStateList.valueOf(Color.rgb(0,204,0))
            } else {
                button.isSelected = false
                button.backgroundTintList = ColorStateList.valueOf(Color.rgb(103,80,164))
            }
        }

        data class PairOfInts(val first: Int, val second: Int)

        var resetInt = 0

        fun undoLastClick(int: Int) {
            when (int) {
                1 -> {toggleButton(buttonRoll7)
                    toggleButton(buttonLoseTurn)}
                2 -> {toggleButton(buttonSnakeEyes)
                    toggleButton(buttonResetScoreToO)}
                3 -> {toggleButton(buttonBoxCars)
                    toggleButton(buttonNoEffect)}
                4 -> {toggleButton(buttonDouble)
                    toggleButton(button2XPoints)
                    toggleButton(buttonRollAgain)}
                5 -> {toggleButton(button3xDoubles)
                    toggleButton(buttonResetScoreToO)}
                6 -> {toggleButton(buttonOffTable)
                    toggleButton(buttonLoseTurn)}
            }
        }

        //SCAFFOLDING TOP------------------------

        //create Map, where SPCs are keys and list of consequences are value
        val rulesMap: MutableMap<SpecialRuleCase, MutableList<Consequence>> = mutableMapOf()
        rulesMap[SpecialRuleCase.ROLL_7] = mutableListOf(Consequence.LOSE_TURN)
        rulesMap[SpecialRuleCase.SNAKE_EYES] = mutableListOf(Consequence.RESET_SCORE_TO_ZERO)
        rulesMap[SpecialRuleCase.BOX_CARS] = mutableListOf(Consequence.NO_EFFECT)
        rulesMap[SpecialRuleCase.DOUBLE] = mutableListOf(Consequence.DOUBLE_POINTS, Consequence.MUST_ROLL_AGAIN)
        rulesMap[SpecialRuleCase.TRIPLE_DOUBLE] = mutableListOf(Consequence.RESET_SCORE_TO_ZERO)
        rulesMap[SpecialRuleCase.OFF_TABLE] = mutableListOf(Consequence.LOSE_TURN)

        var currentlySelectedSpecialRuleCaseCode = 0
        var IsAnySpecialRuleCaseCurrentlySelected = false


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

        //var currentSpecialRoleCase = getMapKey(currentlySelectedSpecialRuleCaseCode)
        var currentSpecialRoleCase = SpecialRuleCase.ROLL_7 //initialized
//        println("currentSpecialRoleCase.listOfConsequence:")
//        println(currentSpecialRoleCase.listOfConsequence)
//        println("rulesMap[SpecialRuleCase.DOUBLE]:")
//        println(rulesMap[SpecialRuleCase.DOUBLE])
//        rulesMap[SpecialRuleCase.DOUBLE]?.add(Consequence.RESET_TURN_SCORE)
//        println(rulesMap[SpecialRuleCase.DOUBLE])

        fun printAllTable() {
            println("ROLL_7: " + rulesMap[SpecialRuleCase.ROLL_7])
            println("SNAKE_EYES: " + rulesMap[SpecialRuleCase.SNAKE_EYES])
            println("BOX_CARS: " + rulesMap[SpecialRuleCase.BOX_CARS])
            println("DOUBLE: " + rulesMap[SpecialRuleCase.DOUBLE])
            println("TRIPLE_DOUBLE: " + rulesMap[SpecialRuleCase.TRIPLE_DOUBLE])
            println("OFF_TABLE: " + rulesMap[SpecialRuleCase.OFF_TABLE])
            println("-----------------------")
        }

        printAllTable()

        var _2xPointsIsActive = false
        var LoseTurnIsActive = false
        var MustRollAgainIsActive = false
        var ResetTurnScoreIsActive = false
        var ResetScoreTo0IsActive = false
        var NoEffectIsActive = false

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
        }

        buttonResetTurnScore.setOnClickListener {
            ResetTurnScoreIsActive = !ResetScoreTo0IsActive

            if (ResetTurnScoreIsActive == false) {
                if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.RESET_TURN_SCORE) == true) {
                    rulesMap[currentSpecialRoleCase]?.remove(Consequence.RESET_TURN_SCORE)
                }
            }
            else if (rulesMap[currentSpecialRoleCase]?.contains(Consequence.RESET_TURN_SCORE) == false) {
                rulesMap[currentSpecialRoleCase]?.add(Consequence.RESET_TURN_SCORE)
            }
            printAllTable() //for testing output
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
        }

        //SCAFFOLDING BOTTOM-------------------------

        buttonRoll7.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 1
            //currentlySelectedSpecialRuleCaseCode = 1
            currentSpecialRoleCase = SpecialRuleCase.ROLL_7
            toggleButton(buttonRoll7)
            toggleButton(buttonLoseTurn)

        }
        buttonSnakeEyes.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 2
            //currentlySelectedSpecialRuleCaseCode = 2
            currentSpecialRoleCase = SpecialRuleCase.SNAKE_EYES
            toggleButton(buttonSnakeEyes)
            toggleButton(buttonResetScoreToO)
        }
        buttonBoxCars.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 3
            //currentlySelectedSpecialRuleCaseCode = 3
            currentSpecialRoleCase = SpecialRuleCase.BOX_CARS
            toggleButton(buttonBoxCars)
            toggleButton(buttonNoEffect)
        }
        buttonDouble.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 4
            //currentlySelectedSpecialRuleCaseCode = 4
            currentSpecialRoleCase = SpecialRuleCase.DOUBLE
            toggleButton(buttonDouble)
            toggleButton(button2XPoints)
            toggleButton(buttonRollAgain)
        }
        button3xDoubles.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 5
            //currentlySelectedSpecialRuleCaseCode = 5
            currentSpecialRoleCase = SpecialRuleCase.TRIPLE_DOUBLE
            toggleButton(button3xDoubles)
            toggleButton(buttonResetScoreToO)
        }
        buttonOffTable.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 6
            //currentlySelectedSpecialRuleCaseCode = 6
            currentSpecialRoleCase = SpecialRuleCase.OFF_TABLE
            toggleButton(buttonOffTable)
            toggleButton(buttonLoseTurn)
        }

    }

    fun onSpecialTurnCaseClick(view: View) {
        specialTurnCaseSelected = !specialTurnCaseSelected
    }
}
