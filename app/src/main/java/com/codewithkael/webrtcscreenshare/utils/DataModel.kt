package com.codewithkael.webrtcscreenshare.utils

enum class DataModelType{
    SignIn, StartStreaming,EndCall, Offer, Answer, IceCandidates
}


data class DataModel(
    val type:DataModelType?=null,
    val username:String,
    val target:String?=null,
    val data:Any?=null
)
