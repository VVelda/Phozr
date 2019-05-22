package cz.velda.phozr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.comparison_overlap.*


class ComparisonOverlapActivity : ComparisonActivity(), View.OnTouchListener {

    private lateinit var frontImage: TouchImageView
    private lateinit var backImage: TouchImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comparison_overlap)

        frontImage = image1
        backImage = image2
        image1.setOnTouchListener(this)
        image2.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event!!.action) {
            // show the photo below when user press finger on screen (without any gesture)
            // must change opacity of the layer instead of visibility (View.VISIBLE / INVISIBLE)
            //  which makes back image stealing touch events
            MotionEvent.ACTION_UP ->
                frontImage.alpha = 1f
            MotionEvent.ACTION_DOWN ->
                Handler().postDelayed({
                    frontImage.alpha = 0f
                }, 100) // wait some time if user does not start some gesture
            else -> { // other events such as movement
                frontImage.alpha = 1f
            }
        }
        backImage.setZoom(frontImage)
        return false
    }

    fun swapPhotos() {
        backImage.bringToFront()
        with(frontImage) { // swap back and front images
            frontImage = backImage
            backImage = this
        }
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
