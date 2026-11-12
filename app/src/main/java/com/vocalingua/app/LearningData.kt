package com.vocalingua.app

data class LearningPhrase(
    val text: String,
    val ipa: String,
    val translation: String,
    val syllables: List<String>,
    val sourceIsLicensed: Boolean = false
)

val samplePhrases = listOf(
    LearningPhrase(
        "Desce, desce, desce até o chão",
        "[ˈdɛ.si ˈdɛ.si ˈdɛ.si aˈtɛ u ˈʃɐ̃w̃]",
        "İn, in, yere kadar in.",
        listOf("de-sce", "de-sce", "de-sce", "a-té", "o", "chão")
    ),
    LearningPhrase(
        "Quando o beat bate, perde o controle",
        "[ˈkwɐ̃.du u ˈbi.tʃi ˈba.tʃi ˈpɛɾ.dʒi u kõˈtɾɔ.li]",
        "Ritim vurduğunda kontrolü kaybet.",
        listOf("quan-do", "o", "beat", "ba-te", "per-de", "con-tro-le")
    ),
    LearningPhrase(
        "Canta e sente a voz vibrar",
        "[ˈkɐ̃.tɐ i ˈsẽ.tʃi a ˈvɔz viˈbɾaɾ]",
        "Şarkı söyle ve sesin titreşimini hisset.",
        listOf("can-ta", "e", "sen-te", "a", "voz", "vi-brar"),
        sourceIsLicensed = true
    )
)

data class SoundInfo(
    val name: String,
    val classification: String,
    val tongue: String,
    val velum: String,
    val lips: String,
    val jaw: String,
    val throat: String,
    val air: String,
    val resonance: String,
    val acoustic: String,
    val mistakes: List<String>,
    val comparison: String
)

val soundInfos = listOf(
    SoundInfo(
        "ʃ", "Ötümsüz, artdişeti sürtünmelisi",
        "Dil ucu veya ön kısmı dişetinin gerisine yaklaşır; temasla hava yolu kapanmaz.",
        "Yükselmiş; burun yolu kapalıdır.",
        "Hafifçe öne ve yuvarlak konuma gelebilir.",
        "Rahat ve az açık.",
        "Ses kıvrımları titreşmez.",
        "Dar kanaldan çıkan hava sürtünme oluşturur.",
        "Gürültü ağız boşluğunun ön bölümünde şekillenir.",
        "Yüksek frekanslı sürtünme gürültüsü baskındır.",
        listOf("Türkçedeki /s/ gibi ince söylemek", "Dil ucunu dişlerin arasına çıkarmak"),
        "[ʃ] sesi Türkçedeki 'ş' sesine yakındır. [ʒ] sesinde ses kıvrımları titreşir."
    ),
    SoundInfo(
        "ɐ̃", "Burunsallaşmış, neredeyse açık merkezî ünlü",
        "Dil gövdesi merkezde ve nispeten alçakta; dil ucu gevşek.",
        "Alçalır, burun boşluğuna akustik geçiş açılır.",
        "Başlangıçta yuvarlanmaz.",
        "Orta-açık, kasılmadan.",
        "Ses kıvrımları titreşir.",
        "Hava ve ses enerjisi ağız ile burun yollarına dağılır.",
        "Ağız ve burun boşluklarının birleşik rezonansı.",
        "Nazal rezonanslar ve antirezonanslar; F1/F2 tahmini nazalizasyon nedeniyle zorlaşır.",
        listOf("Türkçedeki düz [a] gibi burunsuz söylemek", "Sona ayrı bir [n] eklemek"),
        "[ɐ̃] bir burunsal ünlüdür; [a] daha açık ve ağızdan, [ə] ise daha merkezî ve kapalıdır."
    ),
    SoundInfo(
        "w̃", "Burunsallaşmış, ötümlü dudaksıl-artdamaksıl kayıcı",
        "Dil gövdesi geriye ve yukarıya, yumuşak damağa yaklaşır.",
        "Burun yolu açık tutulur.",
        "İki dudak birlikte yuvarlanır ve açıklık daralır.",
        "Başlangıca göre kapanır.",
        "Ses kıvrımları titreşmeye devam eder.",
        "Akış kesilmez, tam kapanma olmaz.",
        "Dudak yuvarlama ağız yolu rezonansını değiştirir; nazal bağ devam eder.",
        "Genellikle F1 ve F2 düşme eğilimindedir; kayış sürekli olmalıdır.",
        listOf("Düz Türkçe 'o' eklemek", "[v] gibi alt dudağı üst dişe değdirmek", "İkinci hece oluşturmak"),
        "[w̃] kayıcıdır, [v] sürtünmelidir; [u] daha kararlı bir ünlü konumudur."
    )
)
