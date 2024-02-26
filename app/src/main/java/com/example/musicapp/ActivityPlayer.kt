package com.example.musicapp
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.musicapp.databinding.ActivityPlayer2Binding
import com.example.musicapp.model.SongsModel
import com.example.musicapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


//
//import android.content.ContentValues.TAG
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.annotation.OptIn
//import androidx.appcompat.app.AppCompatActivity
//import androidx.media3.common.Player
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.exoplayer.ExoPlayer
//import com.bumptech.glide.Glide
//import com.example.musicapp.adapter.PurchasedSongAdapter
//import com.example.musicapp.databinding.ActivityPlayer2Binding
//import com.example.musicapp.model.SongsModel
//
//class ActivityPlayer : AppCompatActivity() {
//    private lateinit var binding: ActivityPlayer2Binding
//    private lateinit var exoPlayer: ExoPlayer
//    private lateinit var currentSong: SongsModel
//    private lateinit var purchasedSongAdapter: PurchasedSongAdapter
//    private val userFetcher = UserFetcher()
//    private var playerListener = object:Player.Listener{
//        override fun onIsPlayingChanged(isPlaying: Boolean) {
//            super.onIsPlayingChanged(isPlaying)
//            showGif(isPlaying)
//        }
//        override fun onPlaybackStateChanged(playbackState: Int) {
//            super.onPlaybackStateChanged(playbackState)
//            if (playbackState == Player.STATE_ENDED) {
//
//                stopPlaybackAfterPreview()
//            }
//        }
//    }
//
//    //    private var playbackTimer: Handler = Handler(Looper.getMainLooper())
////    private val playbackDuration: Long = 33 * 1000 // 30 seconds
//    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding= ActivityPlayer2Binding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        MyExoplayer.getCurrentSong()?.apply {
//            binding.songTitleTextview.text = title
//            binding.songSubtitleTextview.text  = subtitle
//            Glide.with(binding.songCover).load(coverUrl)
//                .circleCrop()
//                .into(binding.songCover)
//            Glide.with(binding.songGifImageView).load(R.drawable.media_playing)
//                .circleCrop()
//                .into(binding.songGifImageView)
//            exoPlayer = MyExoplayer.getInstance(this@ActivityPlayer)!!
//            binding.playerView.player=exoPlayer
//            binding.playerView.showController()
//            exoPlayer.addListener(playerListener)
//
////            binding.nextButton.setOnClickListener {
////                MyExoplayer.playNext(this@ActivityPlayer)
////            }
////            binding.previousButton.setOnClickListener {
////                MyExoplayer.playPrevious(this@ActivityPlayer)
////            }
//            //  startPreviewPlayback()
//        }
//        binding.buy.setOnClickListener {
//            Toast.makeText(this@ActivityPlayer, "Initiating purchase process...", Toast.LENGTH_SHORT).show()
//            val currentSong = MyExoplayer.getCurrentSong()
//            if (currentSong != null) {
//                buyNowButtonClicked(currentSong)
//            } else {
//                Toast.makeText(this@ActivityPlayer, "No song is currently playing", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        exoPlayer?.removeListener(playerListener)
//    }
//    //    private fun startPreviewPlayback() {
////
////        exoPlayer.play()
////
////        playbackTimer.postDelayed({
////            stopPlaybackAfterPreview()
////        }, playbackDuration)
////    }
//    private fun stopPlaybackAfterPreview() {
//        exoPlayer.stop()
//        exoPlayer.release()
//    }
//    fun showGif(show:Boolean)
//    {
//        if(show)
//            binding.songGifImageView.visibility= View.VISIBLE
//        else{
//            binding.songGifImageView.visibility=View.INVISIBLE
//        }
//    }
//    private fun buyNowButtonClicked(song: SongsModel) {
//        // Initiate the purchase process
//        purchaseSong(song)
//    }
//
//    private fun purchaseSong(song: SongsModel) {
//
//        onPurchaseSuccess(song)
//    }    private fun onPurchaseSuccess(song: SongsModel) {
//
//        addSongToPurchasedList(song)
//    }
//
//    private fun addSongToPurchasedList(song: SongsModel) {
//
//        Toast.makeText(this, "Song purchased successfully!", Toast.LENGTH_SHORT).show()
//
//
//        deductCreditsFromUser()
//    }
//    private fun deductCreditsFromUser() {
//        val userFetcher=UserFetcher()
//        userFetcher.deductCreditScoreFromUser(
//            requiredCreditScore = 10, // Deduct 10 credits for buying the song
//            onSuccess = { newCreditScore ->
//                // Show a message or handle success as needed
//               Toast.makeText(this, "Song purchased successfully", Toast.LENGTH_SHORT).show()
//
//                // Update the credit score in the profile activity
//                updateCreditScoreInProfileActivity(newCreditScore)
//            },
//            onFailure = { exception ->
//                // Handle failure to deduct credits
//                Log.e(TAG, "Error deducting credits from user", exception)
//                Toast.makeText(this, "Failed to purchase song", Toast.LENGTH_SHORT).show()
//            }
//        )
//    }
//
//
//    private fun updateCreditScoreInProfileActivity(newCreditScore: Long) {
//        // Send the new credit score back to the profile activity
//        val intent = Intent(this, ProfileActivity::class.java)
//        intent.putExtra("NEW_CREDIT_SCORE", newCreditScore)
//        startActivity(intent)
//    }
//
//
//
//}
//2nd  code////-----------------------------------------------------------------------------------
//2nd  code////-----------------------------------------------------------------------------------
//2nd  code////-----------------------------------------------------------------------------------
//2nd  code////-----------------------------------------------------------------------------------
//2nd  code////-----------------------------------------------------------------------------------
//2nd  code////-----------------------------------------------------------------------------------
//2nd  code////-----------------------------------------------------------------------------------

// 3rdd code



class ActivityPlayer : AppCompatActivity(), Runnable {
    private lateinit var binding: ActivityPlayer2Binding
    private lateinit var currentSong: SongsModel
    private val userFetcher = UserFetcher()
    private var currentSongIndex : Int = 0
    private var mediaPlayer : MediaPlayer? = null
    private var songList : ArrayList<String>? = null
    private var handler : Handler? = null
    private var isSongPlaying : Boolean = false


    //    private var playbackTimer: Handler = Handler(Looper.getMainLooper())
//    private val playbackDuration: Long = 33 * 1000 // 30 seconds
    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayer2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent

        songList = intent.getStringArrayListExtra("songsList")
        currentSongIndex = intent.getIntExtra("index", 0)
        mediaPlayer = MediaPlayer()
        handler = Handler(Looper.getMainLooper())

//        MyExoplayer.getCurrentSong()?.apply {
//            currentSong=(MyExoplayer.getCurrentSong()!!)
//            binding.songTitleTextview.text = title
//            binding.songSubtitleTextview.text  = subtitle
//            Glide.with(binding.songCover).load(coverUrl)
//                .circleCrop()
//                .into(binding.songCover)
//            Glide.with(binding.songGifImageView).load(R.drawable.media_playing)
//                .circleCrop()
//                .into(binding.songGifImageView)

        //  startPreviewPlayback()
        binding.buy.setOnClickListener {
            Toast.makeText(
                this@ActivityPlayer,
                "Initiating purchase process...",
                Toast.LENGTH_SHORT
            ).show()
            GlobalScope.launch(Dispatchers.Main) {
                buyNowButtonClicked()
            }
        }

//            binding.download.visibility = if (downloaded) View.GONE else View.VISIBLE
//            binding.download.setOnClickListener {
//                downloadSong(this)
//            }


                    // Simulated download action
//                    val downloadedSong = SongsModel(
//                        id = "unique_id",
//                        title = "Downloaded Song",
//                        subtitle = "Artist Name",
//                        url = "downloaded_song_url",
//                        coverUrl = "cover_image_url",
//                        credits = 10,
//                        downloaded = true, // Set downloaded to true
//
//                    // Add the downloaded song to the offline playlist
//                        addSongToOfflinePlaylist(dow)

        if(songList != null)
        {
            playSong(currentSongIndex)
        }

        binding.previousButton.setOnClickListener {
            if(songList != null)
            {
                if(currentSongIndex != 0)
                {

                    mediaPlayer?.pause()
                    currentSongIndex--
                    playSong(currentSongIndex)
                }
            }
        }
        binding.nextButton.setOnClickListener {
            if(songList != null)
            {
                if(currentSongIndex < songList!!.size-1)
                {
                    // Pause MediaPlayer
                    mediaPlayer?.pause()

                    // Increment currentSongIndex
                    currentSongIndex++

                    // Reset MediaPlayer
                    mediaPlayer?.reset()

                    // Play next song
                    playSong(currentSongIndex)
                }
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }
        })

        binding.play.setOnClickListener {
            if(isSongPlaying)
            {
                binding.play.setBackgroundResource(R.drawable.baseline_play_arrow_24)
                mediaPlayer?.pause()
                binding.seekBar.removeCallbacks(createUpdateSeekBarRunnable())
            }
            else
            {
                binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
                mediaPlayer?.start()
                createUpdateSeekBarRunnable()
            }
            isSongPlaying = !isSongPlaying
        }
    }


//    private fun addSongToOfflinePlaylist(song: SongsModel) {
//        // Assuming OfflinePlaylistActivity is already running or will be started
//        val offlinePlaylistActivityIntent = OfflinePlaylistActivity.newIntent(this, song)
//        startActivity(offlinePlaylistActivityIntent)
//
//        Toast.makeText(this, "Song downloaded and added to playlist", Toast.LENGTH_SHORT).show()
//    }


//    override fun onDestroy() {
//        super.onDestroy()
//        exoPlayer?.removeListener(playerListener)
//    }
    //    private fun startPreviewPlayback() {
//
//        exoPlayer.play()
//
//        playbackTimer.postDelayed({
//            stopPlaybackAfterPreview()
//        }, playbackDuration)
//    }
//    private fun stopPlaybackAfterPreview() {
//        exoPlayer.stop()
//        exoPlayer.release()
//    }
    fun showGif(show:Boolean)
    {
        if(show)
            binding.songGifImageView.visibility= View.VISIBLE
        else{
            binding.songGifImageView.visibility=View.INVISIBLE
        }
        binding.favSong.setOnClickListener {
                toggleFavorite()
        }
    }
//        private fun deductCreditsFromUser() {
//        val userFetcher=UserFetcher()
//        userFetcher.deductCreditScoreFromUser(
//            requiredCreditScore = 10, // Deduct 10 credits for buying the song
//            onSuccess = { newCreditScore ->
//                // Show a message or handle success as needed
//               Toast.makeText(this, "Song purchased successfully", Toast.LENGTH_SHORT).show()
//
//                // Update the credit score in the profile activity
//                updateCreditScoreInProfileActivity(newCreditScore)
//            },
//            onFailure = { exception ->
//                // Handle failure to deduct credits
//                Log.e(TAG, "Error deducting credits from user", exception)
//                Toast.makeText(this, "Failed to purchase song", Toast.LENGTH_SHORT).show()
//            }
//        )
//    }
    private suspend fun buyNowButtonClicked() {
        // Get the current user
        val currentUser = getCurrentUser()

        if (currentUser != null) {
            if (currentSong != null) {
                // Initiate the purchase process
                purchaseSong(currentUser, currentSong)
            } else {
                Toast.makeText(this@ActivityPlayer, "No song selected", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@ActivityPlayer, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getCurrentUser(): UserModel? {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        return firebaseUser?.let {
            UserModel(
                username = it.displayName ?: "",
                profileImage = it.photoUrl?.toString() ?: "",
                creditScore = 500, // Default credit score
                uid = it.uid
            )
        }
    }

    private suspend fun purchaseSong(user: UserModel, song: SongsModel) {
        if (user != null) {
            val userId = user.uid
            val updatedPurchasedSongs = mutableListOf<String>()
            userFetcher.fetchPurchasedSongsForCurrentUser(
                onSuccess = { purchasedSongs ->
                    updatedPurchasedSongs.addAll(purchasedSongs.map { it.id })
                    if (!updatedPurchasedSongs.contains(song.id)) {
                        updatedPurchasedSongs.add(song.id)
                        updateUserPurchasedSongs(userId, song.id)
                    } else {
                        Toast.makeText(this@ActivityPlayer, "Song already purchased", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error fetching purchased songs", exception)
                    Toast.makeText(this@ActivityPlayer, "Failed to purchase song", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(this@ActivityPlayer, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserPurchasedSongs(userId: String, purchasedSongId: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val purchasedSongs = documentSnapshot.toObject(UserModel::class.java)?.purchasedSongs?.toMutableList()
                        ?: mutableListOf()
                    if (!purchasedSongs.contains(purchasedSongId)) {
                        purchasedSongs.add(purchasedSongId)
                        userRef.update("purchasedSongs", purchasedSongs)
                            .addOnSuccessListener {
                                // Successfully updated purchased songs
                                Toast.makeText(this@ActivityPlayer, "Song purchased successfully", Toast.LENGTH_SHORT).show()
                                updateCreditScoreInProfileActivity()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error updating purchased songs", e)
                                Toast.makeText(this@ActivityPlayer, "Failed to purchase song", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Song already purchased
                        Toast.makeText(this@ActivityPlayer, "Song already purchased", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // User document does not exist
                    Toast.makeText(this@ActivityPlayer, "User document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Error fetching user document
                Log.e(TAG, "Error fetching user document", e)
                Toast.makeText(this@ActivityPlayer, "Failed to purchase song", Toast.LENGTH_SHORT).show()
            }
    }
    private fun playSong(currentSongIndex: Int) {
        FirebaseFirestore.getInstance().collection("songs")
            .document(songList!![currentSongIndex]).get()
            .addOnSuccessListener {
                val song=it.toObject(SongsModel::class.java)
                binding.songTitleTextview.text = title
                binding.songSubtitleTextview.text  = song?.title
                Glide.with(binding.songCover).load(song?.coverUrl)
                    .circleCrop()
                    .into(binding.songCover)
                Glide.with(binding.songGifImageView).load(R.drawable.media_playing)
                    .circleCrop()
                    .into(binding.songGifImageView)

                song?.url?.let { url ->
                    mediaPlayer?.apply {
                        reset()
                        setDataSource(url)
                        setOnPreparedListener { mp ->
                            // Start playback when prepared
                            mp.start()
                            val totalLength = mediaPlayer?.duration ?: 0
                            binding.endChronometer.text = formatTime(totalLength)
                            createUpdateSeekBarRunnable().run()
                            isSongPlaying  = true
                            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
                        }
                        setOnErrorListener { mp, what, extra ->
                            // Handle error here (optional)
                            return@setOnErrorListener false
                        }
                        prepareAsync()
                    }
                }
            }
            .addOnFailureListener {

            }



    }

    private fun formatTime(durationInMillis: Int): String {
        val minutes = (durationInMillis / 1000) / 60
        val seconds = (durationInMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun createUpdateSeekBarRunnable(): Runnable {
        return Runnable {
            val duration = mediaPlayer?.duration ?: 0
            val currentPosition = mediaPlayer?.currentPosition ?: 0
            binding.seekBar.max = duration
            binding.seekBar.progress = currentPosition
            binding.startingChronometer.text = formatTime(currentPosition)
            handler?.postDelayed(this, 1000)
        }
    }



    private fun updateCreditScoreInProfileActivity() {
        // Perform any actions needed before navigating to the profile activity
        Toast.makeText(this@ActivityPlayer, "Updating profile...", Toast.LENGTH_SHORT).show()

        // Navigate to the profile activity
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
//    private fun downloadSong(song: SongsModel) {
//        // Check if the song is already downloaded
//        if (song.downloaded) {
//            Toast.makeText(this, "Song is already downloaded", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Implement your download logic here
//        // For example, you can mark the song as downloaded locally
//        // and update its download status in Firestore
//
//        // Update the download status locally
//        song.downloaded = true
//
//        // Update the download status in Firestore
//        updateDownloadStatusInFirestore(song)
//    }
private fun downloadSong(song: SongsModel) {
    val db = FirebaseFirestore.getInstance()
    val songRef = db.collection("songs").document(song.id)
    songRef.update("downloaded", true)
        .addOnSuccessListener {
            Toast.makeText(this@ActivityPlayer, "Song downloaded successfully", Toast.LENGTH_SHORT).show()
            binding.download.visibility = View.GONE
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error updating song download status", e)
            Toast.makeText(this@ActivityPlayer, "Failed to download song", Toast.LENGTH_SHORT).show()
        }
}
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        handler?.removeCallbacksAndMessages(null)
    }

    override fun run() {
        val currentPosition = mediaPlayer?.currentPosition ?: 0
        binding.startingChronometer.text = formatTime(currentPosition)
        binding.seekBar.progress = currentPosition
        handler?.postDelayed(this@ActivityPlayer, 1000) // Update every second
    }
    private fun toggleFavorite() {
        currentSong.isFavorite = !currentSong.isFavorite

        if (currentSong.isFavorite) {
            // Song is marked as favorite
            binding.favSong.setImageResource(R.drawable.baseline_favorite_24)
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
        } else {
            // Song is unmarked as favorite
            binding.favSong.setImageResource(R.drawable.baseline_favorite_border_24)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        }
    }


//    private fun updateDownloadStatusInFirestore(song: SongsModel) {
//        // Update the 'downloaded' field of the song document in Firestore
//        val db = FirebaseFirestore.getInstance()
//        val songRef = db.collection("songs").document(song.id)
//        songRef.update("downloaded", true)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Song downloaded successfully", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "Error updating download status in Firestore", e)
//                Toast.makeText(this, "Failed to download song", Toast.LENGTH_SHORT).show()
//            }
//    }
//    private fun downloadSong(songId: String) {
//        val songRef = FirebaseFirestore.getInstance().collection("songs").document(songId)
//
//        // Check if the song is already downloaded
//        songRef.get().addOnSuccessListener { document ->
//            val downloaded = document.getBoolean("downloaded") ?: false
//
//            if (!downloaded) {
//                // Perform the download operation here
//                // Once downloaded, update the Firestore document to mark the song as downloaded
//                songRef.update("downloaded", true)
//                    .addOnSuccessListener {
//                        // Update UI - Hide the download button
//                        binding.download.visibility = View.GONE
//                        Toast.makeText(this, "Song downloaded successfully", Toast.LENGTH_SHORT).show()
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(this, "Failed to download song", Toast.LENGTH_SHORT).show()
//                        Log.e(TAG, "Error downloading song: $e")
//                    }
//            } else {
//                // Song is already downloaded
//                Toast.makeText(this, "Song is already downloaded", Toast.LENGTH_SHORT).show()
//            }
//        }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "Error fetching song details: $e")
//            }
//    }

}