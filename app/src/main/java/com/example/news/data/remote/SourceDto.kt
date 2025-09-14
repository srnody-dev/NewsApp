package com.example.news.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SourceDto (

    @SerialName("name")
    val name: String? =""

)