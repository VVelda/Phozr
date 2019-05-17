package cz.velda.phozr

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.provider.MediaStore
import android.graphics.Bitmap
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat


class MainActivity : AppCompatActivity() {

    private val PICK_PHOTOS = 1

    lateinit var preferences: SharedPreferences

    var names = arrayOf( "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
        "Костя", "Игорь", "Анна", "Денис", "Андрей" )
    var photos = ArrayList<Uri>()
    lateinit var adapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        adapter = PhotosAdapter(this, photos)

        if(intent?.action == Intent.ACTION_SEND_MULTIPLE) {
            intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {

                for(it in it) {
                    photos.add(it as Uri)
                }
            }
        } else { // add some default photos for play    ing
            photos.add(Uri.parse(preferences.getString("image1", "")))
            photos.add(Uri.parse(preferences.getString("image2", "")))
        }

        //val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, photos)
        this.photosList.adapter = adapter

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        fab_comp.setOnClickListener { view ->
            intent = Intent(this, ComparisonOverlapActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            //intent.putStringArrayListExtra("photos", photos.map{it.toString()}.toTypedArray())
            //intent.putStringArrayListExtra("photos", names)
            //intent.putExtra("test", names)
            //intent.putExtra("photos", photos.map{it.toString()}.toTypedArray())
            intent.putExtra("image1", photos[0].toString())
            intent.putExtra("image2", photos[1].toString())
            startActivity(intent)
        }
        fab_add.setOnClickListener { view -> // Add photos button
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select photos"), PICK_PHOTOS)
        }

        // because of Glide bug: trying to load thumbnail (instead of picked fxile) and then fail
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1);
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
        }
    }

}
