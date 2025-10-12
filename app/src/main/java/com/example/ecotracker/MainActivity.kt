package com.example.ecotracker

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    var counter = 0
    var pref : SharedPreferences? = null

//    var menuFragment: MainFragment = MainFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO: push to mai gitlab
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val label: TextView = findViewById(R.id.label)
        val button: Button = findViewById(R.id.updateButton)
        var lastTime: ZonedDateTime

        pref = getSharedPreferences("TABLE", MODE_PRIVATE)
        counter = pref?.getInt("counter", 0)!!
        Log.d("pPrive", counter.toString())
        label.text = "Вы помогали экологии $counter дней!"
        lastTime = loadDate("lastTime")

        button.setOnClickListener {
            val time = ZonedDateTime.now(ZoneId.systemDefault())
            if (time.dayOfYear > lastTime.dayOfYear && time.year >= lastTime.year) {
                counter+=1
                val text = "Вы помогали экологии $counter дней!"
                saveInt("counter", counter)
                saveDate("lastTime", time)

                lastTime = time
                label.text = text

                Toast.makeText(this, "Вы молодец!",
                    Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "Ты кого наобмануть пытаешься???",
                    Toast.LENGTH_SHORT).show()
        }

//        getSupportFragmentManager().beginTransaction()
//            .replace(R.id.main_fragment, menuFragment, "menu").commit()
    }
    fun saveInt(name : String, res : Int) {
        val editor = pref?.edit()
        editor?.putInt(name, res)
        editor?.apply()
    }

    fun saveDate(name: String, date : ZonedDateTime) {
        val editor = pref?.edit()
        val j: String = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault()).format(date)
        editor?.putString(name, j)
        editor?.apply()
        Log.d("pPrive", "load in save " + pref?.getString(name, null))
    }

    fun loadDate(name: String): ZonedDateTime {
        val j = pref?.getString(name, null)
        Log.d("pPrive", "load in load $j")
        if (j == null) {
            Log.e("pPrive", "load error")
            return ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1)
        } else {
            return LocalDateTime.parse(j.replace(" ", "T")).atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault())
        }
    }
    override fun onPause() {
        super.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}