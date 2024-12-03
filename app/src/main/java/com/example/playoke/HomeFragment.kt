package com.example.playoke

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playoke.databinding.FragmentHomeBinding
import com.google.android.material.appbar.MaterialToolbar

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentHomeBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // FragmentLibraryBinding을 사용하여 바인딩 초기화
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout)

        // 툴바 설정
        val toolbar: MaterialToolbar = binding.homeToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // 로고 이미지 클릭 시 이벤트 처리: 드로어
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        // 메뉴 클릭 리스너 설정 (메뉴 아이템 클릭 시 이벤트 처리)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mainMenuAdd -> {
                    val intent = Intent(context, AddToPlaylistActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 빠른 추천 RecyclerView 설정
        binding.rvQuickRecommendation.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = HomeAdapter(getQuickRecommendationData())
        }

        // 인기 차트 RecyclerView 설정
        binding.rvPopularChart.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = HomeAdapter(getPopularChartData())
        }

        // 상황별 추천 RecyclerView 설정
        binding.rvSituationRecommendation.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = HomeAdapter(getSituationRecommendationData())
        }
    }

    private fun getQuickRecommendationData(): List<String> {
        return listOf("플리 이름 1", "플리 이름 2", "플리 이름 3", "플리 이름 4", "플리 이름 5", "플리 이름 6")
    }

    private fun getPopularChartData(): List<String> {
        return listOf("차트 1", "차트 2", "차트 3", "차트 4", "차트 5", "차트 6")
    }

    private fun getSituationRecommendationData(): List<String> {
        return listOf("비오는 날", "아침", "운동")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.show()
        Log.d("yerim", "hometoolbar resume")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }
}