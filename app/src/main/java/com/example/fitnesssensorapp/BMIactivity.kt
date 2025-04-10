package com.example.fitnesssensorapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BMIactivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bmiactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val height = findViewById<EditText>(R.id.eTHeight)
        val weight = findViewById<EditText>(R.id.eTWeight)
        val btnCalc = findViewById<Button>(R.id.btnBMI)
        val resultText = findViewById<TextView>(R.id.tVResult)
        btnCalc.setOnClickListener {
            val h = height.text.toString().toFloat() / 100
            val w = (weight.text.toString()).toFloat()
            val res = w / (h * h)

            if (res < 18.5) {
                resultText.text = "Your BMI is ${res.toString()}\nUnderweight"
            } else if (res in 18.5..24.9) {
                resultText.text = "Your BMI is ${res.toString()}\nNormal"
            } else if (res in 25.0..29.9) {
                resultText.text = "Your BMI is ${res.toString()}\nOverweight"
            } else {
                resultText.text = "Your BMI is ${res.toString()}\nObese"
            }
        }
    }
}