package com.example.playoke

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playoke.databinding.FragmentPlaylistBinding
import com.google.firebase.firestore.FirebaseFirestore

class PlaylistFragment : Fragment() {

    lateinit var binding: FragmentPlaylistBinding
    private lateinit var firestore: FirebaseFirestore
    private var collectionPath: String = ""
    private var documentPath: String = ""
    private var playlistId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        // 전달받은 playlistId 받기
        collectionPath = arguments?.getString("collectionPath") ?: throw IllegalArgumentException("collectionPath is missing!")
        Log.d("PlaylistFragment", "Arguments: $arguments")
        documentPath = arguments?.getString("documentPath") ?: throw IllegalArgumentException("documentPath is missing!")
        Log.d("PlaylistFragment", "Arguments: $arguments")
        playlistId = arguments?.getString("playlistId") ?: throw IllegalArgumentException("Playlist ID is missing!")
        Log.d("PlaylistFragment", "Arguments: $arguments")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance()

        // RecyclerView 설정
        binding.recyclerViewSongs.layoutManager=LinearLayoutManager(context)
        binding.recyclerViewSongs.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Firestore에서 song 데이터 가져오기
        firestore.collection(collectionPath)
            .document(documentPath)
            .collection("playlists")
            .document(playlistId) // 예: "Ballard"
            .get()
            .addOnSuccessListener { document ->
                val fetchedSongs = mutableListOf<Song>()

                if (document != null && document.exists()) {
                    // 필드에서 노래 ID를 추출
                    val numberOfSongs = document.getLong("numberOfSongs")?.toInt() ?: 0
                    val songIds = mutableListOf<String>()
                    for (i in 1..numberOfSongs) {
                        document.getString(i.toString())?.let { songIds.add(it) }
                    }

                    // 각 노래 ID로 musics 컬렉션에서 상세 정보 가져오기
                    var index = 1
                    for (songId in songIds) {
                        firestore.collection("musics")
                            .document(songId)
                            .get()
                            .addOnSuccessListener { songDoc ->
                                if (songDoc != null && songDoc.exists()) {
                                    val id = songId
                                    val name = songDoc.getString("name") ?: ""
                                    val artist = songDoc.getString("artist") ?: ""
                                    val coverImageUrl = songDoc.getString("img") ?: ""
                                    // val lyrics = songDoc.getString("lyrics") ?: ""

                                    // Song 객체 생성
                                    fetchedSongs.add(Song(id, name, artist, coverImageUrl, index))

                                    // 인덱스 증가
                                    index++

                                    // Adapter 설정
                                    binding.recyclerViewSongs.adapter = SongAdapter(fetchedSongs) { song ->

                                        // 노래 클릭 시 UserInfo 업데이트
                                        UserInfo.playlistLength = numberOfSongs
                                        UserInfo.selectedMusic = song.index
                                        UserInfo.playingMusic = song.id
                                        UserInfo.musicName = song.name
                                        UserInfo.musicImgSrc = song.coverImageUrl
                                        UserInfo.artistName = song.artist
                                        UserInfo.selectedPlaylist = playlistId
                                        // 업데이트된 UserInfo 로그 메시지 출력
                                        Log.d("PlaylistFragment", "UserInfo updated: " +
                                                "selectedMusic=${UserInfo.selectedMusic}" +
                                                "selectedPlaylist=${UserInfo.selectedPlaylist}, " +
                                                "playlistLength=${UserInfo.playlistLength}, "
                                        )

                                        val intent: Intent = Intent(context, MusicActivity::class.java)
                                        intent.putExtra("restart",true)
                                        context?.startActivity(intent)
                                    }
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error fetching playlist or song details", e)
            }

        // Firestore에서 playlist 데이터 가져오기
        firestore.collection(collectionPath)
            .document(documentPath)
            .collection("playlists")
            .document(playlistId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val playlistId = playlistId
                    val coverImageUrl = document.getString("playlistImg") ?: ""

                    // 이름 설정
                    binding.tvPlaylistName.text = playlistId

                    // 이미지 설정
                    if (coverImageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(coverImageUrl)
                            .into(binding.ivPlaylistCover)
                    } else {
                        binding.ivPlaylistCover.setImageResource(R.drawable.img_error)
                    }
                } else {
                    Toast.makeText(context, "Playlist does not exist.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load playlists: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // btnBack 버튼 클릭 이벤트 처리
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        // btnEdit 버튼 클릭 이벤트 처리
        binding.btnEdit.setOnClickListener {
            // EditFragment에 전달할 arguments 생성
            val editFragment = EditFragment().apply {
                arguments = Bundle().apply {
                    putString("collectionPath", collectionPath)
                    putString("documentPath", documentPath)
                    putString("playlistId", playlistId) // playlistId 전달
                    Log.d("PlaylistFragment", "Sending Playlist ID: $playlistId")
                }
            }

            // Fragment 교체 작업
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, editFragment) // EditFragment로 교체
            fragmentTransaction.addToBackStack(null) // 백 스택에 추가
            fragmentTransaction.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}

data class Song(val id: String, val name: String, val artist: String, val coverImageUrl: String, val index: Int)
