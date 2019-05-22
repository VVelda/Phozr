package cz.velda.phozr

import android.support.v7.app.AppCompatActivity

open class ComparisonActivity: AppCompatActivity() {
    companion object {
        const val RESULT_FIRST = 1
        const val RESULT_SECND = 2
        const val RESULT_DELET = 4
        const val RESULT_UPLOD = 8
    }

    fun ChooseWinner(top: Boolean) {
        val res = if(top) RESULT_FIRST else RESULT_SECND
        setResult(res)
        finish()
    }
}