package com.example.musicapp.model

data class SongsModel(
    val id : String,
    val title:String,
    val subtitle:String,
    val url:String,
    val coverUrl:String,
    val credits:Int,
    var downloaded : Boolean,
    val audioFileName: String,
    var isFavorite:Boolean
)
{
    constructor():this("","","","","",10,true,"",true)
}
