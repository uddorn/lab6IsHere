package com.example.lab6ishere

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.lab6ishere.viewmodel.BrandViewModel
import java.io.File

class MainActivity : AppCompatActivity() {

    private val viewModel: BrandViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonAddData = findViewById<Button>(R.id.buttonAddData)
        val buttonUpdate = findViewById<Button>(R.id.buttonUpdate)
        val buttonSaveReport = findViewById<Button>(R.id.buttonSaveReport)
        val buttonViewReport = findViewById<Button>(R.id.buttonViewReport)
        val textViewAllData = findViewById<TextView>(R.id.textViewAllData)
        val textViewSelection = findViewById<TextView>(R.id.textViewSelection)
        val textViewCount = findViewById<TextView>(R.id.textViewCount)

        buttonAddData.setOnClickListener {
            viewModel.addInitialData()
            Toast.makeText(this, "Початкові дані додано", Toast.LENGTH_SHORT).show()
        }

        buttonUpdate.setOnClickListener {
            showUpdateDialog()
        }

        buttonSaveReport.setOnClickListener {
            viewModel.saveReportToFile(this)
            Toast.makeText(this, "Звіт збережено у brand_report.txt", Toast.LENGTH_LONG).show()
        }

        buttonViewReport.setOnClickListener {
            viewReportFile()
        }

        viewModel.allRatings.observe(this) { ratings ->
            textViewAllData.text = ratings.joinToString(separator = "\n") {
                "${it.lid}: ${it.companyName} ($${it.brandValue})"
            }
        }
        viewModel.highGrowthBrands.observe(this) { brands ->
            textViewSelection.text = brands.joinToString(separator = "\n") {
                "${it.companyName} (+${it.changePercent}%)"
            }
        }
        viewModel.highValueBrandCount.observe(this) { count ->
            textViewCount.text = "Знайдено брендів: $count"
        }
    }

    private fun viewReportFile() {
        try {
            val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(path, "brand_report.txt")

            if (!file.exists()) {
                Toast.makeText(this, "Файл не знайдено. Спочатку збережіть звіт.", Toast.LENGTH_SHORT).show()
                return
            }

            val fileUri: Uri = FileProvider.getUriForFile(
                this,
                "com.example.lab6ishere.fileprovider",
                file
            )

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "text/plain")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(viewIntent)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Не знайдено додатка для перегляду текстових файлів.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(this, "Помилка при відкритті файлу.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Оновити запис")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20)

        val inputId = EditText(this)
        inputId.hint = "Введіть ID запису"
        inputId.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        layout.addView(inputId)

        val inputName = EditText(this)
        inputName.hint = "Введіть нову назву компанії"
        layout.addView(inputName)

        builder.setView(layout)

        builder.setPositiveButton("Оновити") { dialog, _ ->
            val idText = inputId.text.toString()
            val newName = inputName.text.toString()

            if (idText.isNotEmpty() && newName.isNotEmpty()) {
                try {
                    val id = idText.toInt()
                    viewModel.updateRecordById(id, newName)
                    Toast.makeText(this, "Запис #$id оновлено", Toast.LENGTH_SHORT).show()
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "ID має бути числом", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Поля не можуть бути порожніми", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Скасувати") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

}