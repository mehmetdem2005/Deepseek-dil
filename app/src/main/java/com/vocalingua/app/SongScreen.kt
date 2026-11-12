package com.vocalingua.app

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private enum class SongEdition(val title:String,val videoId:String,val duration:Float) {
    NORMAL("Normal","Am8Yi6tjnVE",90f),
    SPED_UP("Sped Up","ehBGpIlEj3k",84f)
}

@Composable
fun SongScreen(
    onWord: (String) -> Unit,
    onPhoneme: () -> Unit,
    onPractice: () -> Unit,
    onLibrary: () -> Unit,
    onProgress: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("vocalingua_lyric_sync", android.content.Context.MODE_PRIVATE) }
    val player = remember { YouTubeController() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var edition by remember { mutableStateOf(SongEdition.SPED_UP) }
    var expanded by remember { mutableIntStateOf(-1) }
    var favorite by remember { mutableStateOf(false) }
    var rate by remember { mutableFloatStateOf(1f) }
    var syncRevision by remember { mutableIntStateOf(0) }
    var editTiming by remember { mutableStateOf(false) }
    val rateOptions = listOf(.75f, 1f, 1.25f)

    // The preset times are explicitly approximate. Tap "İşaretle" while the video plays
    // to replace any preset with a user-calibrated real playback timestamp.
    val cues = remember(edition, syncRevision) {
        fullDemoCues.mapIndexed { i,cue ->
            val key = "cue_${edition.videoId}_$i"
            val estimate = cue.timeSeconds * (edition.duration / 90f)
            cue.copy(timeSeconds = prefs.getFloat(key, estimate))
        }
    }
    val playbackSeconds = player.playbackSeconds
    val activeCue = if (player.ready) cues.indexOfLast { it.timeSeconds <= playbackSeconds }.coerceAtLeast(0) else 0

    LaunchedEffect(activeCue, player.isPlaying) {
        if (player.isPlaying) listState.animateScrollToItem(activeCue.coerceIn(0, cues.lastIndex))
    }
    LaunchedEffect(rate, player.ready, edition) {
        if (player.ready) player.setSpeed(rate)
    }

    Column(Modifier.fillMaxSize().background(OffWhite)) {
        Column(Modifier.fillMaxWidth().background(Navy).padding(horizontal = 13.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, null, tint = Cyan, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(6.dp))
                Text("VocaLingua", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                    color = Color.White, modifier = Modifier.weight(1f))
                Surface(color = Color(0xFF24324B), shape = RoundedCornerShape(24.dp)) {
                    Text("♛ Pro", Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD670))
                }
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Default.Person, null, tint = Color.White)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HomeTab("Ders", Icons.Default.PlayCircle, true) {}
                HomeTab("Kütüphane", Icons.Default.LibraryMusic, false, onLibrary)
                HomeTab("IPA", Icons.Default.Translate, false, onPhoneme)
                HomeTab("Mikrofon", Icons.Default.Mic, false, onPractice)
                HomeTab("İlerleme", Icons.Default.TrendingUp, false, onProgress)
            }
        }

        // Keep the YouTube video visible while the lyrics underneath scroll.
        Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 12.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)) {
            YouTubeEmbed(
                controller = player, videoId = edition.videoId,
                modifier = Modifier.fillMaxWidth().aspectRatio(16f/9f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("SEM CONTROLE", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                    Text("nightfxrce · Brezilya Portekizcesi · YouTube",
                        fontSize = 10.sp, color = Muted)
                }
                IconButton(onClick = { favorite = !favorite }, modifier = Modifier.size(35.dp)) {
                    Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Favori", tint = if (favorite) Color(0xFFE35463) else Ink)
                }
                IconButton(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${edition.videoId}")))
                }, modifier = Modifier.size(35.dp)) {
                    Icon(Icons.Default.OpenInNew, "YouTube'da aç", tint = Ink)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Pill("Normal", selected = edition == SongEdition.NORMAL, modifier = Modifier.weight(1f),
                    onClick = {
                        if (edition != SongEdition.NORMAL) {
                            edition = SongEdition.NORMAL
                            player.switchVideo()
                            expanded = -1
                        }
                    })
                Pill("Sped Up", selected = edition == SongEdition.SPED_UP, modifier = Modifier.weight(1f),
                    onClick = {
                        if (edition != SongEdition.SPED_UP) {
                            edition = SongEdition.SPED_UP
                            player.switchVideo()
                            expanded = -1
                        }
                    })
                Pill("${rate}×", selected = false, onClick = {
                    val next = rateOptions.indexOf(rate).let { if (it < 0) 1 else (it + 1) % rateOptions.size }
                    rate = rateOptions[next]
                    player.setSpeed(rate)
                })
                Pill(if (editTiming) "✓ İşaretle" else "⏱ Eşleştir",
                    selected = editTiming, onClick = { editTiming = !editTiming })
            }
            if (player.playerError != 0) {
                Text("YouTube oynatımı engellendi (hata ${player.playerError}). Sağ üstteki bağlantıdan videoyu açabilirsin.",
                    fontSize = 11.sp, color = Color(0xFFBA4A3A))
            }
        }

        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Cümle cümle şarkı sözleri", fontSize = 15.sp, color = Ink, fontWeight = FontWeight.ExtraBold)
                Text("Çalan satır mavi olur · detayı yalnızca dokununca aç",
                    fontSize = 11.sp, color = Muted)
            }
            if (player.ready) {
                Surface(color = Sky, shape = RoundedCornerShape(10.dp)) {
                    Text("${toClock(playbackSeconds)}", Modifier.padding(9.dp),
                        fontSize = 11.sp, color = DeepBlue, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Surface(color = Color(0xFFE7F3FF), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        if (editTiming)
                            "Düzeltme açık: şarkı çalarken istediğin cümlede ‘Şimdi işaretle’ düğmesine bas. Zaman cihazına kaydedilecek."
                        else
                            "Demo cümle zamanları yaklaşık. YouTube sesi analiz edilmedi; ‘Eşleştir’ ile zamanları düzeltebilirsin.",
                        Modifier.padding(10.dp), color = DeepBlue, fontSize = 11.sp, lineHeight = 16.sp
                    )
                }
            }
            itemsIndexed(cues, key = { index, _ -> index }) { index, cue ->
                SongSentenceCard(
                    index = index,
                    cue = cue,
                    isActive = index == activeCue && player.ready,
                    expanded = index == expanded,
                    editTiming = editTiming,
                    calibrated = prefs.contains("cue_${edition.videoId}_$index"),
                    onToggle = { expanded = if (expanded == index) -1 else index },
                    onPlay = {
                        player.seekTo(cue.timeSeconds)
                        if (expanded != index) expanded = index
                    },
                    onWord = onWord,
                    onCalibrate = {
                        prefs.edit().putFloat("cue_${edition.videoId}_$index", playbackSeconds).apply()
                        syncRevision++
                    }
                )
            }
            item {
                Surface(color = TealSoft, shape = RoundedCornerShape(13.dp),
                    modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, null, tint = Color(0xFF148D76))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Şimdi sen söyle", fontWeight = FontWeight.Bold, color = Ink, fontSize = 13.sp)
                            Text("Mikrofonla kendi sesinin perdesini ölç.", fontSize = 11.sp, color = Muted)
                        }
                        IconButton(onClick = onPractice) { Icon(Icons.Default.ChevronRight, null, tint = Accent) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SongSentenceCard(
    index:Int, cue:SongCue, isActive:Boolean, expanded:Boolean,
    editTiming:Boolean, calibrated:Boolean,
    onToggle:()->Unit, onPlay:()->Unit, onWord:(String)->Unit, onCalibrate:()->Unit
) {
    val color = if (isActive) Color(0xFFECF6FF) else Color.White
    Surface(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        color = color,
        shape = RoundedCornerShape(17.dp),
        border = BorderStroke(1.dp, if (isActive) Color(0xFF78B9F0) else Line),
        shadowElevation = if (isActive) 2.dp else 0.dp
    ) {
        Column(Modifier.padding(11.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPlay,
                    modifier = Modifier.size(35.dp).background(if(isActive) Color(0xFFD1EBFF) else Sky, CircleShape)
                ) {
                    Icon(Icons.Default.PlayArrow, "Cümleyi oynat", tint = DeepBlue, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f).clickable(onClick = onToggle)) {
                    Text(cue.text, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = Ink, lineHeight = 18.sp, maxLines = if (expanded) 6 else 2,
                        overflow = TextOverflow.Ellipsis)
                    Text(if (isActive) "Şimdi çalan cümle" else if (calibrated) "Zamanı işaretlendi" else "Detaylar için dokun",
                        fontSize = 10.sp, color = if(isActive) Accent else Muted)
                }
                IconButton(onClick = onToggle, modifier = Modifier.size(34.dp)) {
                    Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        "Detayları ${if(expanded)"kapat" else "aç"}", tint = Ink)
                }
            }
            if (expanded) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = Line)
                Spacer(Modifier.height(9.dp))
                Text("IPA · Brezilya Portekizcesi", fontSize = 10.sp, color = Muted)
                Text(if (cue.ipa.isBlank()) "Bu bölümün IPA'sı kayıttan henüz doğrulanmadı." else cue.ipa,
                    color = Accent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(8.dp))
                Text("Anlam", fontSize = 10.sp, color = Muted)
                Text(cue.translation, color = Ink, fontSize = 12.sp)
                Spacer(Modifier.height(11.dp))
                Text("Bir kelime seç, ayrıntısına gir", fontSize = 10.sp, color = Muted)
                Spacer(Modifier.height(5.dp))
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    cue.text.split(Regex("\\s+")).filter { it.isNotBlank() }.forEach { token ->
                        Pill(token.trim(',', '.', '?', '!', '—'), onClick = { onWord(token.trim(',', '.', '?', '!', '—')) })
                    }
                }
                if (editTiming) {
                    Spacer(Modifier.height(10.dp))
                    AccentButton(
                        "Şimdi işaretle (${toClock(cue.timeSeconds)})",
                        Icons.Default.AccessTime,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onCalibrate
                    )
                }
            }
        }
    }
}

private fun toClock(seconds:Float):String {
    val n=seconds.toInt().coerceAtLeast(0)
    return "${n/60}:${(n%60).toString().padStart(2,'0')}"
}

@Composable
private fun HomeTab(label:String, icon:androidx.compose.ui.graphics.vector.ImageVector, current:Boolean, onClick:()->Unit) {
    Column(Modifier.clip(RoundedCornerShape(11.dp))
        .background(if(current) Color(0xFFC8E3FF) else Color.Transparent)
        .clickable(onClick=onClick).padding(horizontal=8.dp,vertical=7.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon,null,tint=if(current) DeepBlue else Color.White,modifier=Modifier.size(19.dp))
        Text(label,fontSize=10.sp,color=if(current) DeepBlue else Color.White)
    }
}
