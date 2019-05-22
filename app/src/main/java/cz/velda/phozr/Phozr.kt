package cz.velda.phozr

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast

class Phozr : Application() {
    override fun onCreate() {
        super.onCreate()
        //Fresco.initialize(this)
    }
}

fun deleteImage(context: Context, uri: Uri): Boolean {
    var cursor = context.getContentResolver().query(uri, null, null, null, null)
    cursor.moveToFirst()
    var document_id = cursor.getString(0)
    Log.d("PATH", document_id)
    Log.d("PATH", uri.toString())
    document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
    cursor.close()

    val deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, document_id.toLong())
    Log.d("PATH", deleteUri.toString())
    // return if file was deleted (return value is file(s) deleted count)
    return 0 != context.contentResolver.delete(deleteUri, null, null)
}