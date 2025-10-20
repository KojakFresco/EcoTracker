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
import com.parse.GetCallback
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.SaveCallback
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {
    var counter = 0
    var pref : SharedPreferences? = null

//    var menuFragment: MainFragment = MainFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var userId : String

        val label: TextView = findViewById(R.id.label)
        val updateButton: Button = findViewById(R.id.updateButton)
        val saveButton: Button = findViewById(R.id.saveButton)
        var lastTime: ZonedDateTime

        pref = getSharedPreferences("TABLE", MODE_PRIVATE)
        userId = pref?.getString("userId", "null")!!
        counter = pref?.getInt("counter", 0)!!
        Log.d(LOG_LABEL, counter.toString())
        label.text = "Вы помогали экологии $counter дней!"
        lastTime = loadDate("lastTime")

        updateButton.setOnClickListener {
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

        saveButton.setOnClickListener {
            val query = ParseQuery.getQuery<ParseObject?>("Streak")
            query.getInBackground(
                userId,
                GetCallback { streak: ParseObject?, e: ParseException? ->
                    if (e == null) {
                        streak!!.put("streak", counter)
                        streak.saveInBackground(SaveCallback { e1: ParseException? ->
                            if (e1 == null) {
                                Toast.makeText(this, "Стрик сохранён",
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error occured",
                                    Toast.LENGTH_SHORT).show()
                                Log.e(LOG_LABEL, "Error updating object: " + e1.message)
                            }
                        })
                    } else {
                        val streak = ParseObject("Streak")
                        streak.put("user", "admin")
                        streak.put("streak", counter)
                        streak.saveInBackground(SaveCallback { e1: ParseException? ->
                            if (e1 == null) {
                                userId = streak.objectId
                                saveString("userId", userId)
                                Toast.makeText(this, "Стрик сохранён",
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error occured",
                                    Toast.LENGTH_SHORT).show()
                                Log.e(LOG_LABEL, "Error updating object: " + e1.message)
                            }})
                            Toast.makeText(this, "Error occured",
                                Toast.LENGTH_SHORT).show()
                        Log.e(LOG_LABEL, "Error retrieving object: " + e.message)
                    }
                })
        }

//        getSupportFragmentManager().beginTransaction()
//            .replace(R.id.main_fragment, menuFragment, "menu").commit()
    }
    fun saveInt(name : String, res : Int) {
        val editor = pref?.edit()
        editor?.putInt(name, res)
        editor?.apply()
    }

    fun saveString(name : String, str : String) {
        val editor = pref?.edit()
        editor?.putString(name, str)
        editor?.apply()
    }
    fun saveDate(name: String, date : ZonedDateTime) {
        val editor = pref?.edit()
        val j: String = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault()).format(date)
        editor?.putString(name, j)
        editor?.apply()
        Log.d(LOG_LABEL, "load in save " + pref?.getString(name, null))
    }

    fun loadDate(name: String): ZonedDateTime {
        val j = pref?.getString(name, null)
        Log.d(LOG_LABEL, "load in load $j")
        if (j == null) {
            Log.e(LOG_LABEL, "load error")
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