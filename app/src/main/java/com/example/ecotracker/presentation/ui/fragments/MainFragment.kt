package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecotracker.databinding.FragmentMainBinding
import com.example.ecotracker.R

class MainFragment : Fragment() {

    var myHabitsFragment: MyHabitsFragment = MyHabitsFragment()
    var exFragment2: StatisticsFragment = StatisticsFragment()
    var ratingFragment: RatingFragment = RatingFragment()
    var settingsFragment: ExFragment = ExFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getChildFragmentManager().beginTransaction()
            .replace(R.id.inner_fragment, myHabitsFragment).commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.bottomNavView.setOnItemSelectedListener{

            when (it.itemId) {

                R.id.home_page -> {
                    getChildFragmentManager().beginTransaction()
                        .replace(R.id.inner_fragment, myHabitsFragment).commit()
                }
                R.id.stats_page -> {
                    getChildFragmentManager().beginTransaction()
                        .replace(R.id.inner_fragment, exFragment2).commit()
                }
                R.id.rating_page -> {
                    getChildFragmentManager().beginTransaction()
                        .replace(R.id.inner_fragment, ratingFragment).commit()
                }
                R.id.profile_page -> {
                    getChildFragmentManager().beginTransaction()
                        .replace(R.id.inner_fragment, settingsFragment).commit()
                }

            }
            true
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