package com.milnest.tasklist;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by t-yar on 20.04.2018.
 */

public class YandexTranslate {
        //private static int i = 0;
        /*public static void main(String[] args) throws IOException {
            System.out.println(translate("ru", args[0]));
        }*/
        /**Переводит текст.
         * transDirection - направление перевода в формате ru-en (c русского на английский)
         * input - текст перевода
         * */
        public static String translate(String transDirection, String input) throws IOException {
            String urlStr = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" +
                    "trnsl.1.1.20180420T121109Z.b002d3187929b557" +
                    ".b397db53cb8218077027dca1b19ad897ee594788";
            URL urlObj = new URL(urlStr);
            HttpsURLConnection connection = (HttpsURLConnection)urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes("text=" + URLEncoder.encode(input, "UTF-8") +
                    "&lang=" + transDirection);

            InputStream response = connection.getInputStream();
            String json = new java.util.Scanner(response).nextLine();
            int start = json.indexOf("[");
            int end = json.indexOf("]");
            String translated = json.substring(start + 2, end - 1);
            /*i++;
            if (translated.equals(input) && i < 2) {
                // if return equal of entered text - we need change direction of translation
                return translate("en", input);
            } else */
            return translated;
        }

}
