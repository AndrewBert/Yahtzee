package com.example.yahtzee

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class EndGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_game)


        //val player1Score: String = player1ScoreTextView?.text.toString()
        //val player2Score: String = player2ScoreTextView?.text.toString()

        val player1Score = intent.getIntExtra("player1Score", 999)
        val player2Score = intent.getIntExtra("player2Score", 999)


        var winningPlayerNumber = 0

        /**UI ELEMENTS**/
        val player1ScoreTV = findViewById<TextView>(R.id.player1FinalScoreTV)
        val player2ScoreTV = findViewById<TextView>(R.id.player2FinalScoreTV)
        val winningPlayerTV = findViewById<TextView>(R.id.winningPlayerTV)

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


        /**UI ELEMENTS END**/









    }
}
