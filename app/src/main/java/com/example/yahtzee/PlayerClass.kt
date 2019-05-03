package com.example.yahtzee

data class Player(val name: String, val playerScoreSheet: List<ScoreBox>,
                  var upperTotalScore: Int, var totalScore: Int, var upperScoreBonus: Boolean = false,
                  var upperScoreBonusActivated: Boolean = false)