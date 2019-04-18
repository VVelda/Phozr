package cz.velda.phozr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.content.Intent




class ComparisonSideBySide : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comparison_side_by_side)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.comparison, menu)

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.getItemId()) {
            R.id.pick ->
                Toast.makeText(this, "Share is Selected", Toast.LENGTH_SHORT).show()
            R.id.change ->
                startActivity(Intent(this, ComparisonOverlapActivity::class.java))
        }

        return super.onOptionsItemSelected(item)

    }
}
