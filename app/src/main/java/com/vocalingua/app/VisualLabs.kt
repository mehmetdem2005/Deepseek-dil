package com.vocalingua.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun AnatomyDiagram(sound: String) {
    Panel {
        SectionTitle("Ses yolu · artikülasyon")
        Spacer(Modifier.height(3.dp))
        Description("Ağız, geniz, yumuşak damak ve dilin temsili yan kesiti.")
        Spacer(Modifier.height(10.dp))
        Canvas(Modifier.fillMaxWidth().height(190.dp).background(Color(0xFFF2F7FB), RoundedCornerShape(12.dp))) {
            val sx = size.width / 340f
            val sy = size.height / 190f
            fun x(v: Float) = v * sx
            fun y(v: Float) = v * sy
            fun pt(a: Float,b: Float) = Offset(x(a),y(b))
            val skin = Path().apply {
                moveTo(x(44f),y(15f))
                cubicTo(x(120f),y(-5f),x(234f),y(18f),x(250f),y(80f))
                cubicTo(x(256f),y(102f),x(242f),y(118f),x(246f),y(143f))
                cubicTo(x(256f),y(160f),x(247f),y(177f),x(219f),y(185f))
                lineTo(x(124f),y(185f))
                cubicTo(x(116f),y(163f),x(109f),y(157f),x(89f),y(152f))
                cubicTo(x(70f),y(151f),x(62f),y(138f),x(56f),y(124f))
                cubicTo(x(38f),y(125f),x(27f),y(120f),x(37f),y(108f))
                cubicTo(x(47f),y(100f),x(38f),y(78f),x(39f),y(55f))
                close()
            }
            drawPath(skin, Color(0xFFEFB2A5))
            drawPath(skin, Color(0xFFC57F80), style=Stroke(width=2f*sx))
            val mouth = Path().apply {
                moveTo(x(35f),y(113f))
                cubicTo(x(78f),y(117f),x(115f),y(100f),x(155f),y(113f))
                cubicTo(x(180f),y(118f),x(185f),y(146f),x(208f),y(170f))
            }
            drawPath(mouth, Color(0xFF853F4F), style=Stroke(16f*sx, cap=StrokeCap.Round))
            drawPath(mouth, Color(0xFFF8CBD1), style=Stroke(3f*sx, cap=StrokeCap.Round))
            val nasal = Path().apply {
                moveTo(x(39f),y(91f))
                cubicTo(x(80f),y(73f),x(102f),y(76f),x(135f),y(83f))
                cubicTo(x(156f),y(86f),x(172f),y(92f),x(181f),y(104f))
            }
            drawPath(nasal, Color(0xFF8B4A5A), style=Stroke(width=12f*sx, cap=StrokeCap.Round))
            drawPath(nasal, Color(0xFFFFC6B2), style=Stroke(width=6f*sx, cap=StrokeCap.Round))
            val palate = Path().apply {
                moveTo(x(72f),y(98f))
                cubicTo(x(115f),y(94f),x(144f),y(91f),x(173f),y(109f))
                cubicTo(x(181f),y(116f),x(188f),y(122f),x(185f),y(125f))
            }
            drawPath(palate, Color(0xFFF6D8C6), style=Stroke(width=9f*sx, cap=StrokeCap.Round))
            val tongue = Path().apply {
                moveTo(x(64f),y(142f))
                cubicTo(x(80f),y(119f),x(117f),y(121f),x(145f),y(135f))
                cubicTo(x(161f),y(142f),x(180f),y(157f),x(198f),y(159f))
                cubicTo(x(145f),y(174f),x(98f),y(174f),x(64f),y(142f))
                close()
            }
            drawPath(tongue, Color(0xFFE67175))
            drawPath(tongue, Color(0xFFC14E5F), style=Stroke(width=2f*sx))
            if (sound != "ʃ") {
                val air = Path().apply {
                    moveTo(x(45f),y(86f))
                    cubicTo(x(103f),y(62f),x(154f),y(75f),x(187f),y(101f))
                }
                drawPath(air, Color(0xFF13BED7), style=Stroke(width=4f*sx, cap=StrokeCap.Round, pathEffect=PathEffect.dashPathEffect(floatArrayOf(7f*sx,5f*sx))))
                drawCircle(Color(0xFF13BED7), radius=5f*sx, center=pt(187f,101f))
            }
            drawCircle(Color(0xFF086CC5), radius=5f*sx, center=pt(183f,115f))
            drawCircle(Color(0xFFEFB94F), radius=5f*sx, center=pt(115f,132f))
            drawCircle(Color(0xFF38B6A5), radius=5f*sx, center=pt(46f,114f))
            drawLine(Accent, pt(185f,115f), pt(273f,73f), strokeWidth=1.5f*sx)
            drawLine(Color(0xFFEFB94F), pt(115f,132f), pt(280f,146f), strokeWidth=1.5f*sx)
            drawLine(Color(0xFF38B6A5), pt(46f,114f), pt(23f,152f), strokeWidth=1.5f*sx)
        }
        Spacer(Modifier.height(9.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            StatusTag("Dil", Warm, Color(0xFF955E2A))
            StatusTag("Yumuşak damak", Sky, DeepBlue)
            StatusTag("Burun yolu", TealSoft, Color(0xFF147D72))
        }
        Spacer(Modifier.height(4.dp))
        Description("Anatomik çizim şematiktir, gerçek kamera veya MRI görüntüsü değildir.")
    }
}

@Composable
fun AcousticGraphs() {
    Panel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.GraphicEq, null, tint = Accent)
            Spacer(Modifier.width(7.dp))
            SectionTitle("Akustik analiz laboratuvarı")
        }
        Description("Ölçüm yapılmadığı için aşağıdaki grafikler temsili veridir.")
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Surface(Modifier.weight(1f), color = OffWhite, shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(7.dp)) {
                    TextSmall("Spektrogram · ÖRNEK")
                    Canvas(Modifier.fillMaxWidth().height(106.dp)) {
                        val w = size.width / 42f
                        val h = size.height / 18f
                        for (i in 0 until 42) {
                            for (j in 0 until 18) {
                                val center = 5.7f + 2.6f * sin(i * .18)
                                val stripe = abs(j - center)
                                val amp = exp(-stripe * .55) + .4 * exp(-abs(j - 11.5f) * .7)
                                val intensity = (amp * (0.65 + .35 * sin(i * .62).absoluteValue)).coerceIn(0.0,1.0)
                                val c = Color(
                                    red = (.05 + .67 * intensity).toFloat(),
                                    green = (.13 + .30 * intensity).toFloat(),
                                    blue = (.30 + .52 * (1-intensity)).toFloat()
                                )
                                drawRect(c, Offset(i*w,j*h), androidx.compose.ui.geometry.Size(w+1f,h+1f))
                            }
                        }
                    }
                    TextSmall("0                         Zaman →")
                }
            }
            Surface(Modifier.weight(1f), color = OffWhite, shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(7.dp)) {
                    TextSmall("Dalga · Perde (F0)")
                    Canvas(Modifier.fillMaxWidth().height(106.dp)) {
                        val cy = size.height / 2
                        val mid = Path().apply {
                            moveTo(0f,cy)
                            for (i in 0..105) {
                                val t=i/105f
                                val env=(1f-abs(t*2-1)).coerceAtLeast(0f)
                                val amplitude=(.08f + .45f * env)
                                val dy=amplitude * sin(i*2.5f) * size.height*.65f
                                lineTo(t*size.width,cy+dy)
                            }
                        }
                        drawPath(mid, Color(0xFF0B7AD7), style=Stroke(1.5.dp.toPx()))
                        val contour = Path().apply {
                            moveTo(0f,size.height*.34f)
                            for(i in 1..60) {
                                val t=i/60f
                                val y=.34f+.09f*sin(i*.23f)+.04f*sin(i*.55f)
                                lineTo(t*size.width,y*size.height)
                            }
                        }
                        drawPath(contour, Color(0xFF7058CB), style=Stroke(2.dp.toPx()))
                    }
                    TextSmall("Ölçülen kullanıcı sesi değildir.")
                }
            }
        }
    }
}

@Composable
private fun TextSmall(value:String) {
    androidx.compose.material3.Text(value, color=Muted, fontSize=9.sp, maxLines=2)
}
