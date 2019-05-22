package cz.velda.phozr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.StrictMode
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat.startActivity
import android.widget.Toast
import com.github.scribejava.apis.FlickrApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.*


class Flickr {

    private val API_URL = "https://api.flickr.com/services/rest"
    private val apiKey = APIKEY
    private val apiSecret = SECKEY
    private val preferences: SharedPreferences
    private val context: Context
    private val service = ServiceBuilder(apiKey)
        .apiSecret(apiSecret)
        .callback("phozr://setup.flickr")
        .build(FlickrApi.instance(FlickrApi.FlickrPerm.WRITE))
    var token = OAuth1AccessToken("","")

//    @Throws(IOException::class, InterruptedException::class, ExecutionException::class)
    constructor(context: Context) {
        // BAD PRACTISE: allow networking to run on main thread
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        this.context = context
        preferences = PreferenceManager.getDefaultSharedPreferences(context)

//        val token = preferences.getString("flickr_token", "")
//        if (token == "")
//            setup()
    }

    fun setup() {
        val requestToken = service.requestToken
        val authorizationUrl = service.getAuthorizationUrl(requestToken)
        store(requestToken)
        // now open website in browser to allow user authorize app
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl))
        startActivity(context, browserIntent, null)
    }
    fun finish(oauthVerifier: String): Boolean {
        val requestToken = OAuth1RequestToken(preferences.getString("flickr_token", ""), preferences.getString("flickr_secret",""))
        token = service.getAccessToken(requestToken, oauthVerifier)
        store(token)

        // It is needed to verify authorization to complete authorization
        return check()

    }

    fun check(): Boolean {
        token = OAuth1AccessToken(preferences.getString("flickr_token", ""), preferences.getString("flickr_secret",""))

        val request = OAuthRequest(Verb.GET, API_URL)
        request.addQuerystringParameter("method", "flickr.test.login")
//        request.addQuerystringParameter("format", "json")
        service.signRequest(token, request)
        val response = service.execute(request)

        if(!response.isSuccessful)
            return false

        // parsing primitive XML in Java is so painful
        return response.body.contains("stat=\"ok\"")
    }

    fun store(token: OAuth1Token) {
        preferences.edit()
            .putString("flickr_token", token.token)
            .putString("flickr_secret", token.tokenSecret)
            .apply()
    }
}

// Activity for receiving app authorization from browser
class FlickReceiver: Activity() {
    override fun onStart() {
        super.onStart()
        val flckr = Flickr(this)
        // ENHC: maybe better to also check oauth_token, not only oauth_verifier
        if(flckr.finish(intent.data.getQueryParameter("oauth_verifier")))
            Toast.makeText(this, R.string.FlickrSetUpSuccess, Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this, R.string.FlickrSetUpFailed, Toast.LENGTH_LONG).show()
        finish()
    }
}