package com.example.pigkeeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LastPotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_pot)

        val playerNames = arrayOf("Player 1", "Player 2", "Player 3", "Player 4", "Player 5", "Player 6"
                                    , "Player 7", "Player 8", "Player 9", "Player 10")
        val playerScores = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        val lastPotScores = mutableMapOf<String, Int>()
        for (i in playerNames.indices) {
            lastPotScores[playerNames[i]] = playerScores[i]
        }

        val textViewLastPotScores = findViewById<TextView>(R.id.textViewLastPotScores)

        val formattedScores = StringBuilder()
        for ((player, score) in lastPotScores) {
            formattedScores.append("$player: $score\n")
        }

        textViewLastPotScores.text = formattedScores.toString()



        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener{startActivity(Intent(this@LastPotActivity, MainActivity::class.java))}
    }
}