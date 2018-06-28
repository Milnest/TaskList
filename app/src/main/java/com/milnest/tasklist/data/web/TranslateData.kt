package com.milnest.tasklist.data.web

import com.google.gson.annotations.SerializedName

class TranslateData {
    @SerializedName("code")
    var code: Int? = -1
    @SerializedName("lang")
    var lang: String? = ""
    @SerializedName("text")
    var text: List<String>? = null
}