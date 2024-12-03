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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.playoke.databinding.FragmentLibraryBinding
import androidx.appcompat.widget.Toolbar
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

        // 메뉴 아이템 클릭 시 이벤트 처리
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


        // Firestore에서 플레이리스트 데이터를 가져와 RecyclerView에 설정
//        firestore.collection("playlists")
//            .get()
//            .addOnSuccessListener { documents ->
//                libraryplaylists.clear()
//                for (document in documents) {
//                    val title = document.getString("title") ?: "Unknown Title"
//                    val numberOfSongs = document.getLong("numberOfSongs")?.toInt() ?: 0
//                    val coverImageResId =
//                        document.getLong("coverImageResId")?.toInt() ?: R.drawable.img_music
//                    libraryplaylists.add(LibraryPlaylist(title, numberOfSongs, coverImageResId))
//                }
//            }

        val libraryplaylists = listOf(
            LibraryPlaylist("Playlist Title 1", 10, R.drawable.img_music),
            LibraryPlaylist("Playlist Title 2", 10, R.drawable.img_music),
            LibraryPlaylist("Playlist Title 3", 10, R.drawable.img_music)
        )

        binding.recyclerViewPlaylists.layoutManager=LinearLayoutManager(context)
        binding.recyclerViewPlaylists.adapter=LibraryPlaylistAdapter(libraryplaylists)
        binding.recyclerViewPlaylists.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
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

data class LibraryPlaylist(val title: String, val numberOfSongs: Int, val coverImageResId: Int)

