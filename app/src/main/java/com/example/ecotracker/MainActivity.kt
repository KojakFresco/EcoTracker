package com.example.ecotracker

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.ecotracker.fragments.MainFragment
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {
    lateinit var userId : String
    lateinit var fm: FragmentManager
    var pref : SharedPreferences? = null

//    var menuFragment: MainFragment = MainFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        fm = supportFragmentManager

        pref = getSharedPreferences("TABLE", MODE_PRIVATE)
        userId = pref?.getString("userId", "null")!!

        val transaction: FragmentTransaction = fm.beginTransaction()
        transaction.replace(R.id.fragmentContainer, MainFragment(), "main").commit()

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