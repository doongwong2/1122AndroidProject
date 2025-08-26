package com.example.myproject

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myproject.ui.theme.GameTheme

class AboutUs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(this@AboutUs)
        player.start()
        setContent {
            GameTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    val scrollState = rememberScrollState()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.verticalScroll(scrollState))
                    {
                        Text(text = stringResource(id = R.string.lorem_ipsum),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.lorem_ipsum1),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.lorem_ipsum2),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.lorem_ipsum3),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.lorem_ipsum4),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.lorem_ipsum5),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.technical_explanation),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.technical_explanation1),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.technical_explanation2),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.technical_explanation3),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.technical_explanation4),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.technical_explanation5),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.technical_explanation6),fontSize = 20.sp)
                        Text(text = stringResource(id = R.string.lorem_ipsum6),fontSize = 20.sp)
                        Button(
                            onClick = {
                                player2.start()
                                player.stop()
                                finish()
                            })
                        {
                            Text(text = stringResource(id = R.string.returnBtn))
                        }
                    }

                }

            }
        }
    }
    lateinit var player: MediaPlayer
    lateinit var player2: MediaPlayer
    private fun init(context: Context) {
        try {
            player2 = MediaPlayer.create(context, R.raw.button_clicked).apply {
                setOnCompletionListener {
                    try {
                        stop()
                        prepare()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            player = MediaPlayer.create(context, R.raw.about_us).apply {
                isLooping = true  // Set the player to loop the music
                setVolume(1f, 1f)
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
}