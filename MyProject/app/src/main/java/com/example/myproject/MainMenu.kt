package com.example.myproject

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myproject.ui.theme.GameTheme

class MainMenu : ComponentActivity() {
    private lateinit var player: MediaPlayer
    private lateinit var player1: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val songlist = ArrayList<song>()
        val imageModifier=Modifier
            .size(250.dp)
        init(this@MainMenu)
        setContent {
            MusicPlayerActivity(songlist)
            GameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(painter = painterResource(id=R.drawable.main_menu_pic),
                            contentDescription = "",
//                            contentScale= ContentScale.Fit,
                            modifier=imageModifier)
                        Button(
                            onClick = {
                                player1.start()
                                player.pause()  // Pause instead of stopping
                                val intent = Intent()
                                intent.setClassName(this@MainMenu, "com.example.myproject.MainActivity")
                                this@MainMenu.startActivity(intent)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.SnakeGame))
                        }
                        Button(
                            onClick = {
                                player1.start()
                                player.pause()  // Pause instead of stopping
                                val intent = Intent()
                                intent.setClassName(this@MainMenu, "com.example.myproject.AboutUs")
                                this@MainMenu.startActivity(intent)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.aboutBtn))
                        }
                    }
                }
            }
        }
    }

    private fun MusicPlayerActivity(songlist: ArrayList<song>) {
        songlist.add(song("main_menu"))
        songlist.add(song("game_start"))
        songlist.add(song("button_clicked"))
        songlist.add(song("get_score"))
        songlist.add(song("lose"))
    }

    private fun init(context: Context) {
        try {
            player1 = MediaPlayer.create(context, R.raw.button_clicked).apply {
                setVolume(1.0f, 1.0f)  // Set volume to maximum
                setOnCompletionListener {
                    try {
                        stop()
                        prepare()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            player = MediaPlayer.create(context, R.raw.main_menu).apply {
                isLooping = true  // Set the player to loop the music
                setVolume(0.7f, 0.7f)
                setOnCompletionListener {
                    try {
                        stop()
                        prepare()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!player.isPlaying) {
            player.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (player.isPlaying) {
            player.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        player1.release()
    }
}
