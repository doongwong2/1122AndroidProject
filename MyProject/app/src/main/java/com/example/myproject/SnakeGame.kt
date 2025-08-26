package com.example.myproject

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.myproject.ui.theme.*
import com.example.myproject.ui.theme.GameTheme
import com.example.myproject.ui.theme.Shapes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(this@MainActivity)
        val game = Game(lifecycleScope)
        player.start()
        setContent {
            GameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        showScore()
                        Snake(game)
                        Button(
                            onClick = {
                                player1.start()
                                player.stop()
                                recreate()
                            }) {
                            Text(text = stringResource(id = R.string.restart))
                        }
                        Button(
                            onClick = {
                                player1.start()
                                player.stop()
                                finish()
                            }) {
                            Text(text = stringResource(id = R.string.returnBtn))
                        }
                    }
                }
            }
        }
    }

    lateinit var player: MediaPlayer

    private fun init(context: Context) {
        try {
            player1 = MediaPlayer.create(context, R.raw.button_clicked).apply {
                setVolume(5.0f, 5.0f)
                setOnCompletionListener {
                    try {
                        stop()
                        prepare()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            player = MediaPlayer.create(context, R.raw.game_start).apply {
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
            player2 = MediaPlayer.create(context, R.raw.get_score).apply {
                setOnCompletionListener {
                    try {
                        stop()
                        prepare()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            player3 = MediaPlayer.create(context, R.raw.lose).apply {
                setOnCompletionListener {
                    try {
                        stop()
                        prepare()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            player4 = MediaPlayer.create(context, R.raw.reach_highest_score).apply {
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

lateinit var player1: MediaPlayer
lateinit var player2: MediaPlayer
lateinit var player3: MediaPlayer
lateinit var player4: MediaPlayer

data class State(val food: Pair<Int, Int>, val snake: List<Pair<Int, Int>>, val isGameOver: Boolean = false)

@Composable
fun showScore() {
    var scoreRecord by remember { mutableStateOf(0) }
}

class Game(private val scope: CoroutineScope) {
    private val mutex = Mutex()
    private val mutableState = MutableStateFlow(State(food = Pair(5, 5), snake = listOf(Pair(7, 7))))
    private var score = 0
    private var Bscore = 0
    private var highest_score_sound_effect = 0
    val state: Flow<State> = mutableState
    private var currentDirection = Pair(1, 0) // Default direction is to the right
    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    // Prevent moving in the opposite direction
                    if (value.first != -currentDirection.first && value.second != -currentDirection.second) {
                        field = value
                        currentDirection = value
                    }
                }
            }
        }
    var isGameOver = false
        private set

    init {
        scope.launch {
            var snakeLength = 3

            while (true) {
                delay(150)
                if (isGameOver) continue
                mutableState.update {
                    val newPosition = it.snake.first().let { poz ->
                        mutex.withLock {
                            Pair(
                                (poz.first + currentDirection.first + BOARD_SIZE) % BOARD_SIZE,
                                (poz.second + currentDirection.second + BOARD_SIZE) % BOARD_SIZE
                            )
                        }
                    }
                    if (newPosition == it.food) {
                        snakeLength++
                        player2.start()
                        score += 10
                        if (score > Bscore) {
                            if (highest_score_sound_effect == 0) {
                                player4.start()
                                highest_score_sound_effect = 1
                            }
                            Bscore = score
                        }
                    }

                    if (it.snake.contains(newPosition)) {
                        player3.start()
                        highest_score_sound_effect = 0
                        snakeLength = 3
                        score = 0
                        isGameOver = true
                    }

                    it.copy(
                        food = if (newPosition == it.food) generateNewFood(it.snake) else it.food,
                        snake = listOf(newPosition) + it.snake.take(snakeLength - 1),
                        isGameOver = isGameOver
                    )
                }
            }
        }
    }

    private fun generateNewFood(snake: List<Pair<Int, Int>>): Pair<Int, Int> {
        var newFood: Pair<Int, Int>
        do {
            newFood = Pair(Random().nextInt(BOARD_SIZE), Random().nextInt(BOARD_SIZE))
        } while (snake.contains(newFood))
        return newFood
    }

    fun restart() {
        isGameOver = false
        score = 0
        move = Pair(1, 0) // Reset direction to default (right)
        currentDirection = Pair(1, 0) // Reset current direction to default (right)
        mutableState.value = State(food = Pair(5, 5), snake = listOf(Pair(7, 7)))
    }

    fun getBestScore(): Int {
        return Bscore
    }

    fun getScore(): Int {
        return score
    }

    companion object {
        const val BOARD_SIZE = 16
    }
}

@Composable
fun Snake(game: Game) {
    val state = game.state.collectAsState(initial = null)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(id = R.string.highScore) +" :${game.getBestScore()}")
        Text(stringResource(id = R.string.Score) +" :${game.getScore()}")
        state.value?.let {
            if (it.isGameOver) {
                Text(
                    text = stringResource(id = R.string.GameOver),
                    color = Color.Red,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Board(it)
                Buttons {
                    game.move = it
                }
            }
        }
    }
}

@Composable
fun Buttons(onDirectionChange: (Pair<Int, Int>) -> Unit) {
    val buttonSize = Modifier.size(64.dp)
    var nowDirection by remember { mutableStateOf(-1) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {

        Button(onClick = {
            if (nowDirection != 0) {
                player1.start()
                onDirectionChange(Pair(0, -1))
                nowDirection = 1
            } else {
                player3.start()
            }
        }, modifier = buttonSize) {
            Icon(Icons.Default.KeyboardArrowUp, null)
        }
        Row {
            Button(onClick = {
                if (nowDirection != 2) {
                    player1.start()
                    onDirectionChange(Pair(-1, 0))
                    nowDirection = 3
                } else {
                    player3.start()
                }
            }, modifier = buttonSize) {
                Icon(Icons.Default.KeyboardArrowLeft, null)
            }
            Spacer(modifier = buttonSize)
            Button(onClick = {
                if (nowDirection != 3) {
                    player1.start()
                    onDirectionChange(Pair(1, 0))
                    nowDirection = 2
                } else {
                    player3.start()
                }
            }, modifier = buttonSize) {
                Icon(Icons.Default.KeyboardArrowRight, null)
            }
        }
        Button(onClick = {
            if (nowDirection != 1) {
                player1.start()
                onDirectionChange(Pair(0, 1))
                nowDirection = 0
            } else {
                player3.start()
            }
        }, modifier = buttonSize) {
            Icon(Icons.Default.KeyboardArrowDown, null)
        }
    }
}

@Composable
fun Board(state: State) {
    BoxWithConstraints(Modifier.padding(16.dp)) {
        val tileSize = maxWidth / Game.BOARD_SIZE

        Box(
            Modifier
                .size(maxWidth)
                .border(2.dp, DarkBlue)
        )

        Box(
            Modifier
                .offset(x = tileSize * state.food.first, y = tileSize * state.food.second)
                .size(tileSize)
                .border(5.dp, DarkBlue, CircleShape)
                .background(
                    LightBlue, CircleShape
                )
        )

        state.snake.forEach {
            Box(
                modifier = Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(
                        DarkBlue, Shapes.small
                    )
            )
        }
    }
}