package com.example.musicapp



import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp.adapter.CategoryAdapter
import com.example.musicapp.adapter.SectionListSongAdapter
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.model.CategoryModel
import com.example.musicapp.model.SongsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var categoryAdapter: CategoryAdapter
    @UnstableApi override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.Theme_MusicApp)
        getCategories()
        setupSections("section_1",binding.section1MainLayout,binding.section1Title,binding.section1RecyclerView)
        setupSections("section_2",binding.section2MainLayout,binding.section2Title,binding.section2RecyclerView)
        setupSections("section_3",binding.section3MainLayout,binding.section3Title,binding.section3RecyclerView)
        setupMostlyPlayed("mostly_played",binding.mostPlayedMainLayout,binding.mostPlayedTitle,binding.mostPlayedrecyclerView)

        binding.playerButton.setOnClickListener {
            // Check if a song is currently playing
            if (MyExoplayer.isPlaying()) {
                // If playing, pause the song
                MyExoplayer.pausePlaying()
                // Change the button icon to play icon
                binding.playerButton.setImageResource(R.drawable.baseline_play_circle_24)
            } else {
              //  MyExoplayer.startOrResume()
                binding.playerButton.setImageResource(R.drawable.baseline_pause_circle_24)
            }
        }
        binding.option.setOnClickListener {
            showPopupMenu()
        }

    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this,binding.option)
        val inflator = popupMenu.menuInflater
        inflator.inflate(R.menu.menu_option,popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    logout()
                    true
                }
            R.id.userProfile ->
            {
                startActivity(Intent(this@MainActivity,ProfileActivity::class.java))
            }
                R.id.purchasedSongs->
                {
                    startActivity(Intent(this@MainActivity,PurchasedSongs::class.java))
                }
                R.id.offlinePlaylist->
                {
                    startActivity(Intent(this@MainActivity,OfflinePlaylist::class.java))
                }
                R.id.fav->
                {
                    startActivity(Intent(this@MainActivity,FavoritePlaylist::class.java))
                }
            }
            false
        }

    }
    fun logout(){

        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        showNowPlayingPlayerView()

    }

//categories
    fun getCategories(){
        FirebaseFirestore.getInstance().collection("category")
            .get().addOnSuccessListener {
                val categoryList = it.toObjects(CategoryModel::class.java)
                setupCategoryRecyclerView(categoryList)
            }
    }
    fun showNowPlayingPlayerView()
    {   binding.NPplayerView.setOnClickListener {
        startActivity(Intent(this@MainActivity,ActivityPlayer::class.java))
    }
        MyExoplayer.getCurrentSong()?.let {
            binding.NPplayerView.visibility=View.VISIBLE
            binding.songTitleNP.text=it.title
            binding.songsubTitleNP.text=it.subtitle
            Glide.with(binding.NPsongCoverImageView).load(it.coverUrl)
                .apply (
                    RequestOptions().transform(RoundedCorners(32))
            ).into(binding.NPsongCoverImageView)
        }?: run{
            binding.NPplayerView.visibility=View.GONE

        }
    }
    fun setupCategoryRecyclerView(categoryList : List<CategoryModel>){
        categoryAdapter = CategoryAdapter(categoryList)
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.categoriesRecyclerView.adapter = categoryAdapter
    }
//Sections
    fun setupSections(id:String,mainLayout:RelativeLayout,titleView:TextView,recyclerView: RecyclerView )
{
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {
                val section = it.toObject(CategoryModel::class.java)
                section?.apply {
                    mainLayout.visibility= View.VISIBLE
                    titleView.text=name
                    recyclerView.layoutManager=LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                    recyclerView.adapter=SectionListSongAdapter(songs)
                    mainLayout.setOnClickListener {
                        SongsActivityList.category=section
                        startActivity(Intent(this@MainActivity,SongsActivityList::class.java))
                    }
                }
            }
    }

    fun setupMostlyPlayed(id:String,mainLayout:RelativeLayout,titleView:TextView,recyclerView: RecyclerView )
    {
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {
                FirebaseFirestore.getInstance().collection("songs")
                    .orderBy("count",Query.Direction.DESCENDING)
                    .limit(5)
                    .get()
                    .addOnSuccessListener {songListSnapshot->
                       val songsModelList= songListSnapshot.toObjects<SongsModel>()
                        val songIdList =songsModelList.map {
                            it.id
                        }.toList()
                        val section = it.toObject(CategoryModel::class.java)
                        section?.apply {
                            section.songs=songIdList
                            mainLayout.visibility= View.VISIBLE
                            titleView.text=name
                            recyclerView.layoutManager=LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                            recyclerView.adapter=SectionListSongAdapter(songs)
                            mainLayout.setOnClickListener {
                                SongsActivityList.category=section
                                startActivity(Intent(this@MainActivity,SongsActivityList::class.java))
                            }
                        }
                    }

            }
    }



}