package com.example.playoke

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playoke.databinding.FragmentLibraryBinding
import com.example.playoke.databinding.FragmentPlaylistBinding
import com.google.firebase.firestore.FirebaseFirestore


/**
 * A simple [Fragment] subclass.
 * Use the [PlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class PlaylistFragment : Fragment() {

    private var columnCount = 1
    lateinit var binding: FragmentPlaylistBinding
    private lateinit var firestore: FirebaseFirestore
    private var playlistId: String = ""


    var ARG_PARAM1 = "param1"
    var ARG_PARAM2 = "param2"
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
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        // 전달받은 playlistId 받기
        // playlistId = requireArguments().getString("plalistId") ?: throw IllegalArgumentException("Playlist ID is missing!")
        playlistId = arguments?.getString("playlistId") ?: throw IllegalArgumentException("Playlist ID is missing!")
        Log.d("getPlaylistId", playlistId)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance()

        // RecyclerView 설정
        binding.recyclerViewSongs.layoutManager=LinearLayoutManager(context)
        binding.recyclerViewSongs.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Firestore에서 데이터 가져오기
        firestore.collection("users")
            .document("user-1")
            .collection("playlists")
            .document(playlistId)
            .collection("songs")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedSongs = mutableListOf<Song>()
                for (document in documents) {
                    val name = document.getString("name") ?: ""
                    val artist = document.getString("artist") ?: ""
                    val coverImageUrl = document.getString("img") ?: ""

                    // Firestore 데이터로 LibraryPlaylist 객체 생성
                    fetchedSongs.add(Song(name, artist, coverImageUrl))
                }

                // Adapter 설정
                binding.recyclerViewSongs.adapter = SongAdapter(fetchedSongs)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load playlists: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // btnBack 버튼 클릭 이벤트 처리
        binding.btnBack.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, LibraryFragment()) // HomeFragment도 추가 필요
            fragmentTransaction.addToBackStack(null) // 백 스택에 추가
            fragmentTransaction.commit()
        }
        // btnEdit 버튼 클릭 이벤트 처리
        binding.btnEdit.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, EditFragment()) // EditFragment로 교체
            fragmentTransaction.addToBackStack(null) // 백 스택에 추가
            fragmentTransaction.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlaylistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlaylistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

data class Song(val name: String, val artist: String, val coverImageUrl: String)
