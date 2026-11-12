package com.vocalingua.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class MicrophoneAnalyzer {
    @Volatile private var running = false
    private var recorder: AudioRecord? = null
    private var worker: Thread? = null
    private val ui = Handler(Looper.getMainLooper())

    fun start(onResult: (Float, Float) -> Unit, onError: (String) -> Unit) {
        if (running) return
        val sampleRate = 16000
        val minSize = AudioRecord.getMinBufferSize(
            sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )
        if (minSize <= 0) { onError("Mikrofon biçimi desteklenmiyor."); return }
        val record = try {
            @Suppress("MissingPermission")
            AudioRecord(
                MediaRecorder.AudioSource.MIC, sampleRate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                max(8192, minSize * 2)
            )
        } catch (e: Exception) { onError("Mikrofon açılamadı: ${e.message}"); return }
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            record.release()
            onError("Mikrofon başlatılamadı.")
            return
        }
        recorder = record
        running = true
        worker = Thread {
            try {
                record.startRecording()
                val frame = ShortArray(2048)
                while (running) {
                    val read = record.read(frame, 0, frame.size)
                    if (read > 500) {
                        val rms = sqrt(frame.take(read).sumOf { it.toDouble() * it } / read).toFloat() / 32768f
                        val freq = if (rms > .014f) estimatePitch(frame, read, sampleRate) else 0f
                        ui.post { if (running) onResult(freq, rms) }
                    }
                }
            } catch (e: Exception) {
                ui.post { onError("Ses analizi durdu: ${e.message}") }
            } finally {
                try { record.stop() } catch (_: Exception) {}
                record.release()
                if (recorder === record) recorder = null
            }
        }.apply { name = "VocaLingua-Microphone"; start() }
    }

    fun stop() {
        running = false
        try { recorder?.stop() } catch (_: Exception) {}
    }

    private fun estimatePitch(data: ShortArray, n: Int, rate: Int): Float {
        // Normalised autocorrelation: a simple live-practice estimator, NOT clinical diagnostics.
        val minLag = rate / 850
        val maxLag = min(rate / 75, n / 2)
        var bestLag = 0
        var best = 0.0
        for (lag in minLag..maxLag) {
            var dot = 0.0
            var powerA = 0.0
            var powerB = 0.0
            var i = 0
            while (i + lag < n) {
                val a = data[i].toDouble()
                val b = data[i + lag].toDouble()
                dot += a * b
                powerA += a * a
                powerB += b * b
                i += 3
            }
            val score = dot / sqrt(powerA * powerB + 1.0)
            if (score > best) { best = score; bestLag = lag }
        }
        return if (best > .64 && bestLag > 0) rate.toFloat() / bestLag else 0f
    }
}

@Composable
fun PracticeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val monitor = remember { MicrophoneAnalyzer() }
    var running by remember { mutableStateOf(false) }
    var pitch by remember { mutableFloatStateOf(0f) }
    var rms by remember { mutableFloatStateOf(0f) }
    var error by remember { mutableStateOf("") }
    var beganAt by remember { mutableLongStateOf(0L) }
    val prefs = remember { context.getSharedPreferences("vocalingua", Context.MODE_PRIVATE) }

    fun stop() {
        monitor.stop()
        if (running && System.currentTimeMillis() - beganAt > 1000) {
            prefs.edit().putInt("practices", prefs.getInt("practices", 0) + 1).apply()
        }
        running = false
    }
    fun start() {
        error = ""
        monitor.start(onResult = { f, volume -> pitch = f; rms = volume }, onError = {
            error = it; running = false
        })
        running = error.isEmpty()
        beganAt = System.currentTimeMillis()
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) start() else error = "Mikrofon izni gerekli. Android ayarlarından izin verebilirsin."
    }
    DisposableEffect(Unit) { onDispose { monitor.stop() } }

    Column(Modifier.fillMaxSize().background(OffWhite)) {
        PageHeader("Canlı Ses Laboratuvarı", onBack)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GradientCard(Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Mic, null, tint = Color.White, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text("Sesini bilimsel olarak gör", color = Color.White,
                    fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Spacer(Modifier.height(6.dp))
                Text("Mikrofon verisi yalnızca cihazında işlenir. Ses kaydın sunucuya gönderilmez.",
                    color = Color(0xFFDCEFFF), fontSize = 12.sp, lineHeight = 19.sp)
            }
            Panel {
                SectionTitle("Canlı ölçümler")
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Metric("Temel frekans", if (pitch > 0f) "${pitch.roundToInt()} Hz" else "—",
                        if (pitch > 0f) noteName(pitch) else "Ses bekleniyor", Modifier.weight(1f))
                    Metric("Ses seviyesi", if (rms > 0) "${(rms * 100).roundToInt()}%" else "—",
                        "Bağıl RMS", Modifier.weight(1f))
                }
                Spacer(Modifier.height(15.dp))
                LinearProgressIndicator(
                    progress = { (rms * 8).coerceIn(0f,1f) },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = Cyan, trackColor = Sky
                )
                Spacer(Modifier.height(9.dp))
                Description("Perde tahmini tek sesli, temiz mikrofon kayıtlarında daha güvenilirdir. Formant ve IPA doğruluğu burada henüz otomatik ölçülmüyor.")
            }
            if (error.isNotBlank()) {
                Surface(color = Color(0xFFFFEDE8), shape = RoundedCornerShape(12.dp)) {
                    Text(error, Modifier.padding(12.dp), color = Color(0xFF9C4138), fontSize = 12.sp)
                }
            }
            AccentButton(if (running) "Ölçümü durdur" else "Mikrofonu aç",
                if (running) Icons.Default.Stop else Icons.Default.Mic,
                Modifier.fillMaxWidth()) {
                if (running) stop() else {
                    if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        start()
                    } else permission.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
            Panel {
                SectionTitle("chão · [ʃɐ̃w̃]")
                Spacer(Modifier.height(8.dp))
                Text("1. [ʃ] sesiyle başla.\n2. [ɐ̃] sesini burunsallaştır.\n3. Sesini kesmeden [w̃] yönüne kay.\n4. Tek bir hecede bitir.",
                    fontSize = 13.sp, lineHeight = 23.sp, color = Ink)
                Spacer(Modifier.height(10.dp))
                Text("Bu ilk sürümde mikrofondan temel frekans ve ses şiddeti ölçülür. Seslerin doğru artiküle edildiği iddiasında bulunulmaz.",
                    color = Muted, fontSize = 11.sp, lineHeight = 17.sp)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun Metric(title: String, value: String, extra: String, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(12.dp), color = Sky) {
        Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = Muted, fontSize = 10.sp)
            Text(value, color = DeepBlue, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
            Text(extra, color = Muted, fontSize = 11.sp)
        }
    }
}

private fun noteName(hz: Float): String {
    val midi = (69 + 12 * (ln(hz / 440.0) / ln(2.0))).roundToInt()
    val names = arrayOf("Do", "Do♯", "Re", "Re♯", "Mi", "Fa", "Fa♯", "Sol", "Sol♯", "La", "La♯", "Si")
    return names[((midi % 12) + 12) % 12] + (midi / 12 - 1)
}

@Composable
fun LibraryScreen(onBack: () -> Unit, onSong: () -> Unit) {
    Column(Modifier.fillMaxSize().background(OffWhite)) {
        PageHeader("Şarkı Kütüphanesi", onBack)
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GradientCard(Modifier.fillMaxWidth()) {
                Text("Şarkıyı aç, sesi keşfet.", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(Modifier.height(6.dp))
                Text("YouTube, IPA, hece zamanları ve vokal tekniğini tek ekranda çalış.",
                    color = Color(0xFFE5F2FF), fontSize = 13.sp)
            }
            Panel(Modifier.fillMaxWidth().clickable(onClick = onSong)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayCircle, null, tint = Accent, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        SectionTitle("SEM CONTROLE")
                        Description("nightfxrce · Brezilya Portekizcesi · ders örneği")
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = Muted)
                }
            }
            Panel {
                SectionTitle("İçerik hakları")
                Description("Şarkı YouTube'un resmî gömülü oynatıcısından çalınır. Lisans olmadan ses kaydı indirilmiyor veya yeniden dağıtılmıyor. Ders metinleri demo amaçlıdır.")
            }
        }
    }
}

@Composable
fun ProgressScreen(onBack: () -> Unit, onPractice: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("vocalingua", Context.MODE_PRIVATE) }
    val practices = prefs.getInt("practices", 0)
    Column(Modifier.fillMaxSize().background(OffWhite)) {
        PageHeader("İlerlemem", onBack)
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GradientCard(Modifier.fillMaxWidth()) {
                Text("$practices", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text("tamamlanan mikrofon alıştırması", color = Color.White)
            }
            Panel {
                SectionTitle("Çalışma planın")
                Spacer(Modifier.height(7.dp))
                Text("1. Burunsal [ɐ̃] sesi · 3 tekrar\n2. [ɐ̃w̃] geçişi · 5 tekrar\n3. 'chão' tek hece çalışması\n4. Kendi sesini ölç ve takip et",
                    fontSize = 13.sp, lineHeight = 24.sp, color = Ink)
            }
            AccentButton("Alıştırmaya başla", Icons.Default.Mic, Modifier.fillMaxWidth(), onPractice)
        }
    }
}
