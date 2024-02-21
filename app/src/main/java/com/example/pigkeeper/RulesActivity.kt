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

        buttonRoll7.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 1
            toggleButton(buttonRoll7)
            toggleButton(buttonLoseTurn)

        }
        buttonSnakeEyes.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 2
            toggleButton(buttonSnakeEyes)
            toggleButton(buttonResetScoreToO)
        }
        buttonBoxCars.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 3
            toggleButton(buttonBoxCars)
            toggleButton(buttonNoEffect)
        }
        buttonDouble.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 4
            toggleButton(buttonDouble)
            toggleButton(button2XPoints)
            toggleButton(buttonRollAgain)
        }
        button3xDoubles.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 5
            toggleButton(button3xDoubles)
            toggleButton(buttonResetScoreToO)
        }
        buttonOffTable.setOnClickListener {
            undoLastClick(resetInt)
            resetInt = 6
            toggleButton(buttonOffTable)
            toggleButton(buttonLoseTurn)
        }

    }

    fun onSpecialTurnCaseClick(view: View) {
        specialTurnCaseSelected = !specialTurnCaseSelected
    }

}