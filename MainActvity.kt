package com.beyond.metro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var stationDropdown: Spinner
    private lateinit var nextTrainTextView: TextView
    private lateinit var countdownTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    
    private val trainSchedule = mapOf(
        "Uttara North" to listOf("06:00", "06:15", "06:30", "06:45", "07:00", "07:15", "07:30"),
        "Pallabi" to listOf("06:02", "06:17", "06:32", "06:47", "07:02", "07:17", "07:32"),
        "Mirpur 10" to listOf("06:05", "06:20", "06:35", "06:50", "07:05", "07:20", "07:35"),
        "Farmgate" to listOf("06:10", "06:25", "06:40", "06:55", "07:10", "07:25", "07:40"),
        "Motijheel" to listOf("06:15", "06:30", "06:45", "07:00", "07:15", "07:30", "07:45")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stationDropdown = findViewById(R.id.stationDropdown)
        nextTrainTextView = findViewById(R.id.nextTrainTextView)
        countdownTextView = findViewById(R.id.countdownTextView)

        val stationNames = trainSchedule.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stationNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stationDropdown.adapter = adapter

        stationDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedStation = stationNames[position]
                updateNextTrain(selectedStation)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                val selectedStation = stationDropdown.selectedItem.toString()
                updateNextTrain(selectedStation)
                handler.postDelayed(this, 30000)
            }
        }, 30000)
    }

    private fun updateNextTrain(station: String) {
        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val nextTrain = trainSchedule[station]?.firstOrNull { LocalTime.parse(it, formatter).isAfter(now) }

        if (nextTrain != null) {
            nextTrainTextView.text = "Next train: $nextTrain"
            val trainTime = LocalTime.parse(nextTrain, formatter)
            val minutesLeft = java.time.Duration.between(now, trainTime).toMinutes()
            countdownTextView.text = "Time left: $minutesLeft min"
        } else {
            nextTrainTextView.text = "No more trains today"
            countdownTextView.text = ""
        }
    }
}
