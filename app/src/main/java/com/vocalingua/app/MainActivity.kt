package com.vocalingua.app

import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.statusBarColor = AndroidColor.rgb(13, 23, 41)
        @Suppress("DEPRECATION")
        window.navigationBarColor = AndroidColor.rgb(246, 249, 253)
        setContent {
            VocaTheme {
                AppRoot()
            }
        }
    }
}

private enum class Page { SONG, WORD, PHONEME, PRACTICE, LIBRARY, PROGRESS }

@Composable
private fun AppRoot() {
    var pageName by rememberSaveable { mutableStateOf(Page.SONG.name) }
    var selectedSound by rememberSaveable { mutableStateOf("ɐ̃") }
    var selectedWord by rememberSaveable { mutableStateOf("chão") }
    val page = Page.valueOf(pageName)
    val goBack = { pageName = Page.SONG.name }

    when (page) {
        Page.SONG -> SongScreen(
            onWord = { word -> selectedWord = word; pageName = Page.WORD.name },
            onPhoneme = { selectedSound = "ɐ̃"; pageName = Page.PHONEME.name },
            onPractice = { pageName = Page.PRACTICE.name },
            onLibrary = { pageName = Page.LIBRARY.name },
            onProgress = { pageName = Page.PROGRESS.name }
        )
        Page.WORD -> WordScreen(
            word = selectedWord,
            onBack = goBack,
            onPhoneme = { symbol -> selectedSound = symbol; pageName = Page.PHONEME.name },
            onPractice = { pageName = Page.PRACTICE.name }
        )
        Page.PHONEME -> PhonemeScreen(
            initialSound = selectedSound,
            onBack = { pageName = Page.WORD.name },
            onPractice = { pageName = Page.PRACTICE.name }
        )
        Page.PRACTICE -> PracticeScreen(onBack = goBack)
        Page.LIBRARY -> LibraryScreen(onBack = goBack, onSong = goBack)
        Page.PROGRESS -> ProgressScreen(onBack = goBack, onPractice = { pageName = Page.PRACTICE.name })
    }
}
