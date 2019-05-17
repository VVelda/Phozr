package cz.velda.phozr

import android.app.Application
import android.widget.Toast
import com.facebook.drawee.backends.pipeline.Fresco

class Phozr : Application() {
    override fun onCreate() {
        super.onCreate()
        //Fresco.initialize(this)
        Toast.makeText(this, "App started", Toast.LENGTH_SHORT).show()
    }
}