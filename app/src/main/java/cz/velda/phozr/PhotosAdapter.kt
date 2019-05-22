package cz.velda.phozr

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.daimajia.swipe.SwipeLayout
import com.squareup.picasso.Picasso
import android.provider.MediaStore
import android.util.Log
import org.apache.commons.io.FilenameUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*




//import cz.velda.phozr.R

class PhotosAdapter(private val context: Context,
                    private val data: ArrayList<Uri>
    ): BaseAdapter() {

    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getItem(p0: Int): Any {
        return data[p0]
    }

    override fun getCount(): Int = data.size

    override fun getView(pos: Int, p1: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(R.layout.list_photo, parent, false) as SwipeLayout
        val uri = data[pos]

        itemView.addDrag(SwipeLayout.DragEdge.Bottom, itemView.findViewById(R.id.bottom_wrapper))
        itemView.isRightSwipeEnabled = true
        itemView.isLeftSwipeEnabled = true
        val imageView = itemView.findViewById<ImageView>(R.id.img)

        itemView.addSwipeListener(object : SwipeLayout.SwipeListener {
            override fun onClose(layout: SwipeLayout) {
                //when the SurfaceView totally cover the BottomView.
            }

            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {
                //you are swiping.
            }

            override fun onStartOpen(layout: SwipeLayout) {

            }

            override fun onOpen(layout: SwipeLayout) {
                // Toast.makeText(context, "Photo deleted: " + uri.toString(), Toast.LENGTH_SHORT).show()
                data.remove(uri)
                notifyDataSetChanged()
            }

            override fun onStartClose(layout: SwipeLayout) {

            }

            override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {
                //when user's hand released.
            }
        })
        Glide.with(imageView.context).load(uri).into(imageView)
        //Picasso.get().load(data[pos]).into(imageView)
        //itemView.findViewById<ImageView>(R.id.img).setImageURI(data[pos]) // load image preview

        var fileName: String = "<NAME>"
        var fileDate: String = "" // ""<missing date taken>"
        if(uri.scheme.equals("content")) { // but everything in the app should have use content scheme anyway
//            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN), null, null, null)
            val cursor = context.contentResolver.query(uri, null, null, null, null)

            Log.d(TAG, cursor.columnNames.joinToString())
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                fileName = FilenameUtils.removeExtension(fileName)
                var index: Int
                if(cursor.getColumnIndex("datetaken").also { index = it } != -1) ;
                // fallback to last_modified date of file, if datataken are not present
                else if (cursor.getColumnIndex("last_modified").also { index = it } != -1) ;

                if(index >= 0) {
                    val date = Date(cursor.getLong(index))
                    val cal = Calendar.getInstance()
                    cal.time = date
                    fileDate += DateFormat.getTimeInstance(DateFormat.MEDIUM).format(date)
                    fileDate += ", "
                    fileDate += com.ibm.icu.text.MessageFormat.format("{0,ordinal}", cal.get(Calendar.DAY_OF_MONTH))
                }
            }
        }
        itemView.findViewById<TextView>(R.id.name).text = fileName
        itemView.findViewById<TextView>(R.id.time).text = fileDate

        return itemView
    }

    fun add(item: Uri) {
        data.add(item)
        this.notifyDataSetChanged()
    }
}