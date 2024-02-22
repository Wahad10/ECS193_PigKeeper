package com.example.pigkeeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class WinScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win_screen)

        val globalVariable = GlobalData.instance

        val textWinner = findViewById<TextView>(R.id.textWinner)
        //update top score
        val topScore = globalVariable.nameToScore.values.maxOrNull()!!
        val topPlayer = globalVariable.nameToScore.entries.firstOrNull { it.value == topScore }?.key
        textWinner.text = "$topPlayer Wins!\n\n$topScore"

        val buttonPot = findViewById<Button>(R.id.buttonPot)
        buttonPot.setOnClickListener{startActivity(Intent(this@WinScreenActivity, PotScreenActivity::class.java))}

    }
}