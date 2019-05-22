package cz.velda.phozr

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import kotlinx.android.synthetic.main.activity_comparison_side_by_side.*
import kotlinx.android.synthetic.main.dialog_pick_winner.*
import kotlinx.android.synthetic.main.dialog_pick_winner.view.*


val MAX_ZOOM = 8f

open class ComparisonActivity: AppCompatActivity() {
    companion object {
        const val RESULT_FIRST = 1
        const val RESULT_SECND = 2
        const val RESULT_DELET = 4
        const val RESULT_UPLOD = 8
    }

    lateinit var extras: Bundle
    lateinit var preferences: SharedPreferences
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        extras = intent.extras
    }

    override fun onResume() {
        super.onResume()
        image1.setImageURI(Uri.parse(extras.getString("image1")))
        image2.setImageURI(Uri.parse(extras.getString("image2")))
        image1.maxZoom = MAX_ZOOM
        image2.maxZoom = MAX_ZOOM
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.comparison, menu)
        return true
    }

    fun showPickDialog(context: Context = this, topSel: Boolean = false, botSel: Boolean = false) {

        val v = LayoutInflater.from(context).inflate(R.layout.dialog_pick_winner,null)
        // radio logic for toggle buttons
        v.leftBtn.isChecked = topSel
        v.leftBtn.setOnClickListener(View.OnClickListener {
            v.rightBtn.isChecked = !v.leftBtn.isChecked
            enableButtons(true)
        })
        v.rightBtn.isChecked = botSel
        v.rightBtn.setOnClickListener(View.OnClickListener {
            v.leftBtn.isChecked = !v.rightBtn.isChecked
            enableButtons(true)
        })
        v.upl.setOnClickListener(View.OnClickListener {
            // prove Flickr account settings
            if(v.upl.isChecked) {
                val flckr = Flickr(context)
                if (!flckr.check()) {
                    v.upl.isChecked = false
                    flckr.setup()
                }
            }
        })
        dialog = AlertDialog.Builder(context)
            .setMessage(R.string.DialogChooseWinner)
            .setView(v)
            .setNegativeButton(R.string.delete, null)
            .setPositiveButton(R.string.proceed, null)
            .create()

        dialog.show()
        if(!(topSel || botSel)) // disable buttons if not winning photo was selected
            enableButtons(false)

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            ChooseWinner(v.leftBtn.isChecked, v.upl.isChecked, true)
        }
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            ChooseWinner(v.leftBtn.isChecked, v.upl.isChecked)
        }
    }

    fun enableButtons(en: Boolean) {
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = en
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = en
    }

    fun ChooseWinner(top: Boolean, upl: Boolean = false, del: Boolean = false) {
        dialog.progress.visibility = View.VISIBLE
        var res = if(top) RESULT_FIRST else RESULT_SECND
        if(upl)
            res = res or RESULT_UPLOD
        if(del)
            res = res or RESULT_DELET
        setResult(res)

        Handler().postDelayed({
            dialog.dismiss()
            finish()
        }, 800)
    }
}