package cz.velda.phozr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.synthetic.main.activity_comparison_side_by_side.*
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.R.id.edit
import com.bumptech.glide.Glide


class ComparisonSideBySide : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var extras: Bundle

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
            this.image1.setImageURI(Uri.parse(this.intent.getStringExtra("image1")))
            this.image2.setImageURI(Uri.parse(this.intent.getStringExtra("image2")))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.comparison, menu)

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.getItemId()) {
            R.id.pick ->
                Toast.makeText(this, "Share is Selected", Toast.LENGTH_SHORT).show()
            R.id.change -> {
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
