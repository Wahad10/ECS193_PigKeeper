package com.example.pigkeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import android.content.Intent

class RollActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll)

        val globalVariable = GlobalData.instance
        val players = globalVariable.players
        val sittingOut = globalVariable.sittingOut

        //Idk if you need this
        val score = globalVariable.score

        // ability to click on the cardTop to view AllScoresActivity
        val cardTop = findViewById<CardView>(R.id.cardTop)
        cardTop.setOnClickListener{startActivity(Intent(this@RollActivity, AllScoresActivity::class.java))}

    }
}