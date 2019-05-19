package cz.velda.phozr

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.comparison_overlap.*


class ComparisonOverlapActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var extras: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comparison_overlap)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        extras = intent.extras
        //this.image1.setImageURI(Uri.parse(preferences.getString("image1", "")))
        this.image1.setImageURI(Uri.parse(this.intent.getStringExtra("image1")))
        //this.image2.setImageURI(Uri.parse(preferences.getString("image2", "")))
        this.image2.setImageURI(Uri.parse(this.intent.getStringExtra("image2")))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // on touching display
        swapPhotos()
        return true
    }

    fun swapPhotos() {
        // hide the top photo in order to reveal bottom one
        if(this.image2.visibility == View.INVISIBLE)
            this.image2.visibility = View.VISIBLE
        else
            this.image2.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.comparison, menu)

        return true

    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.swap).isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.getItemId()) {
            R.id.pick ->
                Toast.makeText(this, "Share is Selected", Toast.LENGTH_SHORT).show()
            R.id.change -> {
                intent = Intent(this, ComparisonSideBySide::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.putExtras(extras)
                startActivity(intent)
            }
            R.id.swap -> swapPhotos()
        }

        return super.onOptionsItemSelected(item)

    }
}
