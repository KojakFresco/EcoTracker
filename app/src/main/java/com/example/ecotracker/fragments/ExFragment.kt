package com.example.ecotracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.MainActivity
import com.example.ecotracker.databinding.FragmentExBinding
import com.example.ecotracker.databinding.FragmentMainBinding
import com.parse.GetCallback
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.SaveCallback
import java.time.ZoneId
import java.time.ZonedDateTime

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ExFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var counter = 0
    lateinit var userId: String
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val binding = FragmentExBinding.inflate(inflater, container, false)

        userId = (activity as MainActivity).userId

        val label: TextView = binding.label
        val updateButton: Button = binding.updateButton
        val saveButton: Button = binding.saveButton
        var lastTime: ZonedDateTime

        counter = (activity as MainActivity).pref?.getInt("counter", 0)!!
        Log.d(LOG_LABEL, counter.toString())
        label.text = "Вы помогали экологии $counter дней!"
        lastTime =(activity as MainActivity).loadDate("lastTime")

        updateButton.setOnClickListener {
            val time = ZonedDateTime.now(ZoneId.systemDefault())
            if (time.dayOfYear > lastTime.dayOfYear && time.year >= lastTime.year) {
                counter+=1
                val text = "Вы помогали экологии $counter дней!"
                (activity as MainActivity).saveInt("counter", counter)
                (activity as MainActivity).saveDate("lastTime", time)

                lastTime = time
                label.text = text

                Toast.makeText(activity, "Вы молодец!",
                    Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(activity, "Ты кого наобмануть пытаешься???",
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
                                Toast.makeText(activity, "Стрик сохранён",
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity, "Error occured",
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
                                (activity as MainActivity).saveString("userId", userId)
                                Toast.makeText(activity, "Стрик сохранён",
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity, "Error occured",
                                    Toast.LENGTH_SHORT).show()
                                Log.e(LOG_LABEL, "Error updating object: " + e1.message)
                            }})
                        Toast.makeText(activity, "Error occured",
                            Toast.LENGTH_SHORT).show()
                        Log.e(LOG_LABEL, "Error retrieving object: " + e.message)
                    }
                })
        }

        return binding.root
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment MainFragment.
//         */
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MainFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}