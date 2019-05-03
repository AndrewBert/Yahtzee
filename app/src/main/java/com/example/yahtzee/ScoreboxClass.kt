package com.example.yahtzee

import android.widget.Button

data class ScoreBox(val button: Button, var value: Int = 0, var isSelected: Boolean = false, var isSaved: Boolean = false, var isCalculated: Boolean = false)