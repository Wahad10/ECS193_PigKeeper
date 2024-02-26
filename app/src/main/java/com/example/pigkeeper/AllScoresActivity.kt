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
        var playerScores = globalVariable.nameToScore

        val textViewAllScores = findViewById<TextView>(R.id.textViewAllScores)
        val formattedScores = StringBuilder()
        for ((player, score) in playerScores) {
            formattedScores.append("$player: $score\n")
        }
        textViewAllScores.text = formattedScores.toString()

        val buttonBack = findViewById<Button>(R.id.buttonAllScoresBack)
        buttonBack.setOnClickListener{startActivity(Intent(this@AllScoresActivity, RollActivity::class.java))}
    }

}