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

        val globalVariable = GlobalData.instance

        var playerNames = ArrayList<String>();

        var playerScores = globalVariable.score

        //If no score then just leave some default
        if (playerScores.size == 0){
            playerNames.addAll(arrayOf("Player 1", "Player 2", "Player 3", "Player 4", "Player 5", "Player 6", "Player 7", "Player 8", "Player 9", "Player 10"))
            playerScores = ArrayList<Int>(0)
            repeat(playerNames.size){playerScores.add(0)}
        }
        else{
            playerNames = globalVariable.players
        }


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