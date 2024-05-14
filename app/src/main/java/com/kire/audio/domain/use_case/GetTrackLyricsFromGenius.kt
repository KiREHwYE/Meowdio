package com.kire.audio.domain.use_case

import com.kire.audio.domain.model.ILyricsRequestStateDomain
import com.kire.audio.domain.util.LyricsRequestModeDomain
import org.jsoup.Jsoup
import java.io.IOException
import javax.inject.Inject

class GetTrackLyricsFromGenius @Inject constructor() {

    operator fun invoke(
        mode: LyricsRequestModeDomain,
        title: String?,
        artist: String?,
        userInput: String?
    ): ILyricsRequestStateDomain {

        try {
            val titleFormatted = title?.toAllowedForm()
            val artistFormatted = artist?.toAllowedForm()?.replaceFirstChar(Char::titlecase)

            val url =
                when(mode) {
                    LyricsRequestModeDomain.BY_LINK -> userInput
                    LyricsRequestModeDomain.BY_TITLE_AND_ARTIST -> {
                        val urlPart = userInput?.toAllowedForm()?.replaceFirstChar(Char::titlecase)
                        ("https://genius.com/$urlPart-lyrics").replace("--+".toRegex(), "-")
                    }
                    else ->
                        ("https://genius.com/$artistFormatted-$titleFormatted-lyrics").replace("--+".toRegex(), "-")

                }

            var doc: org.jsoup.nodes.Document =
                Jsoup.connect(url).userAgent(
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0"
                ).get()
            val temp = doc.html().replace("<br>", "$$$")
            doc = Jsoup.parse(temp)

            val elements = doc.select("div.Lyrics__Container-sc-1ynbvzw-1.kUgSbL")

            var text = ""

            for (i in 0 until elements.size)
                text += elements.eq(i).text().replace("$$$", "\n")

            return ILyricsRequestStateDomain.Success(text)

        } catch (e: Exception) {

            return ILyricsRequestStateDomain.Unsuccess
        }
    }


    private fun String.toAllowedForm(): String {
        val notAllowedCharacters = "[^\\sa-zA-Z0-9_-]".toRegex()
        val hyphen = "[\\s_]+".toRegex()

        return this.trim().lowercase().replace("&", "and").replace(notAllowedCharacters, "")
            .replace(hyphen, "-").run {
                if (this.contains("feat")) this.removeRange(
                    this.indexOf("feat") - 1,
                    this.length
                ) else this
            }
    }
}