package com.example.ecotracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecotracker.databinding.FragmentMainBinding
import com.example.ecotracker.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {

    var myHabitsFragment: MyHabitsFragment = MyHabitsFragment()
    var exFragment2: StatisticsFragment = StatisticsFragment()
    var ratingFragment: RatingFragment = RatingFragment()
    var settingsFragment: ExFragment = ExFragment()
//    private var param1: String? = null
//    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }

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