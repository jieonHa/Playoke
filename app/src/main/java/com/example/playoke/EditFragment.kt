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
import com.bumptech.glide.Glide
import com.example.playoke.databinding.FragmentEditBinding
import com.example.playoke.databinding.FragmentPlaylistBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditFragment : Fragment() {

    private var columnCount = 1
    lateinit var binding: FragmentEditBinding
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
        binding = FragmentEditBinding.inflate(inflater, container, false)

        // 전달받은 playlistId 받기
        collectionPath = arguments?.getString("collectionPath") ?: throw IllegalArgumentException("collectionPath is missing!")
        Log.d("EditFragment", "Arguments: $arguments")
        documentPath = arguments?.getString("documentPath") ?: throw IllegalArgumentException("documentPath is missing!")
        Log.d("EditFragment", "Arguments: $arguments")
        playlistId = arguments?.getString("playlistId") ?: throw IllegalArgumentException("Playlist ID is missing!")
        Log.d("EditFragment", "Arguments: $arguments")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance()

        // RecyclerView 설정
        binding.recyclerViewEdit.layoutManager=LinearLayoutManager(context)
        binding.recyclerViewEdit.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Firestore에서 song 데이터 가져오기
        firestore.collection(collectionPath)
            .document(documentPath)
            .collection("playlists")
            .document(playlistId) // 예: "Ballard"
            .get()
            .addOnSuccessListener { document ->
                val fetchedEdit = mutableListOf<Edit>()

                if (document != null && document.exists()) {
                    // 필드에서 노래 ID를 추출
                    val numberOfSongs = document.getLong("numberOfSongs")?.toInt() ?: 0
                    val songIds = mutableListOf<String>()
                    for (i in 1..numberOfSongs) {
                        document.getString(i.toString())?.let { songIds.add(it) }
                    }

                    // 각 노래 ID로 musics 컬렉션에서 상세 정보 가져오기
                    for (songId in songIds) {
                        firestore.collection("musics")
                            .document(songId)
                            .get()
                            .addOnSuccessListener { songDoc ->
                                if (songDoc != null && songDoc.exists()) {
                                    val name = songDoc.getString("name") ?: ""
                                    val artist = songDoc.getString("artist") ?: ""
                                    val coverImageUrl = songDoc.getString("img") ?: ""
                                    // val lyrics = songDoc.getString("lyrics") ?: ""

                                    // Firestore 데이터로 Song 객체 생성
                                    fetchedEdit.add(Edit(name, artist, coverImageUrl))

                                    // 결과 확인
                                    Log.d("EditFragment", "Fetched Song: $name, Artist: $artist")

                                    // Adapter 설정
                                    binding.recyclerViewEdit.adapter = EditAdapter(fetchedEdit)
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

                    // 이미지 설정
                    if (coverImageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(coverImageUrl)
                            .into(binding.ivEditCover)
                    } else {
                        binding.ivEditCover.setImageResource(R.drawable.img_error)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}

data class Edit(val name: String, val artist: String, val coverImageUrl: String)