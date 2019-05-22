package cz.velda.phozr

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.activity_comparison_side_by_side.*


class ComparisonSideBySide : ComparisonActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var extras: Bundle
    lateinit var im1: Uri
    lateinit var im2: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comparison_side_by_side)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if(intent?.action == Intent.ACTION_SEND_MULTIPLE) {
            intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
                var editor = preferences.edit()

                for((i, it) in it.withIndex()) {
                    val uri = it as Uri
                    Toast.makeText(this, i.toString() + uri.toString(), Toast.LENGTH_LONG).show()
                    if(i == 0) {
                        //this.image1.setImageURI(uri)
                        Glide.with(this).load(uri).into(this.image1)
                        editor.putString("image1", uri.toString())
                    }
                    if(i == 1) {
                        this.image2.setImageURI(uri)
                        editor.putString("image2", uri.toString())
                    }
                }
                editor.apply()
            }
        } else {
            extras = intent.extras
            im1 = Uri.parse(this.intent.getStringExtra("image1"))
            im2 = Uri.parse(this.intent.getStringExtra("image2"))
            this.image1.setImageURI(Uri.parse(this.intent.getStringExtra("image1")))
            this.image2.setImageURI(Uri.parse(this.intent.getStringExtra("image2")))
        }

        // Mirroring zoomed area between both images
        image1.setOnTouchImageViewListener(TouchImageView.OnTouchImageViewListener { image2.setZoom(image1) })
        image2.setOnTouchImageViewListener(TouchImageView.OnTouchImageViewListener { image1.setZoom(image2) })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.comparison, menu)

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.getItemId()) {
            R.id.pick -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.DialogChooseWinner)
                builder.setNegativeButton(R.string.top,
                    DialogInterface.OnClickListener { dialog, id ->
                    ChooseWinner(true)
                })
                builder.setPositiveButton(R.string.bottom,
                    DialogInterface.OnClickListener { dialog, id ->
                        ChooseWinner(false)
                    })
                builder.show()
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
