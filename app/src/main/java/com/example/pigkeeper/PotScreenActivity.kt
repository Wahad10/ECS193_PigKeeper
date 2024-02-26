package com.example.pigkeeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class PotScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pot_screen)

        val globalVariable = GlobalData.instance
        var playerScores = globalVariable.nameToScore
        var playerPots = globalVariable.nameToPot


        //SHOW POT
        val formattedScores = StringBuilder()
        for ((player, pot) in playerPots) {
            formattedScores.append("$player: $pot\n")
        }
        val textViewPotScores = findViewById<TextView>(R.id.textViewPotScores)
        textViewPotScores.text = formattedScores.toString()

        //SHOW ROUND SCORES
        val formattedScores2 = StringBuilder()
        for ((player, score) in playerScores) {
            formattedScores2.append("$player: $score\n")
        }
        val textViewRoundScores = findViewById<TextView>(R.id.textViewRoundScores)
        textViewRoundScores.text = formattedScores2.toString()


        val buttonEndGame = findViewById<Button>(R.id.buttonEndGame)
        buttonEndGame.setOnClickListener {
            globalVariable.nameToScore.clear()
            globalVariable.endedGameSession = true
            startActivity(Intent(this@PotScreenActivity, MainActivity::class.java))
        }

        val buttonNext = findViewById<Button>(R.id.buttonNext)
        buttonNext.setOnClickListener {
            globalVariable.nameToScore.clear()
            startActivity(Intent(this@PotScreenActivity, NewPlayersActivity::class.java))
        }
    }
}