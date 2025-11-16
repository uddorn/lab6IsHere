package com.example.lab6ishere.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BrandRatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRatings(ratings: List<BrandRating>)

    @Query("SELECT * FROM interbrand_ratings ORDER BY lid ASC")
    fun getAllRatings(): LiveData<List<BrandRating>>

    @Update
    suspend fun updateRating(rating: BrandRating)

    @Query("SELECT * FROM interbrand_ratings WHERE change_percent >= 20.0")
    fun getHighGrowthBrands(): LiveData<List<BrandRating>>

    @Query("SELECT COUNT(*) FROM interbrand_ratings WHERE brand_value_usd > 20000000000.0")
    fun getHighValueBrandCount(): LiveData<Int>

    @Query("SELECT * FROM interbrand_ratings WHERE lid = :id")
    suspend fun getRatingById(id: Int): BrandRating?
}