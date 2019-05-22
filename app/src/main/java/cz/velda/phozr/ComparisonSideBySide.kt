package cz.velda.phozr

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.activity_comparison_side_by_side.*


class ComparisonSideBySide : ComparisonActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comparison_side_by_side)

        // Mirroring zoomed area between both images
        image1.setOnTouchImageViewListener(TouchImageView.OnTouchImageViewListener { image2.setZoom(image1) })
        image2.setOnTouchImageViewListener(TouchImageView.OnTouchImageViewListener { image1.setZoom(image2) })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.getItemId()) {
            R.id.pick -> {
                showPickDialog()
            } R.id.change -> {
                val intent = Intent(this, ComparisonOverlapActivity::class.java)
                // intent.putExtra("image1",this.image1.uri)
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.putExtras(extras)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)

    }
}
