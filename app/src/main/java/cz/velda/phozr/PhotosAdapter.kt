package cz.velda.phozr

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
        itemView.findViewById<TextView>(R.id.name).text = "<NAME>"
        itemView.findViewById<TextView>(R.id.time).text = data[pos].toString()

        return itemView
    }

    fun add(item: Uri) {
        data.add(item)
        this.notifyDataSetChanged()
    }
}
//class cz.velda.phozr.PhotosAdapter: RecyclerView.Adapter<Uri>() {
//
//    val data = mutableListOf<Uri>()
//
//    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
//        val inflater = LayoutInflater.from(p0.context)
//        val view = inflater.inflate(R.layout.repo_layout, p0, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = data.size
//
//    fun onBindViewHolder(p0: ViewHolder, p1: Int) {
//        p0.itemView.findViewById<ImageView>(R.id.img).setImageURI(data[p1])
//    }
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
//}