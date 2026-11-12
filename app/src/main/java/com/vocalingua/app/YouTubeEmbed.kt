package com.vocalingua.app

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp

class YouTubeController {
    internal var webView: WebView? = null
    fun setSpeed(speed: Float) {
        webView?.evaluateJavascript("if(window.ytPlayer&&ytPlayer.setPlaybackRate){ytPlayer.setPlaybackRate($speed)}", null)
    }
    fun seekBy(seconds: Int) {
        webView?.evaluateJavascript(
            "if(window.ytPlayer&&ytPlayer.seekTo){ytPlayer.seekTo(Math.max(0,ytPlayer.getCurrentTime()+($seconds)),true)}",
            null
        )
    }
    fun togglePlayback() {
        webView?.evaluateJavascript(
            "if(window.ytPlayer){if(ytPlayer.getPlayerState()===1){ytPlayer.pauseVideo()}else{ytPlayer.playVideo()}}",
            null
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbed(controller: YouTubeController, modifier: Modifier = Modifier) {
    val html = remember {
        """
        <!doctype html><html lang="tr"><head>
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
        <meta name="referrer" content="strict-origin-when-cross-origin">
        <style>html,body,#player{margin:0;padding:0;width:100%;height:100%;background:#081528;overflow:hidden}</style>
        </head><body><div id="player"></div>
        <script src="https://www.youtube.com/iframe_api"></script>
        <script>
          var ytPlayer=null;
          function onYouTubeIframeAPIReady(){
            ytPlayer=new YT.Player('player',{
              videoId:'Am8Yi6tjnVE',
              playerVars:{playsinline:1,rel:0,controls:1,origin:'https://www.youtube.com'},
              events:{onReady:function(e){window.ytPlayer=e.target;}}
            });
          }
        </script>
        </body></html>
        """.trimIndent()
    }
    AndroidView(
        modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Navy),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = true
                webChromeClient = WebChromeClient()
                setBackgroundColor(android.graphics.Color.rgb(8,21,40))
                controller.webView = this
                loadDataWithBaseURL("https://www.youtube.com/", html, "text/html", "UTF-8", null)
            }
        },
        update = { controller.webView = it }
    )
}
