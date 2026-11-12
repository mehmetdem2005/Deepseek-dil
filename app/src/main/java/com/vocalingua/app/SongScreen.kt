package com.vocalingua.app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale

@Composable
fun SongScreen(
    onWord: () -> Unit,
    onPhoneme: () -> Unit,
    onPractice: () -> Unit,
    onLibrary: () -> Unit,
    onProgress: () -> Unit
) {
    var selectedPhrase by remember { mutableIntStateOf(0) }
    var showIpa by remember { mutableStateOf(true) }
    var showTranslation by remember { mutableStateOf(false) }
    var favorite by remember { mutableStateOf(false) }
    var speedIndex by remember { mutableIntStateOf(1) }
    var showRhythm by remember { mutableStateOf(true) }
    val speeds = listOf(.75f, 1f, 1.25f)
    val context = LocalContext.current
    val player = remember { YouTubeController() }

    Column(Modifier.fillMaxSize().background(OffWhite)) {
        Column(
            Modifier.fillMaxWidth().background(Navy).padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, null, tint = Cyan, modifier = Modifier.size(27.dp))
                Spacer(Modifier.width(7.dp))
                Text("VocaLingua", color = Color.White, fontWeight = FontWeight.ExtraBold,
                    fontSize = 21.sp, modifier = Modifier.weight(1f))
                Surface(color = Color(0xFF24324B), shape = RoundedCornerShape(30.dp), border = BorderStroke(1.dp, Color(0xFF84713C))) {
                    Text("♛  Pro", Modifier.padding(horizontal = 13.dp, vertical = 7.dp), color = Color(0xFFFBD56D),
                        fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(9.dp))
                Surface(shape = CircleShape, color = Color(0xFF33425D)) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.padding(8.dp).size(18.dp))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TopTab("Ana Sayfa", Icons.Default.Home, false) { onLibrary() }
                TopTab("Ders", Icons.Default.PlayCircle, true) {}
                TopTab("Sözlük", Icons.Default.MenuBook, false) { onWord() }
                TopTab("IPA", Icons.Default.Translate, false) { onPhoneme() }
                TopTab("İlerleme", Icons.Default.TrendingUp, false) { onProgress() }
            }
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Box {
                YouTubeEmbed(player, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("SEM CONTROLE", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("pt-BR · IPA & Vocal Lab · YouTube", color = Muted, fontSize = 12.sp)
                }
                IconButton(onClick = { favorite = !favorite }) {
                    Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Kaydet", tint = if (favorite) Color(0xFFE44D56) else Ink)
                }
                IconButton(onClick = {
                    val url = Uri.parse("https://youtu.be/Am8Yi6tjnVE")
                    context.startActivity(Intent(Intent.ACTION_VIEW, url))
                }) {
                    Icon(Icons.Default.OpenInNew, contentDescription = "YouTube'da aç", tint = Ink)
                }
            }
            Surface(color = Color(0xFFE8F5FF), shape = RoundedCornerShape(10.dp)) {
                Text("Ders önizlemesi · cümle zamanları ve perde grafiği örnektir; YouTube sesinden çıkarılmadı.",
                    Modifier.padding(10.dp), fontSize = 10.sp, color = DeepBlue, lineHeight = 15.sp)
            }
            samplePhrases.forEachIndexed { index, phrase ->
                PhraseCard(
                    phrase = phrase,
                    selected = index == selectedPhrase,
                    ipaVisible = showIpa,
                    translationVisible = showTranslation,
                    onClick = { selectedPhrase = index },
                    onWord = onWord
                )
            }
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Pill("${speeds[speedIndex]}×", selected = true, onClick = {
                    speedIndex = (speedIndex + 1) % speeds.size
                    player.setSpeed(speeds[speedIndex])
                })
                Pill("−10 sn", onClick = { player.seekBy(-10) })
                Pill("+10 sn", onClick = { player.seekBy(10) })
                Pill("IPA", selected = showIpa, onClick = { showIpa = !showIpa })
                Pill("Çeviri", selected = showTranslation, onClick = { showTranslation = !showTranslation })
                Pill("Ritim", selected = showRhythm, onClick = { showRhythm = !showRhythm })
            }
            Panel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Subtitles, null, tint = Accent)
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        SectionTitle("Cümle cümle akış")
                        Description("Normal yazım · IPA · kelime detayları")
                    }
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = Ink)
                }
            }
            if (showRhythm) {
                Panel {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, null, tint = Accent, modifier = Modifier.size(19.dp))
                        Spacer(Modifier.width(5.dp))
                        SectionTitle("Ritim / Zaman Çizelgesi")
                        Spacer(Modifier.weight(1f))
                        Text("ÖRNEK", color = Muted, fontSize = 10.sp)
                    }
                    Spacer(Modifier.height(13.dp))
                    BeatStrip(samplePhrases[selectedPhrase].syllables)
                }
            }
            Panel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timeline, null, tint = Accent, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.width(6.dp))
                    SectionTitle("Melodi (Perde Eğrisi)")
                    Spacer(Modifier.weight(1f))
                    Text("ÖRNEK", color = Muted, fontSize = 10.sp)
                }
                Spacer(Modifier.height(5.dp))
                MelodyGraph()
            }
            GradientCard(Modifier.fillMaxWidth()) {
                Text("Şimdi sıra sende", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
                Spacer(Modifier.height(6.dp))
                Text("Mikrofonu aç, perde ve ses şiddetini canlı izle. Şarkıdan ses kopyalamıyoruz.",
                    color = Color(0xFFDAF0FF), fontSize = 12.sp, lineHeight = 18.sp)
                Spacer(Modifier.height(13.dp))
                Button(onClick = onPractice, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = DeepBlue)) {
                    Icon(Icons.Default.Mic, null)
                    Spacer(Modifier.width(5.dp))
                    Text("Sesimi analiz et")
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TopTab(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.clip(RoundedCornerShape(11.dp))
            .background(if (selected) Color(0xFFCAE4FF) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, modifier = Modifier.size(19.dp), tint = if (selected) DeepBlue else Color.White)
        Text(label, fontSize = 10.sp, color = if (selected) DeepBlue else Color.White, maxLines = 1)
    }
}

@Composable
private fun PhraseCard(
    phrase: LearningPhrase,
    selected: Boolean,
    ipaVisible: Boolean,
    translationVisible: Boolean,
    onClick: () -> Unit,
    onWord: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(17.dp),
        color = if (selected) Color(0xFFF9FCFF) else Color.White,
        border = BorderStroke(1.dp, if (selected) Color(0xFFBCD9F4) else Line),
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Column(Modifier.padding(11.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayCircle, null, tint = Accent, modifier = Modifier.size(21.dp))
                Spacer(Modifier.width(7.dp))
                if (selected && phrase.text.contains("chão")) {
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Text("Desce, desce, desce até o ", color = Ink,
                            fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2,
                            modifier = Modifier.weight(1f, fill = false))
                        Surface(color = Color(0xFFC7E2FF), shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable(onClick = onWord)) {
                            Text("chão", Modifier.padding(horizontal = 3.dp, vertical = 2.dp),
                                color = DeepBlue, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                } else {
                    Text(phrase.text, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        maxLines = 3, modifier = Modifier.weight(1f))
                }
            }
            if (ipaVisible) {
                Text(phrase.ipa, color = DeepBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 27.dp), lineHeight = 17.sp)
            }
            if (translationVisible) {
                Text(phrase.translation, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(start = 27.dp))
            }
            if (selected) {
                Spacer(Modifier.height(2.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 2, 3, 4).forEach { n ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.size(6.dp).background(Accent, CircleShape))
                                Text("$n", fontSize = 9.sp, color = Muted)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BeatStrip(syllables: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            syllables.forEachIndexed { index, syllable ->
                Surface(color = if (index == syllables.lastIndex) Color(0xFFB5DBFF) else Color(0xFFE9F1FA),
                    shape = RoundedCornerShape(8.dp)) {
                    Text(syllable, Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                        fontSize = 10.sp, color = DeepBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            for (i in 1..4) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(7.dp).background(Accent, CircleShape))
                    Text("$i", color = Muted, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun MelodyGraph(modifier: Modifier = Modifier) {
    val y = listOf(.70f, .61f, .39f, .43f, .67f, .71f, .62f, .52f, .73f, .63f, .32f, .25f)
    Column(modifier) {
        Canvas(Modifier.fillMaxWidth().height(105.dp)) {
            val left = 14.dp.toPx()
            val top = 12.dp.toPx()
            val right = size.width - 12.dp.toPx()
            val bottom = size.height - 10.dp.toPx()
            for (n in 0..3) {
                val py = top + (bottom-top) * n / 3f
                drawLine(Color(0xFFE5EDF5), Offset(left, py), Offset(right, py), 1.dp.toPx())
            }
            val points = y.mapIndexed { index, value ->
                Offset(left + (right-left) * index / (y.size-1), top + (bottom-top) * value)
            }
            val path = Path().apply {
                points.forEachIndexed { i, p -> if (i == 0) moveTo(p.x,p.y) else lineTo(p.x,p.y) }
            }
            drawPath(path, Accent, style = Stroke(width = 2.5.dp.toPx()))
            points.forEach { p ->
                drawCircle(Color.White, 4.4.dp.toPx(), p)
                drawCircle(Accent, 2.7.dp.toPx(), p)
            }
        }
        Text("Gösterim amaçlı eğri; gerçek şarkı notaları değildir.", color = Muted, fontSize = 10.sp)
    }
}
