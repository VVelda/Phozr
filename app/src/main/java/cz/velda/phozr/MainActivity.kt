package cz.velda.phozr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*


val COMPARISON_PAIR = 2


class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener, View.OnClickListener {

    private val ACT_PICK_PHOTOS = 0
    private val ACT_COMPARE = 1

    lateinit var preferences: SharedPreferences

    var photos = ArrayList<Uri>()
    lateinit var adapter: PhotosAdapter
    // Comparison pair positions
    var im1 = 0; var im2 = 1

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
//            photos.add(Uri.parse(preferences.getString("image1", "")))
//            photos.add(Uri.parse(preferences.getString("image2", "")))
        }

        adapter = PhotosAdapter(this, photos)
        photosList.adapter = adapter
        photosList.onItemClickListener = this
        photosUpdated()

        fab_comp.setOnClickListener(this)
        fab_add.setOnClickListener(this)

        // because of Glide bug: trying to load thumbnail (instead of picked fxile) and then fail
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1);
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        photosUpdated()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACT_PICK_PHOTOS && resultCode == Activity.RESULT_OK && data != null) {

            if(data.clipData != null) {
                for (i in 0 until data.clipData.itemCount) {
                    photos.add(data.clipData.getItemAt(i).uri)
                }
            } else
                photos.add(data.data)

            this.adapter.notifyDataSetChanged()
            photosUpdated()
        }
        if (requestCode == ACT_COMPARE) {
            var win = -1
            var los = -1
            if(resultCode and ComparisonActivity.RESULT_FIRST != 0) {
                win = im1
                los = im2
            } else if(resultCode and ComparisonActivity.RESULT_SECND != 0) {
                win = im2
                los = im1
            }
            if(win >= 0) { // apply consequences only if winner was chosen
                if(resultCode and ComparisonActivity.RESULT_UPLOD != 0) {
                    Snackbar.make(findViewById(android.R.id.content), "The photo have been upload", Snackbar.LENGTH_LONG)
                        .setAction("Show", {}).show()
                }
                if(resultCode and ComparisonActivity.RESULT_DELET != 0) {
                    deleteImage(this, photos[los])
                    Snackbar.make(findViewById(android.R.id.content), "The photo have been erased", Snackbar.LENGTH_LONG)
                        .setAction("Undo", {}).show()
                }
                photos.remove(photos[los])
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onClick(btn: View?) {
        lateinit var intent: Intent
        when(btn) {
            fab_add -> {
                intent = Intent(Intent.ACTION_GET_CONTENT)
                // intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                // intent = Intent(Intent.ACTION_PICK)
                // intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(Intent.createChooser(intent, "Select photos"), ACT_PICK_PHOTOS)
            }
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
        }
    }

    fun photosUpdated() { // to be called when photos' list or their selection gets changed
        fab_comp.visibility =
            if (photosList.checkedItemCount == COMPARISON_PAIR || photosList.count == COMPARISON_PAIR) View.VISIBLE else View.INVISIBLE
    }

    fun activityComparison(im1: Int, im2: Int) {
        this.im1 = im1
        this.im2 = im2
        val intent = Intent(this, ComparisonSideBySide::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.putExtra("image1", photos[im1].toString())
        intent.putExtra("image2", photos[im2].toString())
        startActivityForResult(intent, ACT_COMPARE)
    }
}



// function to get image path from Internet (works for most cases with ACTION_GET_CONTENT)
//fun getImagePath(uri: Uri): String {
//    var cursor = getContentResolver().query(uri, null, null, null, null)
//    cursor.moveToFirst()
//    var document_id = cursor.getString(0)
//    document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
//    cursor.close()
//
//    cursor = getContentResolver().query(
//        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//        null,
//        MediaStore.Images.Media._ID + " = ? ",
//        arrayOf<String>(document_id),
//        null
//    )
//    cursor.moveToFirst()
//    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
//    cursor.close()
//
//    return path
//}