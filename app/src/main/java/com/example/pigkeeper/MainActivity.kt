package com.example.pigkeeper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    lateinit var globalVariable : GlobalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        globalVariable = GlobalData.instance

        val buttonPlay = findViewById<Button>(R.id.buttonPlay)
        buttonPlay.setOnClickListener{newGame()}

        val buttonResume = findViewById<Button>(R.id.buttonResume)
        buttonResume.setOnClickListener{resumeGame()}

        val buttonRules = findViewById<Button>(R.id.buttonRules)
        buttonRules.setOnClickListener{startActivity(Intent(this@MainActivity, RulesActivity::class.java))}

        val buttonLastPot = findViewById<Button>(R.id.buttonLastPot)
        buttonLastPot.setOnClickListener{startActivity(Intent(this@MainActivity, LastPotActivity::class.java))}

        val SettingsButton = findViewById<ImageButton>(R.id.SettingsButton)
        SettingsButton.setOnClickListener{startActivity(Intent(this@MainActivity, SettingsActivity::class.java))}
    }

    private fun newGame(){
        globalVariable.nameToScore.clear()
        globalVariable.endedGameSession = true
        globalVariable.endedGameRound = true
        startActivity(Intent(this@MainActivity, NewPlayersActivity::class.java))
    }

    private fun resumeGame(){
        if(!globalVariable.endedGameSession){
            if(globalVariable.endedGameRound){
                startActivity(Intent(this@MainActivity, NewPlayersActivity::class.java))
            }else{
                startActivity(Intent(this@MainActivity, RollActivity::class.java))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        globalVariable.saveData()
    }
}