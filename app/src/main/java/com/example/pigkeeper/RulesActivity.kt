package com.example.pigkeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RulesActivity : AppCompatActivity() {
    lateinit var globalVariable : GlobalData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)


        globalVariable = GlobalData.instance

        //Save rules to globalData
    }


    override fun onPause() {
        super.onPause()
        globalVariable.saveData()
    }
}