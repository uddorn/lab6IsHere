package com.example.lab6ishere.viewmodel

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.lab6ishere.database.AppDatabase
import com.example.lab6ishere.database.BrandRating
import com.example.lab6ishere.database.BrandRatingDao
import kotlinx.coroutines.launch
import java.io.File

class BrandViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: BrandRatingDao

    val allRatings: LiveData<List<BrandRating>>
    val highGrowthBrands: LiveData<List<BrandRating>>
    val highValueBrandCount: LiveData<Int>

    init {
        val db = AppDatabase.getDatabase(application)
        dao = db.brandRatingDao()
        allRatings = dao.getAllRatings()
        highGrowthBrands = dao.getHighGrowthBrands()
        highValueBrandCount = dao.getHighValueBrandCount()
    }

    fun addInitialData() = viewModelScope.launch {
        val sampleData = listOf(
            BrandRating(companyName = "Apple", rank2015 = 1, brandValue = 170276000000.0, changePercent = 5.0),
            BrandRating(companyName = "Google", rank2015 = 2, brandValue = 120314000000.0, changePercent = 12.0),
            BrandRating(companyName = "Coca-Cola", rank2015 = 3, brandValue = 78423000000.0, changePercent = -4.0),
            BrandRating(companyName = "Microsoft", rank2015 = 4, brandValue = 6767000000.0, changePercent = 11.0),
            BrandRating(companyName = "Toyota", rank2015 = 6, brandValue = 49048000000.0, changePercent = 16.0),
            BrandRating(companyName = "Samsung", rank2015 = 7, brandValue = 45297000000.0, changePercent = 0.0),
            BrandRating(companyName = "Amazon", rank2015 = 10, brandValue = 37948000000.0, changePercent = 29.0), // <- Вибірка 5
            BrandRating(companyName = "Disney", rank2015 = 13, brandValue = 36514000000.0, changePercent = 13.0),
            BrandRating(companyName = "Nike", rank2015 = 17, brandValue = 23171000000.0, changePercent = 16.0),
            BrandRating(companyName = "Facebook", rank2015 = 23, brandValue = 22029000000.0, changePercent = 54.0) // <- Вибірка 5
        )
        dao.insertRatings(sampleData)
    }

    fun updateRecordById(id: Int, newName: String) = viewModelScope.launch {
        val recordToUpdate = dao.getRatingById(id)
        if (recordToUpdate != null) {
            val updatedRecord = recordToUpdate.copy(companyName = newName)
            dao.updateRating(updatedRecord)
        }
    }

    fun saveReportToFile(context: Context) = viewModelScope.launch {
        val allRatingsList = allRatings.value ?: emptyList()
        val highGrowthList = highGrowthBrands.value ?: emptyList()
        val highValueCount = highValueBrandCount.value ?: 0

        val reportContent = buildString {
            append("Вся таблиця\n")
            allRatingsList.forEach {
                append("${it.lid}: ${it.companyName} ($${it.brandValue})\n")
            }
            append("\nВибірка (зростання >= 20%)\n")
            highGrowthList.forEach {
                append("${it.companyName} (+${it.changePercent}%)\n")
            }
            append("\nОбчислення\n")
            append("Знайдено брендів: $highValueCount\n")
        }

        try {
            val path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(path, "brand_report.txt")
            file.writeText(reportContent)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}