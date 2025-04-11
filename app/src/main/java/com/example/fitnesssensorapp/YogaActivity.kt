package com.example.fitnesssensorapp

// YogaActivity.kt
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class YogaActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button
    private lateinit var poseName: TextView

    private var timeLeft: Long = 300000 // 5 minutes in milliseconds
    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var currentPose = 0
    private val poses = arrayOf(
        "Mountain Pose" to 300000L,
        "Downward Dog" to 300000L,
        "Warrior II" to 300000L,
        "Tree Pose" to 300000L
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yoga)

        timerText = findViewById(R.id.timerTextView)
        btnStart = findViewById(R.id.btnStart)
        btnPause = findViewById(R.id.btnPause)
        btnReset = findViewById(R.id.btnReset)
        poseName = findViewById(R.id.poseName)

        setupButtons()
        updatePoseDisplay()
    }

    private fun setupButtons() {
        btnStart.setOnClickListener {
            startTimer()
            btnStart.isEnabled = false
            btnPause.isEnabled = true
        }

        btnPause.setOnClickListener {
            pauseTimer()
            btnStart.isEnabled = true
            btnPause.isEnabled = false
        }

        btnReset.setOnClickListener {
            resetTimer()
            moveToNextPose()
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                isRunning = false
                moveToNextPose()
            }
        }.start()
        isRunning = true
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
    }

    private fun resetTimer() {
        timer?.cancel()
        timeLeft = poses[currentPose].second
        updateTimer()
        isRunning = false
    }

    private fun updateTimer() {
        val minutes = (timeLeft / 1000) / 60
        val seconds = (timeLeft / 1000) % 60
        timerText.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun moveToNextPose() {
        currentPose = (currentPose + 1) % poses.size
        timeLeft = poses[currentPose].second
        updatePoseDisplay()
        resetTimer()
        if(isRunning) startTimer()
    }

    private fun updatePoseDisplay() {
        poseName.text = poses[currentPose].first
        timerText.text = "05:00"
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}