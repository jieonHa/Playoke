package com.example.playoke

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playoke.databinding.FragmentSearchBinding
import com.example.playoke.databinding.ViewholderPlaylistBinding
import com.example.playoke.databinding.ViewholderSearchBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class SearchViewHolder(val binding: ViewholderSearchBinding): RecyclerView.ViewHolder(binding.root)
class SearchAdapter(val context: Context, val ids:MutableList<String?>, val names:MutableList<String?>, val artists:MutableList<String?>, val images:MutableList<String?>): RecyclerView.Adapter<SearchViewHolder>(){
    override fun getItemCount():Int = names.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):SearchViewHolder =
        SearchViewHolder(ViewholderSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder:SearchViewHolder, position:Int){
        Log.d("Firestore", "IDs List: $ids")
        holder.binding.songTitle.text = names[position]
        holder.binding.songArtist.text = artists[position]
        Glide.with(context)
            .load(images[position])
            .into(holder.binding.songImage)
        holder.binding.musicInfoContainer.setOnClickListener{
            Log.d("Firebase", "Set On Click Listener is Working")
            UserInfo.selectedPlaylist = "Exciting Pop"
            UserInfo.playlistLength = 0
            UserInfo.selectedMusic = 0
            Log.d("Firebase", "Id is ${ids[position]}")
            UserInfo.playingMusic = "${ids[position]}"
            UserInfo.musicName = "${names[position]}"
            UserInfo.musicImgSrc = "${images[position]}"
            UserInfo.artistName = "${artists[position]}"
            UserInfo.selectedPlaylist = ""


            val intent: Intent = Intent(context, MusicActivity::class.java)
            intent.putExtra("restart",true)
            context.startActivity(intent)
        }
    }
}

class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val ids = mutableListOf<String?>()
    private val names = mutableListOf<String?>()
    private val artists = mutableListOf<String?>()
    private val images = mutableListOf<String?>()
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the binding class
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up button click listener
        binding.deleteBtn.setOnClickListener {
            binding.searchBar.setText("")
        }
        binding.searchBar.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                //binding.recyclerView.removeAllViews()
                ids.clear()
                names.clear()
                artists.clear()
                images.clear()
                binding.recyclerView.adapter?.notifyDataSetChanged()

                val searchWord = binding.searchBar.text.toString().trim() // Get user input

                if (searchWord.isNotEmpty()) {
                    // Firestore query using startAt and endAt for partial matching
                    db.collection("musics")
                        .orderBy("name") // Ensure the field is indexed and ordered
                        .startAt(searchWord)
                        .endAt(searchWord + "\uf8ff") // Unicode trick for partial matching
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                // Iterate through matching documents
                                for (document in querySnapshot.documents) {
                                    val id = document.getId()
                                    val name = document.getString("name")
                                    val artist = document.getString("artist")
                                    val imgUrl = document.getString("img")

                                    ids.add(id)
                                    names.add(name)
                                    artists.add(artist)
                                    images.add(imgUrl)
                                    Log.d("Firestore", "Document ID: $id")
                                }
                                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                                binding.recyclerView.adapter = SearchAdapter(requireContext(), ids, names, artists, images)
                            } else {
                                // No matches found
                                Log.d("Firestore", "No music found matching: $searchWord")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error searching artist", e)
                        }
                } else {
                    Log.d("Firestore", "Search word is empty")
                }
                true // Consume the event
            } else {
                false // Let the event propagate further
            }
        }

    }

}