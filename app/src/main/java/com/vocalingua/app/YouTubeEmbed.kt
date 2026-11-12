package com.vocalingua.app

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * The YouTube player remains fully official. This controller reads only playback
 * position/state through the YouTube IFrame API; it never reads or downloads audio.
 */
class YouTubeController {
    internal var webView: WebView? = null
    var playbackSeconds by mutableFloatStateOf(0f)
        internal set
    var isPlaying by mutableStateOf(false)
        internal set
    var ready by mutableStateOf(false)
        internal set
    var playerError by mutableIntStateOf(0)
        internal set

    fun setSpeed(rate: Float) = script("if(window.ytPlayer){window.ytPlayer.setPlaybackRate($rate)}")
    fun seekTo(seconds: Float) = script("if(window.ytPlayer){window.ytPlayer.seekTo(${seconds.coerceAtLeast(0f)},true)}")
    fun seekBy(seconds: Int) = script("if(window.ytPlayer){window.ytPlayer.seekTo(Math.max(0,window.ytPlayer.getCurrentTime()+($seconds)),true)}")
    fun togglePlayback() = script("if(window.ytPlayer){if(window.ytPlayer.getPlayerState()===1){window.ytPlayer.pauseVideo()}else{window.ytPlayer.playVideo()}}")
    private fun script(value: String) { webView?.evaluateJavascript(value, null) }
    fun switchVideo() {
        playbackSeconds = 0f
        isPlaying = false
        ready = false
        playerError = 0
    }
}

/** Calls arrive on a WebView thread; Compose state is always changed on main. */
private class YoutubeBridge(private val controller: YouTubeController) {
    private val main = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onProgress(seconds: Double, state: Int) {
        main.post {
            controller.playbackSeconds = seconds.toFloat()
            controller.isPlaying = state == 1
        }
    }
    @JavascriptInterface
    fun onReady() { main.post { controller.ready = true; controller.playerError = 0 } }
    @JavascriptInterface
    fun onError(code: Int) { main.post { controller.playerError = code } }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbed(
    controller: YouTubeController,
    videoId: String,
    modifier: Modifier = Modifier
) {
    // Restrict to trusted, hard-coded official artist videos.
    val allowed = setOf("Am8Yi6tjnVE", "ehBGpIlEj3k")
    val safeVideoId = if (videoId in allowed) videoId else "Am8Yi6tjnVE"
    val html = remember(safeVideoId) {
        """
        <!doctype html><html><head>
        <meta name="viewport" content="width=device-width,initial-scale=1">
        <meta name="referrer" content="strict-origin-when-cross-origin">
        <style>html,body,#player{margin:0;width:100%;height:100%;padding:0;background:#081528;overflow:hidden}</style>
        </head><body><div id="player"></div>
        <script src="https://www.youtube.com/iframe_api"></script>
        <script>
          var ytPlayer=null;
          function onYouTubeIframeAPIReady(){
            ytPlayer=new YT.Player('player',{
              videoId:'$safeVideoId',
              playerVars:{playsinline:1,controls:1,rel:0,enablejsapi:1,origin:'https://www.youtube.com'},
              events:{
                onReady:function(e){ytPlayer=e.target;Android.onReady();},
                onError:function(e){Android.onError(e.data);}
              }
            });
          }
          setInterval(function(){
            try {
              if(ytPlayer && ytPlayer.getCurrentTime){
                Android.onProgress(ytPlayer.getCurrentTime(),ytPlayer.getPlayerState());
              }
            }catch(e){}
          },350);
        </script></body></html>
        """.trimIndent()
    }
    key(safeVideoId) {
        AndroidView(
            modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Navy),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    setBackgroundColor(android.graphics.Color.rgb(8, 21, 40))
                    addJavascriptInterface(YoutubeBridge(controller), "Android")
                    controller.webView = this
                    loadDataWithBaseURL("https://www.youtube.com/", html, "text/html", "UTF-8", null)
                }
            },
            update = { controller.webView = it },
            onRelease = { view -> if(controller.webView === view) controller.webView = null; view.destroy() }
        )
    }
}
