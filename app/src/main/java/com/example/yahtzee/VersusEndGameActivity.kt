package com.example.yahtzee

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.TextView

class VersusEndGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_versus_end_game)


        val player1Score = intent.getIntExtra("player1Score", 999)
        val player2Score = intent.getIntExtra("player2Score", 999)


        var winningPlayerNumber = 0

        /**UI ELEMENTS**/
        val player1ScoreTV = findViewById<TextView>(R.id.player1FinalScoreTV)
        val player2ScoreTV = findViewById<TextView>(R.id.player2FinalScoreTV)
        val winningPlayerTV = findViewById<TextView>(R.id.winningPlayerTV)
        val player1TV = findViewById<TextView>(R.id.player1TV)
        val player2TV = findViewById<TextView>(R.id.player2TV)



        player1ScoreTV.text = player1Score.toString()
        player2ScoreTV.text = player2Score.toString()

        winningPlayerNumber = when{
            player1Score > player2Score -> 1
            player1Score < player2Score -> 2
            player1Score == player2Score -> 3
            else -> 4
        }

        when(winningPlayerNumber){
            1,2 -> winningPlayerTV.text = "Player $winningPlayerNumber wins!"
            3   -> winningPlayerTV.text = "It's a tie!"
            4   -> winningPlayerTV.text = "Something weird happened"
            0   -> winningPlayerTV.text = "Player score error"
        }

        if(winningPlayerNumber==1){
            player1TV.setTextColor(ContextCompat.getColor(this,R.color.colorOrange))
            player1ScoreTV.setTextColor(ContextCompat.getColor(this,R.color.colorOrange))
        }else if(winningPlayerNumber==2){
            player2TV.setTextColor(ContextCompat.getColor(this,R.color.colorOrange))
            player2ScoreTV.setTextColor(ContextCompat.getColor(this,R.color.colorOrange))
        }

        val newGameButton = findViewById<Button>(R.id.versusButton)

        newGameButton.setOnClickListener {
            val intent = Intent(this, VersusActivity::class.java)
            startActivity(intent)
        }

        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)

        mainMenuButton.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }



        /**UI ELEMENTS END**/









    }
}
