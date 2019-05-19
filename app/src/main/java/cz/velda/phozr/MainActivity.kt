package cz.velda.phozr

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity;
import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.AdapterView

val COMPARISON_PAIR = 2


class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener, View.OnClickListener {

    private val PICK_PHOTOS = 1

    lateinit var preferences: SharedPreferences

    var photos = ArrayList<Uri>()
    lateinit var adapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if(intent?.action == Intent.ACTION_SEND_MULTIPLE) {
            intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {

                for(it in it) {
                    photos.add(it as Uri)
                }
            }

            if(photos.count() == COMPARISON_PAIR) // if comparing only 2, directly starts comparison
                activityComparison(0,1)
        } else { // add some default photos for playing
            photos.add(Uri.parse(preferences.getString("image1", "")))
            photos.add(Uri.parse(preferences.getString("image2", "")))
        }

        adapter = PhotosAdapter(this, photos)
        photosList.adapter = adapter
        photosList.onItemClickListener = this
        photosUpdated()

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        fab_comp.setOnClickListener(this)
        fab_add.setOnClickListener(this)

        // because of Glide bug: trying to load thumbnail (instead of picked fxile) and then fail
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1);
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        photosUpdated()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PHOTOS && resultCode == Activity.RESULT_OK && data != null) {

            if(data.clipData != null) {
                for (i in 0 until data.clipData.itemCount) {
                    photos.add(data.clipData.getItemAt(i).uri)
                }
            } else
                photos.add(data.data)

            this.adapter.notifyDataSetChanged()
            photosUpdated()
        }
    }

    override fun onClick(btn: View?) {
        lateinit var intent: Intent
        when(btn) {
            fab_comp -> {
                var im1 = 0; var im2 = 1
                if(photosList.checkedItemCount == COMPARISON_PAIR) {
                    val checked = photosList.checkedItemPositions
                    for (i in 0..checked.size()) {
                        if (checked[i]) {
                            im1 = im2
                            im2 = checked.keyAt(i)
                        }
                    }
                }
                activityComparison(im1, im2)
            }
            fab_add -> {
                intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(Intent.createChooser(intent, "Select photos"), PICK_PHOTOS)
            }
        }
    }

    fun photosUpdated() { // to be called when photos' list or their selection gets changed
        fab_comp.visibility =
            if (photosList.checkedItemCount == COMPARISON_PAIR || photosList.count == COMPARISON_PAIR) View.VISIBLE else View.INVISIBLE
    }

    fun activityComparison(im1: Int, im2: Int) {
        val intent = Intent(this, ComparisonSideBySide::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.putExtra("image1", photos[im1].toString())
        intent.putExtra("image2", photos[im2].toString())
        startActivity(intent)
    }
}
