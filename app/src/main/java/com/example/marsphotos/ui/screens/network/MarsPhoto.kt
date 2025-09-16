package com.example.marsphotos.ui.screens.network

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class MarsPhoto(
    @PrimaryKey
        val id: String,
    @ColumnInfo(name = "img_src")
    @SerialName(value = "img_src")
        val imgSrc: String
)/*{
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other is MarsPhoto && other.id == id /*&& other.imgSrc == imgSrc*/)
    }
}*/
