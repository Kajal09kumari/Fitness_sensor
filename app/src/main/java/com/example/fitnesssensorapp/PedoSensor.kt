package com.example.fitnesssensorapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlin.math.pow
import kotlin.math.sqrt

class PedoSensor : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null

    // Step counting variables
    private var initialSteps = 0
    private var currentSteps = 0
    private val targetSteps = 2500
    private var usingAccelerometer = false
    private val accelerometerWindow = ArrayList<Double>()
    private var lastStepTime = 0L
    private val stepCooldown = 300L // milliseconds
    private var lastAccValues = FloatArray(3)

    // UI components
    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var tvStepsTaken: TextView

    // Constants
    private val ACTIVITY_RECOGNITION_REQUEST = 100
    private val TAG = "PedoSensor"
    private val ACCELEROMETER_WINDOW_SIZE = 15
    private val STEP_THRESHOLD = 1.5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedo_sensor)

        // Initialize UI components
        circularProgressBar = findViewById(R.id.circularProgressBar)
        tvStepsTaken = findViewById(R.id.tv_stepsTaken)

        setupProgressBar()
        checkPermissions()
    }

    private fun setupProgressBar() {
        circularProgressBar.apply {
            progressMax = targetSteps.toFloat()
            setProgressWithAnimation(0f, 1000)
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    ACTIVITY_RECOGNITION_REQUEST
                )
            } else {
                setupSensors()
            }
        } else {
            setupSensors()
        }
    }

    private fun setupSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Try to get step counter sensor first
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
            Log.d(TAG, "Using STEP_COUNTER sensor")
        } else {
            // Fallback to accelerometer
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
                usingAccelerometer = true
                Log.d(TAG, "Using ACCELEROMETER fallback")
            } else {
                Toast.makeText(this, "No sensors available!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> handleStepCounter(event)
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event)
        }
    }

    private fun handleStepCounter(event: SensorEvent) {
        if (initialSteps == 0) initialSteps = event.values[0].toInt()
        currentSteps = event.values[0].toInt() - initialSteps
        updateUI()
    }

    private fun handleAccelerometer(event: SensorEvent) {
        if (!usingAccelerometer) return

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastStepTime < stepCooldown) return

        // Apply low-pass filter to reduce noise
        val filteredValues = lowPassFilter(event.values)
        val magnitude = sqrt(
            filteredValues[0].toDouble().pow(2) +
                    filteredValues[1].toDouble().pow(2) +
                    filteredValues[2].toDouble().pow(2)
        )

        accelerometerWindow.add(magnitude)
        if (accelerometerWindow.size > ACCELEROMETER_WINDOW_SIZE) {
            accelerometerWindow.removeAt(0)
        }

        detectStep()
    }

    private fun lowPassFilter(input: FloatArray): FloatArray {
        val alpha = 0.8f
        val output = FloatArray(3)
        output[0] = input[0] * alpha + lastAccValues[0] * (1 - alpha)
        output[1] = input[1] * alpha + lastAccValues[1] * (1 - alpha)
        output[2] = input[2] * alpha + lastAccValues[2] * (1 - alpha)
        lastAccValues = output
        return output
    }

    private fun detectStep() {
        if (accelerometerWindow.size < ACCELEROMETER_WINDOW_SIZE) return

        val avg = accelerometerWindow.average()
        val variance = accelerometerWindow.map { (it - avg).pow(2) }.average()
        val stdDev = sqrt(variance)

        val currentMagnitude = accelerometerWindow.last()
        val threshold = avg + stdDev * STEP_THRESHOLD

        if (currentMagnitude > threshold) {
            currentSteps++
            lastStepTime = System.currentTimeMillis()
            updateUI()
        }
    }

    private fun updateUI() {
        runOnUiThread {
            tvStepsTaken.text = "steps:$currentSteps"
            circularProgressBar.setProgressWithAnimation(currentSteps.toFloat(), 500)

            if (currentSteps >= targetSteps) {
                circularProgressBar.progressBarColor = getColor(R.color.green)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupSensors()
            } else {
                Toast.makeText(this, "Permission required for step counting!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        setupSensors()
    }
}