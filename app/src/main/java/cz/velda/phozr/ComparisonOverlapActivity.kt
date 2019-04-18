package cz.velda.phozr

import android.content.Intent
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.comparison_overlap.*


class ComparisonOverlapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comparison_overlap)
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
            R.id.change ->
                startActivity(Intent(this, ComparisonSideBySide::class.java))
            R.id.swap -> swapPhotos()
        }

        return super.onOptionsItemSelected(item)

    }
}
