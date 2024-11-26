package com.example.playoke

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.playoke.databinding.FragmentHomeBinding
import com.example.playoke.databinding.FragmentLibraryBinding
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

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
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