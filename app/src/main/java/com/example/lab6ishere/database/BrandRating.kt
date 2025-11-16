package com.example.lab6ishere.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interbrand_ratings")
data class BrandRating(
    @PrimaryKey(autoGenerate = true)
    val lid: Int = 0,

    @ColumnInfo(name = "company_name")
    val companyName: String,

    @ColumnInfo(name = "rank_2015")
    val rank2015: Int,

    @ColumnInfo(name = "brand_value_usd")
    val brandValue: Double,

    @ColumnInfo(name = "change_percent")
    val changePercent: Double
)