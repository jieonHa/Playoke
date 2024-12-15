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
            .document(playlistId)
            .collection("songs")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedEdit = mutableListOf<Edit>()
                for (document in documents) {
                    val name = document.getString("name") ?: ""
                    val artist = document.getString("artist") ?: ""
                    val coverImageUrl = document.getString("img") ?: ""

                    // Firestore 데이터로 Song 객체 생성
                    fetchedEdit.add(Edit(name, artist, coverImageUrl))

                }

                // Adapter 설정
                binding.recyclerViewEdit.adapter = EditAdapter(fetchedEdit)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load playlists: ${exception.message}", Toast.LENGTH_SHORT).show()
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