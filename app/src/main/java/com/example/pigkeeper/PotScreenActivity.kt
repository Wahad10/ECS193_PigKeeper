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

        //var playerNames = ArrayList<String>();

        var playerScores = globalVariable.nameToScore

        /**var playerScores = globalVariable.score

        //If no score then just leave some default
        if (playerScores.size == 0) {
            playerNames.addAll(
                arrayOf(
                    "Player 1",
                    "Player 2",
                    "Player 3",
                    "Player 4",
                    "Player 5",
                    "Player 6",
                    "Player 7",
                    "Player 8",
                    "Player 9",
                    "Player 10"
                )
            )
            playerScores = ArrayList<Int>(0)
            repeat(playerNames.size) { playerScores.add(0) }
        } else {
            playerNames = globalVariable.players
        }

        val potScores = mutableMapOf<String, Int>()
        for (i in playerNames.indices) {
            potScores[playerNames[i]] = playerScores[i]
        }**/

        val textViewPotScores = findViewById<TextView>(R.id.textViewPotScores)

        val formattedScores = StringBuilder()
        for ((player, score) in playerScores) {
            formattedScores.append("$player: $score\n")
        }

        textViewPotScores.text = formattedScores.toString()

        val buttonEndGame = findViewById<Button>(R.id.buttonEndGame)
        buttonEndGame.setOnClickListener {
            startActivity(Intent(this@PotScreenActivity, MainActivity::class.java))
        }

        val buttonNext = findViewById<Button>(R.id.buttonNext)
        buttonNext.setOnClickListener {
            startActivity(Intent(this@PotScreenActivity, NewPlayersActivity::class.java))
        }

    }
}