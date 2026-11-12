package com.vocalingua.app

import android.speech.tts.TextToSpeech
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun WordScreen(
    onBack: () -> Unit,
    onPhoneme: (String) -> Unit,
    onPractice: () -> Unit
) {
    val tabs = listOf("Telaffuz", "Artikülasyon", "Ritim", "Hatalar", "Benzer Sesler")
    var tab by remember { mutableIntStateOf(0) }
    var favorite by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val speech = remember(context) { TextToSpeech(context) {} }
    DisposableEffect(speech) { onDispose { speech.shutdown() } }

    Column(Modifier.fillMaxSize().background(OffWhite)) {
        PageHeader("Kelime Detayı", onBack)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Panel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        speech.language = Locale.forLanguageTag("pt-BR")
                        speech.speak("chão", TextToSpeech.QUEUE_FLUSH, null, "chao")
                    }) {
                        Icon(Icons.Default.VolumeUp, null, tint = Accent, modifier = Modifier.size(27.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text("chão", fontSize = 39.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                        Text("[ʃɐ̃w̃]", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Accent)
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = Sky) {
                        Column(Modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Home, null, tint = DeepBlue, modifier = Modifier.size(40.dp))
                            Text("yer · zemin", color = DeepBlue, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Anlam: ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("yer, zemin", fontSize = 14.sp)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { favorite = !favorite }) {
                        Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            null, tint = if (favorite) Color(0xFFE74C57) else Muted)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusTag("pt-BR")
                    StatusTag("Tek hece", TealSoft, Color(0xFF137465))
                    StatusTag("Burunsal diftong", Warm, Color(0xFF9A5527))
                }
            }

            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                tabs.forEachIndexed { index, text -> Pill(text, selected = tab == index, onClick = { tab = index }) }
            }

            when(tab) {
                0 -> {
                    Panel {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TipsAndUpdates, null, tint = Accent)
                            Spacer(Modifier.width(8.dp))
                            SectionTitle("Bunu ağzında nasıl oluşturursun?")
                        }
                        Spacer(Modifier.height(8.dp))
                        Surface(color = Sky, shape = RoundedCornerShape(12.dp)) {
                            Text("Önce [ʃ] ile başla. Ardından [ɐ̃] bölümünü burunsal üret, sesi kesmeden dudaklarını yuvarlayarak [w̃] konumuna kay. Bütün hareket tek hecede.",
                                Modifier.padding(12.dp), color = DeepBlue, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                    }
                    AnatomyDiagram("ɐ̃")
                    AccentButton("Şimdi telaffuzumu dene", Icons.Default.Mic, Modifier.fillMaxWidth(), onPractice)
                }
                1 -> {
                    AnatomyDiagram("ɐ̃")
                    Panel {
                        SectionTitle("Artikülasyon sırası")
                        Spacer(Modifier.height(6.dp))
                        InfoRow("Dil", "Merkezî ve nispeten alçak konumdan geriye-yukarı kayar.", "1")
                        InfoRow("Yumuşak damak", "Alçalır. Burun yolunun akustik bağlantısı açık kalır.", "2")
                        InfoRow("Dudaklar", "Başlangıçta gevşek; [w̃] sırasında yuvarlanır.", "3")
                        InfoRow("Çene", "Açık konumdan daha dar konuma geçer.", "4")
                        InfoRow("Gırtlak", "Ses kıvrımları ötümlülüğü sürdürür.", "5")
                    }
                }
                2 -> {
                    Panel {
                        SectionTitle("Bir hecede iki artikülasyon hedefi")
                        Description("Önce [ɐ̃], sonra [w̃]. Şarkıda ikili ünlünün kayışı notanın sonuna yaklaşabilir; bu genel bir egzersizdir, kayıt ölçümü değildir.")
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("[ʃ]", "[ɐ̃]", "[w̃]").forEachIndexed { i, s ->
                                Surface(Modifier.weight(1f).clickable { onPhoneme(if (i == 0) "ʃ" else if (i == 1) "ɐ̃" else "w̃") },
                                    color = if (i == 1) Sky else Color(0xFFF0F4F8), shape = RoundedCornerShape(9.dp)) {
                                    Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(s, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = DeepBlue)
                                        Text(if (i == 0) "Başlangıç" else if (i == 1) "Ünlü" else "Kayış", fontSize = 10.sp, color = Muted)
                                    }
                                }
                            }
                        }
                    }
                    Panel { SectionTitle("Melodi örneği"); MelodyGraph() }
                }
                3 -> MistakesPanel()
                4 -> ComparisonPanel()
            }

            Panel {
                SectionTitle("Fonemleri tek tek keşfet")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    soundInfos.forEach { phoneme ->
                        Surface(modifier = Modifier.weight(1f).clickable { onPhoneme(phoneme.name) },
                            shape = RoundedCornerShape(12.dp), color = Sky) {
                            Column(Modifier.padding(vertical = 13.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("[${phoneme.name}]", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = DeepBlue)
                                Icon(Icons.Default.ChevronRight, null, tint = Accent, modifier = Modifier.size(17.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MistakesPanel() {
    Panel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WarningAmber, null, tint = Color(0xFFE47635))
            Spacer(Modifier.width(7.dp))
            SectionTitle("Muhtemel hatalar")
        }
        Spacer(Modifier.height(8.dp))
        listOf(
            "Türkçedeki düz [a] gibi, burunsallaştırmadan söylemek",
            "Sonuna bağımsız [n] veya [m] eklemek",
            "Kayışı ayrı bir 'u' hecesine bölmek",
            "[w̃] yerine [v] yapmak"
        ).forEach { mistake ->
            Surface(Modifier.fillMaxWidth().padding(vertical = 3.dp), color = Color(0xFFFFF0EB),
                shape = RoundedCornerShape(8.dp)) {
                Row(Modifier.padding(9.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Close, null, tint = Color(0xFFD94E42), modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(mistake, fontSize = 12.sp, color = Ink, lineHeight = 17.sp)
                }
            }
        }
    }
}

@Composable
fun ComparisonPanel() {
    Panel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CompareArrows, null, tint = Accent)
            Spacer(Modifier.width(7.dp))
            SectionTitle("Birbirine benzeyen sesler")
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ComparisonTile("[ɐ̃w̃]", "chão", "Burunsal + kayış", true, Modifier.weight(1f))
            ComparisonTile("[aw]", "cau", "Ağızdan; nazallik yok", false, Modifier.weight(1f))
            ComparisonTile("[o]", "avô", "Tek bir ünlü", false, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ComparisonTile(ipa: String, word: String, note: String, correct: Boolean, modifier: Modifier = Modifier) {
    Surface(modifier, color = if (correct) TealSoft else Color(0xFFF4F6F8), shape = RoundedCornerShape(11.dp)) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(ipa, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = DeepBlue)
            Text(word, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Icon(if (correct) Icons.Default.CheckCircle else Icons.Default.Cancel,
                null, tint = if (correct) Color(0xFF21A76C) else Color(0xFFDA6559))
            Text(note, fontSize = 10.sp, lineHeight = 14.sp, color = Muted)
        }
    }
}

@Composable
fun PhonemeScreen(initialSound: String, onBack: () -> Unit, onPractice: () -> Unit) {
    var selected by remember(initialSound) { mutableStateOf(initialSound) }
    val sound = soundInfos.firstOrNull { it.name == selected } ?: soundInfos[1]

    Column(Modifier.fillMaxSize().background(OffWhite)) {
        PageHeader("Ses (Fonem) İncelemesi", onBack)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Panel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("[${sound.name}]", fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, color = Ink)
                        Text(sound.classification, color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                    }
                    StatusTag(if (sound.name == "ʃ") "Sürtünmeli" else "Burunsal", TealSoft, Color(0xFF106E64))
                    Spacer(Modifier.width(5.dp))
                    StatusTag(if (sound.name == "w̃") "Kayıcı" else "Fonem")
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    soundInfos.forEach { p ->
                        Pill("[${p.name}]", selected = selected == p.name, onClick = { selected = p.name })
                    }
                }
            }
            Panel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, null, tint = Accent)
                    Spacer(Modifier.width(6.dp))
                    SectionTitle("Bilimsel çözümleme")
                }
                Spacer(Modifier.height(9.dp))
                InfoRow("Dil", sound.tongue, "D")
                InfoRow("Yumuşak damak", sound.velum, "Y")
                InfoRow("Dudak", sound.lips, "U")
                InfoRow("Çene", sound.jaw, "Ç")
                InfoRow("Boğaz", sound.throat, "B")
                InfoRow("Hava akışı", sound.air, "H")
                InfoRow("Rezonans", sound.resonance, "R")
                InfoRow("Akustik", sound.acoustic, "A")
            }
            AnatomyDiagram(selected)
            AcousticGraphs()
            Panel {
                SectionTitle("Bu sesi üretirken")
                Spacer(Modifier.height(8.dp))
                Surface(color = TealSoft, shape = RoundedCornerShape(12.dp)) {
                    Text(
                        when (selected) {
                            "ʃ" -> "Dilini dişlerinin biraz arkasına yaklaştır, havayı dar kanaldan sürtünmeli geçir. Ses tellerini titreştirme."
                            "ɐ̃" -> "Dilini merkezde alçak tut, çeneni rahat aç. Yumuşak damağın alçalarak burun yolunu açmasına izin ver."
                            else -> "İki dudağını yuvarla, dilin arkasını kaldır. 'V' gibi dişine dokunma ve burun yolunu açık tut."
                        },
                        Modifier.padding(13.dp), color = Color(0xFF0D665B), fontSize = 13.sp, lineHeight = 19.sp
                    )
                }
            }
            Panel {
                SectionTitle("Muhtemel hatalar")
                sound.mistakes.forEach { error ->
                    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Close, null, tint = Color(0xFFE25E50), modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(7.dp))
                        Text(error, fontSize = 12.sp, color = Ink)
                    }
                }
            }
            Panel {
                SectionTitle("Benzer seslerle fark")
                Spacer(Modifier.height(7.dp))
                Text(sound.comparison, fontSize = 12.sp, lineHeight = 19.sp, color = Muted)
            }
            AccentButton("Sesimi analiz et", Icons.Default.Mic, Modifier.fillMaxWidth(), onPractice)
            Spacer(Modifier.height(10.dp))
        }
    }
}
