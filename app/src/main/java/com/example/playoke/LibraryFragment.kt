package com.example.playoke

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.playoke.databinding.FragmentLibraryBinding
import androidx.drawerlayout.widget.DrawerLayout
import com.example.playoke.placeholder.PlaceholderContent
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore


/**
 * A fragment representing a list of Items.
 */
class LibraryFragment : Fragment() {

    private var columnCount = 1
    lateinit var binding: FragmentLibraryBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var firestore: FirebaseFirestore

    // Firestore에서 가져올 플레이리스트 데이터를 담을 리스트
    private val libraryplaylists = mutableListOf<LibraryPlaylist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // FragmentLibraryBinding을 사용하여 바인딩 초기화
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout)

        // 툴바 설정
        val toolbar: MaterialToolbar = binding.libraryToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // 로고 이미지 클릭 시 이벤트 처리: 드로어
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        // 메뉴 아이템 클릭 시 이벤트 처리: 플리 추가 화면으로 이동
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

        // RecyclerView 설정
        binding.recyclerViewPlaylists.layoutManager=LinearLayoutManager(context)
        binding.recyclerViewPlaylists.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Firestore에서 데이터 가져오기
        firestore.collection("users")
            .document("user-1")
            .collection("playlists")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedPlaylists = mutableListOf<LibraryPlaylist>()
                for (document in documents) {
                    val playlistId = document.id  // 컬렉션 하위 문서의 ID를 타이틀로 사용
                    val numberOfSongs = document.getLong("numberOfSongs")?.toInt() ?: 0
                    val coverImageUrl = document.getString("playlistImg") ?: ""

                    // Firestore 데이터로 LibraryPlaylist 객체 생성
                    fetchedPlaylists.add(LibraryPlaylist(playlistId, numberOfSongs, coverImageUrl))
                }

                // Adapter 설정
                binding.recyclerViewPlaylists.adapter = LibraryPlaylistAdapter(fetchedPlaylists) { playlistId ->
                    val playlistFragment = PlaylistFragment().apply {
                        arguments = Bundle().apply {
                            putString("collectionPath", "users")
                            putString("documentPath", "user-1")
                            putString("playlistId", playlistId)  // playlistId 전달
                            Log.d("LibraryFragment", "Sending Playlist ID: $playlistId")

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

    override fun onResume() {
        super.onResume()
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.show()
        Log.d("yerim", "libtoolbar resume")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            LibraryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }
}

data class LibraryPlaylist(val title: String, val numberOfSongs: Int, val coverImageUrl: String)

