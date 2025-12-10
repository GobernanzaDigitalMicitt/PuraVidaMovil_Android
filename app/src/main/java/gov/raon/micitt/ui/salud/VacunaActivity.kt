package gov.raon.micitt.ui.salud

import android.content.Context
import android.content.SharedPreferences

import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


import gov.raon.micitt.databinding.ActivityVacunaBinding
import gov.raon.micitt.di.network.RetrofitClient



@AndroidEntryPoint
class VacunaActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityVacunaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        binding = ActivityVacunaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        downloadAndPreviewPdf()

        val nid = sharedPreferences.getString("nid","null")
        binding.infoNidString.text = nid

        val userName = sharedPreferences.getString("userName","null")
        binding.infoNameString.text=userName

//        binding.btnDownloadPdf.setOnClickListener {
//            //Agregar función para descargar el archivo en PDF
//        }

        binding.btnDownloadPdf.setOnClickListener {
            val base64String = sharedPreferences.getString("pdfBase64", null)
            if (base64String != null) {
                val pdfFile = decodeBase64ToPdf(base64String)
                pdfFile?.let { savePdfToDownloads(it) }
            } else {
                Toast.makeText(this, "No se encontró el PDF", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun downloadAndPreviewPdf() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getVacuna()
                Log.d("API_RESPONSE", "Hash: ${response.hashId}, Tipo: ${response.mime_type}, Nombre: ${response.file_name}")

                val base64String = response.archivo ?: return@launch
                sharedPreferences.edit().putString("pdfBase64", base64String).apply()

                val pdfFile = decodeBase64ToPdf(base64String)

                pdfFile?.let { showPdfPreview(it) }

            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener el PDF en base64", e)
                Toast.makeText(this@VacunaActivity, "Error al obtener PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun decodeBase64ToPdf(base64String: String): File? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val pdfFile = File(cacheDir, "decoded_pdf.pdf")

            FileOutputStream(pdfFile).use { output ->
                output.write(decodedBytes)
            }

            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showPdfPreview(pdfFile: File) {
        try {
            val parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val page = pdfRenderer.openPage(0)

            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            binding.pdfPreview.setImageBitmap(bitmap)
            binding.pdfPreview.visibility = View.VISIBLE

            page.close()
            pdfRenderer.close()
        } catch (e: IOException) {
            Log.e("PDF_ERROR", "Error al mostrar PDF", e)
        }
    }

    private fun savePdfToDownloads(pdfFile: File) {
        val downloadsDir = getExternalFilesDir(null)
        val destinationFile = File(downloadsDir, "vacuna.pdf")

        try {
            pdfFile.copyTo(destinationFile, overwrite = true)
            Toast.makeText(this, "PDF guardado en Descargas", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("PDF_SAVE_ERROR", "Error al guardar el PDF", e)
            Toast.makeText(this, "Error al guardar el PDF", Toast.LENGTH_SHORT).show()
        }
    }
}