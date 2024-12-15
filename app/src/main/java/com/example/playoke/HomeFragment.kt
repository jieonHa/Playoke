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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playoke.databinding.FragmentHomeBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance()

        // 빠른 추천 RecyclerView 설정
        binding.rvQuickRec.layoutManager = GridLayoutManager(context, 3)
        binding.rvQuickRec.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Firestore에서 데이터 가져오기
        firestore.collection("users")
            .document("user-1")
            .collection("playlists")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedPlaylists = mutableListOf<HomePlaylist>()
                for (document in documents) {
                    val playlistId = document.id  // 컬렉션 하위 문서의 ID를 타이틀로 사용
                    val coverImageUrl = document.getString("playlistImg") ?: ""

                    // Firestore 데이터로 HomePlaylist 객체 생성
                    fetchedPlaylists.add(HomePlaylist(playlistId, coverImageUrl))
                    Log.d("Firestore", "Fetched Playlists: $fetchedPlaylists")
                }

                // Adapter 설정
                binding.rvQuickRec.adapter = HomeAdapter(fetchedPlaylists) { playlistId ->
                    val playlistFragment = PlaylistFragment().apply {
                        arguments = Bundle().apply {
                            putString("collectionPath", "users")
                            putString("documentPath", "user-1")
                            putString("playlistId", playlistId)  // playlistId 전달
                            Log.d("HomeFragment", "Sending Playlist ID: $playlistId")

                        }
                    }

                    // 프래그먼트 트랜잭션
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, playlistFragment)
                        .addToBackStack(null)  // 백 스택에 추가
                        .commit()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load playlists: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // 인기차트 RecyclerView 설정
        binding.rvPopularChart.layoutManager = GridLayoutManager(context, 3)
        binding.rvPopularChart.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Firestore에서 데이터 가져오기
        firestore.collection("main")
            .document("PopularChart")
            .collection("playlists")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedPlaylists = mutableListOf<HomePlaylist>()
                for (document in documents) {
                    val playlistId = document.id
                    val coverImageUrl = document.getString("playlistImg") ?: ""

                    // Firestore 데이터로 HomePlaylist 객체 생성
                    fetchedPlaylists.add(HomePlaylist(playlistId, coverImageUrl))
                    Log.d("Firestore", "Fetched Playlists: $fetchedPlaylists")
                }

                // Adapter 설정
                binding.rvPopularChart.adapter = HomeAdapter(fetchedPlaylists) { playlistId ->
                    val playlistFragment = PlaylistFragment().apply {
                        arguments = Bundle().apply {
                            putString("collectionPath", "main")
                            putString("documentPath", "PopularChart")
                            putString("playlistId", playlistId)  // playlistId 전달
                            Log.d("HomeFragment", "Sending Playlist ID: $playlistId")
                        }
                    }

                    // 프래그먼트 트랜잭션
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, playlistFragment)
                        .addToBackStack(null)  // 백 스택에 추가
                        .commit()
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load playlists: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // 상황별 추천 RecyclerView 설정
        binding.rvSituationRec.layoutManager = GridLayoutManager(context, 3)
        binding.rvSituationRec.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Firestore에서 데이터 가져오기
        firestore.collection("main")
            .document("SituationRec")
            .collection("playlists")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedPlaylists = mutableListOf<HomePlaylist>()
                for (document in documents) {
                    val playlistId = document.id
                    val coverImageUrl = document.getString("playlistImg") ?: ""

                    // Firestore 데이터로 HomePlaylist 객체 생성
                    fetchedPlaylists.add(HomePlaylist(playlistId, coverImageUrl))
                    Log.d("Firestore", "Fetched Playlists: $fetchedPlaylists")
                }

                // Adapter 설정
                binding.rvSituationRec.adapter = HomeAdapter(fetchedPlaylists) { playlistId ->
                    val playlistFragment = PlaylistFragment().apply {
                        arguments = Bundle().apply {
                            putString("collectionPath", "main")
                            putString("documentPath", "SituationRec")
                            putString("playlistId", playlistId)  // playlistId 전달
                            Log.d("HomeFragment", "Sending Playlist ID: $playlistId")

                        }
                    }

                    // 프래그먼트 트랜잭션
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, playlistFragment)
                        .addToBackStack(null)  // 백 스택에 추가
                        .commit()
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load playlists: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.show()
        Log.d("HomeFragment", "hometoolbar resume")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }
}

data class HomePlaylist(val title: String, val coverImageUrl: String)