package ru.chernikovanton.lab27

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gravitySensor: Sensor? = null

    private lateinit var ballView: BallView
    private lateinit var resetButton: Button

    companion object {
        private const val BALL_STATE_KEY = "ball_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ballView = findViewById(R.id.ballView)
        resetButton = findViewById(R.id.resetButton)

        resetButton.setOnClickListener {
            ballView.resetBallPosition()
        }

        setupSensor()

        savedInstanceState?.getBundle(BALL_STATE_KEY)?.let {
            ballView.restoreState(it)
        }
    }

    private fun setupSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (gravitySensor == null) {
            resetButton.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        gravitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(BALL_STATE_KEY, ballView.getState())
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GRAVITY || event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            ballView.updateState(event.values[0], event.values[1])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }
}