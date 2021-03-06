package com.milnest.tasklist.data.web

import java.io.DataOutputStream
import java.io.IOException
import java.net.URL
import java.net.URLEncoder

import javax.net.ssl.HttpsURLConnection

/**
 * Created by t-yar on 20.04.2018.
 */

class YandexTranslate private constructor(): Translator {
    @Throws(IOException::class)
    override fun translate(transDirection: String, input: String?): String {
        val urlStr = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" +
                "trnsl.1.1.20180420T121109Z.b002d3187929b557" +
                ".b397db53cb8218077027dca1b19ad897ee594788"
        val urlObj = URL(urlStr)
        val connection = urlObj.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        val dataOutputStream = DataOutputStream(connection.outputStream)
        dataOutputStream.writeBytes("text=" + URLEncoder.encode(input, "UTF-8") +
                "&lang=" + transDirection)

        val response = connection.inputStream
        val json = java.util.Scanner(response).nextLine()
        val start = json.indexOf("[")
        val end = json.indexOf("]")
        val translated = json.substring(start + 2, end - 1)
        /*i++;
            if (translated.equals(input) && i < 2) {
                // if return equal of entered text - we need change direction of translation
                return translateInteractor("en", input);
            } else */
        return translated
    }

    companion object {
        var yandexTranslate: YandexTranslate? = null
        fun getYandexTranslateObj(): YandexTranslate? {
            if (yandexTranslate == null) {
                yandexTranslate = YandexTranslate()
            }
            return yandexTranslate
        }
    }

}
