package com.example.fitnesssensorapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()
        val bmi=findViewById<ImageView>(R.id.imgBMI)
        val sensor=findViewById<ImageView>(R.id.imgPedometer)
        bmi.setOnClickListener {
            val i= Intent(this,BMIactivity::class.java)
            startActivity(i)
        }
        sensor.setOnClickListener {
            val i2= Intent(this,PedoSensor::class.java)
            startActivity(i2)
        }
    }
}