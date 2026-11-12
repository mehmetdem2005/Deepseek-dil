package com.vocalingua.app

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Ink = Color(0xFF111C30)
val Navy = Color(0xFF0D1729)
val DeepBlue = Color(0xFF064184)
val Accent = Color(0xFF0875DA)
val Cyan = Color(0xFF12BED7)
val Sky = Color(0xFFE8F3FF)
val Line = Color(0xFFE2EAF3)
val OffWhite = Color(0xFFF6F9FD)
val Muted = Color(0xFF637187)
val Warm = Color(0xFFFFF2E9)
val TealSoft = Color(0xFFE1F7F3)

@Composable
fun VocaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Accent,
            onPrimary = Color.White,
            background = OffWhite,
            surface = Color.White,
            onSurface = Ink,
            onBackground = Ink,
            outline = Line
        ),
        content = content
    )
}

@Composable
fun Panel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier, color = Color.White, shape = RoundedCornerShape(19.dp),
        border = BorderStroke(1.dp, Line), shadowElevation = 1.dp) {
        Column(Modifier.padding(14.dp), content = content)
    }
}

@Composable
fun Pill(
    label: String,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        color = if (selected) Accent else Color(0xFFF0F4F8),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(label, Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            color = if (selected) Color.White else Ink,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 1)
    }
}

@Composable
fun StatusTag(label: String, color: Color = Sky, foreground: Color = DeepBlue) {
    Surface(color = color, shape = RoundedCornerShape(8.dp)) {
        Text(label, Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = foreground)
    }
}

@Composable
fun PageHeader(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 11.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(onClick = onBack, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = Ink)
        }
        Text(title, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Ink, modifier = Modifier.weight(1f))
        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Accent)
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(title, modifier, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Ink)
}

@Composable
fun Description(text: String) {
    Text(text, color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
}

@Composable
fun InfoRow(label: String, detail: String, symbol: String = "•") {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.Top) {
        Surface(shape = CircleShape, color = Sky) {
            Text(symbol, Modifier.size(28.dp).wrapContentSize(), fontWeight = FontWeight.Bold,
                color = Accent, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.width(8.dp))
        Text(label, modifier = Modifier.width(95.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(detail, modifier = Modifier.weight(1f), fontSize = 11.sp, color = Muted, lineHeight = 16.sp)
    }
    HorizontalDivider(color = Line, thickness = .5.dp)
}

@Composable
fun AccentButton(label: String, icon: ImageVector? = null, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Accent),
        shape = RoundedCornerShape(11.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(5.dp))
        }
        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun GradientCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.background(
        Brush.linearGradient(listOf(Color(0xFF0C2448), Color(0xFF0861AA), Color(0xFF11AABD))),
        shape = RoundedCornerShape(18.dp)
    ).padding(16.dp), content = content)
}

@Composable
fun FeatureButton(label: String, symbol: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(onClick = onClick).padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(symbol, color = Accent, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
