package com.baokiiin.mymap

data class Filter(
    var zoom: Float,
    var chan: Int? = null,
    var count: Int? = null,

    )
data class DataFilter(
    val title:String?=null,
    val type:Type,
    var dataCallBack:Float?=null,
    val data:MutableList<String>? = null,
    val category: FilterCategory? = null
)
enum class Type{
    SEEKBAR,
    SPINNER
}
enum class FilterCategory {
    CHAN,
    SOLUONG
}
