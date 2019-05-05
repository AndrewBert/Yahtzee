package com.example.yahtzee

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainMenuActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)



        val soloButton = findViewById<Button>(R.id.soloButton)

        soloButton.setOnClickListener {
            val intent = Intent(this,SoloActivity::class.java)
            startActivity(intent)
        }

        val versusButton = findViewById<Button>(R.id.versusButton)

        versusButton.setOnClickListener {
            val intent = Intent(this,VersusActivity::class.java)
            startActivity(intent)
        }

    }
}
