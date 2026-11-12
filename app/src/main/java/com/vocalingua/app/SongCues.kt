package com.vocalingua.app

/**
 * Prototype data assembled from the lyrics supplied by the user.
 * Timing is deliberately flagged as approximate, and can be calibrated
 * in the player. Production must use licensed lyrics and verified timings.
 */
data class SongCue(
    val text: String,
    val ipa: String,
    val translation: String,
    val timeSeconds: Float
)

private data class Line(val text:String,val ipa:String,val tr:String)

private val refrain = listOf(
    Line("Desce, desce, desce até o chão", "[ˈdɛ.si ˈdɛ.si ˈdɛ.si aˈtɛ u ˈʃɐ̃w̃]", "İn, in, yere kadar in."),
    Line("Quando o beat bate, perde o controle", "[ˈkwɐ̃.du u ˈbi.tʃi ˈba.tʃi ˈpɛɾ.dʒi u kõˈtɾɔ.li]", "Ritim vurduğunda kontrolü kaybet."),
    Line("Então vira, vira joga pressão", "[ẽˈtɐ̃w̃ ˈvi.ɾɐ ˈvi.ɾɐ ˈʒɔ.gɐ pɾeˈsɐ̃w̃]", "Öyleyse dön, dön, baskıyı artır."),
    Line("Esse funk bate direito no coração", "[ˈɛ.si ˈfɐ̃.ki ˈba.tʃi dʒiˈɾej.tʊ nu ko.ɾaˈsɐ̃w̃]", "Bu funk doğrudan kalbe vuruyor.")
)
private val bridge = listOf(
    Line("Desce, desce sem pensar", "[ˈdɛ.si ˈdɛ.si sẽj̃ pẽˈsaɾ]", "Düşünmeden in, in."),
    Line("Se é errado, deixa rolar", "[si ɛ eˈha.du ˈdej.ʃɐ hoˈlaɾ]", "Yanlış olsa da, bırak aksın."),
    Line("Olha pra mim, vem sem razão", "[ˈɔ.ʎɐ pɾɐ ˈmĩ vẽj̃ sẽj̃ haˈzɐ̃w̃]", "Bana bak, sebepsizce gel."),
    Line("Essa noite não tem direção", "[ˈɛ.sɐ ˈnoj.tʃi nɐ̃w̃ tẽj̃ dʒi.ɾeˈsɐ̃w̃]", "Bu gecenin yönü yok.")
)
private val post = listOf(
    Line("Olha a maldade, sorriso fatal", "[ˈɔ.ʎɐ a mawˈda.dʒi soˈhi.zu faˈtaw]", "Bak şu muzırlığa, ölümcül gülümsemeye."),
    Line("Sabe que é perigo, gosto ilegal", "[ˈsa.bi ki ɛ peˈɾi.gu ˈgos.tʊ i.leˈgaw]", "Tehlikeli olduğunu biliyor, yasak bir tat."),
    Line("Quando ela mexe, o chão treme assim", "", "O hareket edince yer böyle titrer."),
    Line("Todo mundo sente quando ela vem", "", "O geldiğinde herkes hisseder.")
)
private val second = listOf(
    Line("Vai-vai com o popozão no chão, vem-vem com o popozão, novinha", "", "Büyük kalçanla aşağı in, gel, genç kız."),
    Line("A nov—, a novinha dança a pampa na f—, pare-paredão", "", "Kayıtta kesilen kelimeler var; sözler doğrulanmalı."),
    Line("Vai-vai com o popozão no chão, vem-vem com o popozão, novinha", "", "Nakarat tekrarı."),
    Line("A nov—, a novinha dança a pampa na f—, pare-paredão", "", "Kayıtta kesilen kelimeler var; sözler doğrulanmalı.")
)

val fullDemoCues: List<SongCue> = buildList {
    // Estimated marks for demo mode, *not* a transcription/alignment of the recording.
    val parts = refrain + refrain + bridge + post + second + refrain + refrain
    val offsets = listOf(
        2f, 5.1f, 8.3f, 11.4f,
        14.4f, 17.5f, 20.8f, 23.8f,
        27f, 30.2f, 33.4f, 36.6f,
        40f, 43.2f, 46.4f, 49.6f,
        53f, 57f, 61f, 65f,
        69f, 71.8f, 74.6f, 77.4f,
        80f, 82.8f, 85.3f, 87.5f
    )
    parts.forEachIndexed { index, line ->
        add(SongCue(line.text,line.ipa,line.tr,offsets[index]))
    }
}
