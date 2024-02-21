package com.example.pigkeeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
class AllScoresActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_scores)

        val globalVariable = GlobalData.instance

        var playerNames = ArrayList<String>()

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

        val allScores = mutableMapOf<String, Int>()
        for (i in playerNames.indices) {
            allScores[playerNames[i]] = playerScores[i]
        }

        val textViewAllScores = findViewById<TextView>(R.id.textViewAllScores)

        val formattedScores = StringBuilder()
        for ((player, score) in allScores) {
            formattedScores.append("$player: $score\n")
        }

        textViewAllScores.text = formattedScores.toString()

        val buttonBack = findViewById<Button>(R.id.buttonAllScoresBack)
        buttonBack.setOnClickListener{startActivity(Intent(this@AllScoresActivity, RollActivity::class.java))}
    }

}