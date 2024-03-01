package com.example.musicapp
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.musicapp.adapter.SongListAdapter
import com.example.musicapp.databinding.ActivityPlayer2Binding
import com.example.musicapp.model.SongsModel
import com.example.musicapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.util.Timer
import java.util.TimerTask

class ActivityPlayer : AppCompatActivity(), Runnable {
    private lateinit var binding: ActivityPlayer2Binding

    private var currentSongIndex: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private var songList: ArrayList<String>? = null
    private var handler: Handler? = null
    private var isSongPlaying: Boolean = false
    private var from: String? = null
    private lateinit var songListAdapter: SongListAdapter
    private  var isFavorite: Boolean = false
    private  var isSongPurchased:Boolean=false
    private lateinit var userFetcher: UserFetcher
    private lateinit var userModel: UserModel
    private var purchanseSong: ArrayList<SongsModel>? = null
    private var offlineSongs:ArrayList<SongsModel>?=null
    private lateinit var currentSong: SongsModel
    private lateinit var purchasedSongIds: List<String>

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayer2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        userModel = UserModel()
        mediaPlayer = MediaPlayer()
        handler = Handler(Looper.getMainLooper())
        from = intent.getStringExtra("From")
        userFetcher = UserFetcher()
        currentSong= SongsModel()

        if (from.equals("formPurchaseSong")) {
            purchanseSong = intent?.getParcelableArrayListExtra("songsList")
            currentSongIndex = intent.getIntExtra("index", 0)
            playPurchaseSong(currentSongIndex)
        }
        else if(from.equals("Offline")) {
            offlineSongs=intent.getParcelableArrayListExtra<SongsModel>("offline_Song_list")
            currentSongIndex = intent.getIntExtra("index", 0)
            playOfflineSong(currentSongIndex)
        }
        else {
            songList = intent.getStringArrayListExtra("songsList")
            currentSongIndex = intent.getIntExtra("index", 0)
            if(songList!=null)
            {
                playSong(currentSongIndex)
            }
        }
//        currentSong = SongsModel()
//        if (songList != null) {
//            playSong(currentSongIndex)
//        }


        binding.previousButton.setOnClickListener {
            if (songList != null) {
                if (currentSongIndex != 0) {

                    mediaPlayer?.pause()
                    currentSongIndex--
                    playSong(currentSongIndex)
                }
            } else if (purchanseSong != null) {

                if (currentSongIndex != 0) {

                    mediaPlayer?.pause()
                    currentSongIndex--
                    playPurchaseSong(currentSongIndex)
                }
            }
            else
            {
                if(offlineSongs!=null)
                {
                    if(currentSongIndex!=0)
                    {
                        mediaPlayer?.pause()
                        currentSongIndex--
                        playOfflineSong(currentSongIndex)
                    }
                }
            }

        }



        binding.nextButton.setOnClickListener {
            if (songList != null) {
                if (currentSongIndex < songList!!.size - 1) {
                    // Pause MediaPlayer
                    mediaPlayer?.pause()

                    // Increment currentSongIndex
                    currentSongIndex++

                    // Reset MediaPlayer
                    mediaPlayer?.reset()

                    // Play next song
                    playSong(currentSongIndex)

                }
            } else if (purchanseSong != null) {
                if (currentSongIndex < purchanseSong!!.size - 1) {
                    // Pause MediaPlayer
                    mediaPlayer?.pause()

                    // Increment currentSongIndex
                    currentSongIndex++

                    // Reset MediaPlayer
                    mediaPlayer?.reset()

                    // Play next song
                    playPurchaseSong(currentSongIndex)
                }
            }
            else
            {
                if(offlineSongs!=null)
                {
                    if(currentSongIndex<offlineSongs!!.size-1)
                    {
                        mediaPlayer?.pause()
                        currentSongIndex++
                        playOfflineSong(currentSongIndex)
                    }
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
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }
        })


        binding.play.setOnClickListener {
            if (isSongPlaying) {
                binding.play.setBackgroundResource(R.drawable.baseline_play_arrow_24)
                mediaPlayer?.pause()
                binding.seekBar.removeCallbacks(createUpdateSeekBarRunnable())
                showGif(false)
            } else {
                binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
                mediaPlayer?.start()
                createUpdateSeekBarRunnable()
                showGif(true)
            }
            isSongPlaying = !isSongPlaying
        }
        binding.buy.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                purchaseCurrentSong()
            }
        }
        binding.download.setOnClickListener {
            checkIfSongIsPurchased()
        }


    }
    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.Main) {
            fetchPurchasedSongs()
        }
    }

    private suspend fun fetchPurchasedSongs() {
        userFetcher.fetchPurchasedSongsForCurrentUser(
            onSuccess = { purchasedSongs ->
                // Check if the current song ID is in the list of purchased song IDs
                val isSongPurchased = purchasedSongs.any { it.first.id == currentSong.id }

                // Update UI based on whether the song is purchased
                if (isSongPurchased) {
                    hideBuyButton() // Hide buy button if the song is purchased
                    if (isSongDownloaded(currentSong.id!!)) {
                        hideDownloadButton() // Hide download button if the song is downloaded
                    } else {
                        showDownloadButton() // Show download button if the song is purchased but not downloaded
                    }
                } else {
                    showBuyButton() // Show buy button if the song is not purchased
                    hideDownloadButton() // Hide download button if the song is not purchased
                }

                // Show alert if the song is already purchased
                if (isSongPurchased) {
                    showAlreadyPurchasedAlert()
                }
            },
            onFailure = { exception ->
                // Handle failure to fetch purchased songs
                Log.e(TAG, "Failed to fetch purchased songs", exception)
                Toast.makeText(this, "Failed to fetch purchased songs: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showAlreadyPurchasedAlert() {
        AlertDialog.Builder(this)
            .setTitle("Already Purchased")
            .setMessage("You have already purchased this song.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun isSongDownloaded(songId: String): Boolean {
        // Get the directory where downloaded songs are stored
        val storageDir = getExternalFilesDir(null)?.absolutePath + "/Downloads"

        // Create a File object for the song file
        val songFile = File("$storageDir/$songId.mp3")

        // Check if the song file exists
        return songFile.exists()
    }
    private fun hideBuyButton() {
        binding.buy.visibility = View.GONE
    }

    private fun hideDownloadButton() {
        binding.download.visibility = View.GONE
    }

    private fun checkSongPurchaseAndDownload(song: SongsModel) {
        song.id?.let {
            userFetcher.isSongPurchased(
                it,
                onSuccess = { isPurchased ->
                    if (isPurchased) {
                        // Song is purchased, allow download
                        checkDownload(song)
                    } else {
                        // Song is not purchased, show a message
                        showToast("To download this song, please purchase it first.")
                    }
                },
                onFailure = { exception ->
                    // Handle failure to check song purchase status
                    showToast("Error checking song purchase status: ${exception.message}")
                }
            )
        }
    }
    private fun checkDownload(song: SongsModel)
    {
        Toast.makeText(this, "Download Successfull", Toast.LENGTH_SHORT).show()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request write permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE
            )
        } else {
            // Permission granted, start download
            downloadSong(userModel.uid)
        }
    }
    private fun showPurchaseDialog() {
        // Show pop-up dialog indicating that download is only available after purchase
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Download Unavailable")
        builder.setMessage("This song is only available for download after purchase.")
        builder.setPositiveButton("OK") { dialog, which ->
            // Handle the OK button click if needed
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun isSongPurchased(songId: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        // Check if the song is purchased for the current user
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val purchasedSongs = documentSnapshot.get("purchasedSongs") as? List<String> ?: emptyList()
                        val isPurchased = purchasedSongs.contains(songId)
                        onSuccess(isPurchased)
                    } else {
                        onFailure(IllegalStateException("User document does not exist"))
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            onFailure(IllegalStateException("User not authenticated"))
        }
    }

    private fun playSongForLimitedTime(songUrl: String) {
        mediaPlayer?.apply {
            reset()
            setDataSource(songUrl)
            setOnPreparedListener { mp ->
                mp.start()
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        mp.stop()
                        mp.release()
                        timer.cancel() // Cancel the timer
                        showToast("You need to purchase the song to listen to the whole track.")
                    }
                }, 30 * 1000) // 30 seconds in milliseconds

                // Disable seek bar interaction
                binding.seekBar.isEnabled = false

                isSongPlaying = true
                binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            }
            setOnErrorListener { mp, what, extra ->
                // Handle error here (optional)
                return@setOnErrorListener false
            }
            prepareAsync()
        }
    }



    private fun addCurrentSongToFavorites() {
        val userId = userModel.uid
        val songId = currentSong.id
        if (userId != null && songId != null) {
            userFetcher.addSongToFavorites(userId, songId,
                onSuccess = {
                    // Handle success, if needed
                    // For example, show a toast indicating success
                    showToast("Song added to favorites")
                },
                onFailure = { exception ->
                    // Handle failure
                    // For example, show a toast with the error message
                    showToast("Failed to add song to favorites: ${exception.message}")
                }
            )
        }
    }


    private fun downloadSong(userId: String) {
        // Replace the following with actual code to download the song
        val songUrl = "https://example.com/song.mp3"
        val fileName = "song_$userId.mp3" // Append userId to the filename
        val storageDir = File(getExternalFilesDir(null), "Downloads")
        storageDir.mkdirs()

        val file = File(storageDir, fileName)
        if (!file.exists()) {
            val url = URL(songUrl)
            val connection = url.openConnection()
            connection.connect()
            val input: InputStream = connection.getInputStream()
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            output.close()
            input.close()
            Toast.makeText(this, "Song downloaded", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Song already downloaded", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to remove the song from favorites
    private fun removeFromFavorites() {
        currentSong.id?.let { songId ->
            userFetcher?.removeSongFromFavorites(songId,
                onSuccess = {
                    showToast("Song removed from favorites")
                    // Update the drawable resource to unfilled when song is removed from favorites
                    //        binding.favSong.setImageResource(R.drawable.baseline_favorite_border_24)
                    // Update the isFavorite flag
                    isFavorite = false
                },
                onFailure = { exception ->
                    showToast("Failed to remove song from favorites: ${exception.message}")
                })
        }
    }

    private fun playPurchaseSong(currentSongIndex: Int) {
        val song = purchanseSong?.get(currentSongIndex)
        binding.songTitleTextview.text = song?.title
        binding.songSubtitleTextview.text = song?.subtitle
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
                    isSongPlaying = true
                    binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
                }
                prepareAsync()
            }
        }

    }
    private suspend fun purchaseCurrentSong() {
        // Ensure that the current song is properly initialized
        val song = if (from == "formPurchaseSong") {
            purchanseSong?.get(currentSongIndex)
        } else {
            // Retrieve the song from the songList if not from purchase
            FirebaseFirestore.getInstance().collection("songs")
                .document(songList!![currentSongIndex]).get()
                .addOnSuccessListener { document ->
                    val song = document.toObject(SongsModel::class.java)
                    song?.let {
                        currentSong = it
                        // Call isSongPurchased to check if the song is already purchased
                        isSongPurchased(
                            song.id!!,
                            onSuccess = { isPurchased ->
                                if (isPurchased) {
                                    // Song is already purchased, show alert message
                                    showAlreadyPurchasedAlert()
                                } else {
                                    // Song is not purchased, proceed with purchasing
                                    userFetcher.purchaseSong(
                                        currentSong,
                                        onSuccess = {
                                            showToast("Song purchased successfully")
                                            // Hide the buy button after successful purchase
                                            hideBuyButton()
                                        },
                                        onFailure = { exception ->
                                            showToast("Failed to purchase song: ${exception.message}")
                                        }
                                    )
                                }
                            },
                            onFailure = { exception ->
                                showToast("Failed to check song purchase status: ${exception.message}")
                            }
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Failed to retrieve song: ${exception.message}")
                }
            return
        }

        // Call isSongPurchased to check if the song is already purchased
        song?.id?.let { songId ->
            isSongPurchased(
                songId,
                onSuccess = { isPurchased ->
                    if (isPurchased) {
                        // Song is already purchased, show alert message
                        showAlreadyPurchasedAlert()
                    } else {
                        // Song is not purchased, proceed with purchasing
                        userFetcher.purchaseSong(
                            song,
                            onSuccess = {
                                showToast("Song purchased successfully")
                                // Hide the buy button after successful purchase
                                hideBuyButton()
                            },
                            onFailure = { exception ->
                                showToast("Failed to purchase song: ${exception.message}")
                            }
                        )
                    }
                },
                onFailure = { exception ->
                    showToast("Failed to check song purchase status: ${exception.message}")
                }
            )
        }
    }

    private fun checkIfSongIsPurchased() {
        // Ensure currentSong is properly initialized
        val currentSong = SongsModel() // Initialize with appropriate values

        // Check if currentSong's ID is not null
        currentSong.id?.let { songId ->
            // Call isSongPurchased with currentSong object
            isSongPurchased(currentSong.id,
                onSuccess = { isPurchased ->
                    if (isPurchased) {
                        // Song is purchased, allow download and play
                        enableDownloadButton()
                    } else {
                        // Song is not purchased, show pop-up dialog
                        showPurchaseDialog()
                    }
                },
                onFailure = { exception ->
                    // Handle failure to check song purchase status
                    Log.e(TAG, "Error checking song purchase status", exception)
                    // Display an error message
                    showToast("Failed to check song purchase status")
                }
            )
        }
    }




    private fun disableBuyButton() {
        binding.buy.visibility = View.GONE
    }

    private fun showBuyButton() {
        binding.buy.visibility = View.VISIBLE
    }

    private fun showDownloadButton() {
        binding.buy.visibility = View.GONE
        binding.download.visibility = View.VISIBLE
    }

    private fun enableDownloadButton() {
        // Enable the download button
        binding.download.isEnabled = true
        // Optionally, you can update the appearance of the download button
        // For example, change the alpha value to make it fully visible
        binding.download.alpha = 1.0f
    }

    private fun updateProfileCredit() {
        val userFetcher = UserFetcher()

        // Fetch the remaining credit score and update the profile activity
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val remainingCredit = userFetcher.fetchRemainingCredit()
                // Pass the remaining credit value to ProfileActivity
                val intent = Intent(this@ActivityPlayer, ProfileActivity::class.java)
                intent.putExtra("remainingCredit", remainingCredit)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating profile credit", e)
                // Handle error, for example, display a toast message
                showToast("Error updating profile credit: ${e.message}")
            }
        }
    }
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@ActivityPlayer, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun showGif(show:Boolean) {
        // Show the GIF ImageView
        if(show)
            binding.songGifImageView.visibility = View.VISIBLE
        else{
            binding.songGifImageView.visibility=View.GONE
        }
    }
    private fun playOfflineSong(currentSongIndex: Int) {
        val song = offlineSongs?.get(currentSongIndex)
        binding.songTitleTextview.text = song?.title
        binding.songSubtitleTextview.text = song?.subtitle
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
                    isSongPlaying = true
                    binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
                }
                prepareAsync()
            }
        }
    }




    private fun playSong(currentSongIndex: Int) {
        FirebaseFirestore.getInstance().collection("songs")
            .document(songList!![currentSongIndex]).get()
            .addOnSuccessListener { documentSnapshot ->
                // Check if the document exists and contains data
                if (documentSnapshot.exists() && documentSnapshot.data != null) {
                    val song = documentSnapshot.toObject(SongsModel::class.java)
                    if (song != null) {
                        // Update UI with song details
                        binding.songTitleTextview.text = song.title
                        binding.songSubtitleTextview.text = song.subtitle
                        Glide.with(binding.songCover).load(song.coverUrl)
                            .circleCrop()
                            .into(binding.songCover)
                        Glide.with(binding.songGifImageView).load(R.drawable.media_playing)
                            .circleCrop()
                            .into(binding.songGifImageView)

                        // Load song from URL
                        song.url?.let { url ->
                            isSongPurchased(song.id!!,
                                onSuccess = { isPurchased ->
                                    if (isPurchased) {
                                        // Song is purchased, play the full song
                                        playFullSong(url)
                                    } else {
                                        // Song is not purchased, play it for a limited time
                                        playSongForLimitedTime(url)
                                    }
                                },
                                onFailure = { exception ->
                                    // Handle failure to check song purchase status
                                    Log.e(TAG, "Error checking song purchase status", exception)
                                    showToast("Failed to check song purchase status")
                                }
                            )
                        }
                    } else {
                        // Handle case where the document exists but cannot be converted to SongsModel
                    }
                } else {
                    // Handle case where the document doesn't exist or doesn't contain data
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to retrieve song data
                Log.e(TAG, "Error fetching song data", exception)
                showToast("Failed to fetch song data: ${exception.message}")
            }
    }



    private fun playFullSong(songUrl: String) {
        mediaPlayer?.apply {
            reset()
            setDataSource(songUrl)
            setOnPreparedListener { mp ->
                mp.start()
                val totalLength = mediaPlayer?.duration ?: 0
                binding.endChronometer.text = formatTime(totalLength)
                createUpdateSeekBarRunnable().run()
                isSongPlaying = true
                binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            }
            setOnErrorListener { mp, what, extra ->

                return@setOnErrorListener false
            }
            prepareAsync()
        }
    }
    private fun updateSeekBarAndTime(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.let { player ->
            val totalDuration = player.duration
            binding.seekBar.max = totalDuration

            val timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (player.isPlaying) {
                        val currentPosition = player.currentPosition
                        runOnUiThread {
                            binding.seekBar.progress = currentPosition
                            binding.startingChronometer.text = formatTime(currentPosition)
                            binding.endChronometer.text = formatTime(totalDuration - currentPosition)
                        }
                    }
                }
            }, 0, 1000)

            player.setOnCompletionListener {
                timer.cancel()
            }
        }
    }


    // Function to format time in MM:SS format
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
    private fun hideGif() {
        // Hide the GIF ImageView
        binding.songGifImageView.visibility = View.GONE
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
        handler?.postDelayed(this, 1000) // Update every second
    }

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

}