package gov.raon.micitt.utils

import android.content.ContentValues
import org.bson.internal.Base64
import java.security.DigestException
import java.security.MessageDigest
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Util {

    fun hashSHA256(msg: String): String? {
        val hash: ByteArray
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(msg.toByteArray())
            hash = md.digest()
        } catch (e: CloneNotSupportedException) {
            throw DigestException("no podía hacer digestión de contenido parcial");
        }

        return bytesToHex(hash)
    }

    private fun bytesToHex(byteArray: ByteArray): String {
        val hexChars = CharArray(byteArray.size * 2)
        val digits = "0123456789ABCDEF"
        for (i in byteArray.indices) {
            val v = byteArray[i].toInt() and 0xff
            hexChars[i * 2] = digits[v shr 4]
            hexChars[i * 2 + 1] = digits[v and 0xf]
        }
        return String(hexChars)
    }

    fun base64UrlDecode(input: String?): String {
        val base64String = input!!.replace('-', '+').replace('_', '/')

        val padding = 4 - (base64String.length % 4)
        val paddedBase64String =
            if (padding < 4) base64String + "=".repeat(padding) else base64String

        val decodedBytes = Base64.decode(paddedBase64String)
        return String(decodedBytes, Charsets.UTF_8)
    }

    fun base64UrlEncode(input: String): String {
        val encodedString = Base64.encode(input.toByteArray(Charsets.UTF_8))

        return encodedString.replace('+', '-').replace('/', '_').replace("=", "")
    }


    // External Storage
    // Permission
    fun saveFile(context: Context, baseFileName: String, fileContents: String) {
        val dateFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val todayDate = dateFormat.format(Date())

        val fileName = "${baseFileName}_$todayDate.xml"

        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
            outputStream.write(fileContents.toByteArray())
        }
    }

    fun saveFileExternal(context: Context, baseFileName: String, fileContents: String) {

        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())

        val fileName = "${baseFileName}_$todayDate.txt"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/micitt")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        )
        uri?.let {
            context.contentResolver.openOutputStream(it).use { outputStream ->
                outputStream?.write(fileContents.toByteArray())
            }
        }
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("dd. MM. yyyy", Locale.getDefault()).format(Date())
    }

    fun readFile(context: Context, fileName: String): String? {
        return try {
            context.openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.fold("") { some, text -> "$some\n$text" }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getFilePath(context: Context, fileName: String): String {
        val file: File = context.getFileStreamPath(fileName)
        return file.absolutePath
    }

}