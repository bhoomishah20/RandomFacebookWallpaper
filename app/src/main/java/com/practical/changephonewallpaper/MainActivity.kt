package com.practical.changephonewallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.content.IntentFilter
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import java.util.*
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.facebook.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.facebook.login.LoginResult
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    var wallpaperArray = arrayOf(R.drawable.ic_wallpaper_4,R.drawable.ic_wall1,R.drawable.ic_wall2,R.drawable.ic_wall3)

    lateinit var callbackManager:CallbackManager
    var email=""

    fun getRandomWallpaperArray(max: Int): Int {
        var random = Random()
        return random.nextInt((max - 0) + 1) + 0
    }

    inner class PhoneUnlockedReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_USER_PRESENT -> {
                    changeRandomWallpaper()
                }
                Intent.ACTION_SCREEN_OFF -> {

                }
            }
        }
    }

    fun changeRandomWallpaper() {
        var randomInt = getRandomWallpaperArray(wallpaperArray.size - 1)
        println("randomInt = ${randomInt}")
        changeWallpaper(wallpaperArray[randomInt])
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerReceiver(
            PhoneUnlockedReceiver(),
            IntentFilter(Intent.ACTION_USER_PRESENT)
        )
        printHashKey(this)
        callbackManager = CallbackManager.Factory.create()
        login_button.setPermissions(Arrays.asList("public_profile", "user_friends", "email"))
        // Callback registration
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code

                val accessToken = AccessToken.getCurrentAccessToken()
                val isLoggedIn = accessToken != null && !accessToken.isExpired
                getFriendsList()

            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })
//        registerReceiver(
//            PhoneUnlockedReceiver(),
//            IntentFilter(, Intent.ACTION_SCREEN_OFF)

    }

    fun changeWallpaper(drawable: Int) {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        val bitmap = BitmapFactory.decodeResource(getResources(), drawable)
        wallpaperManager.setBitmap(bitmap)
    }

    fun printHashKey(pContext: Context) {
        try {
            val info = packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun getFriendsList(): List<String> {
        val friendslist = ArrayList<String>()
        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", null, HttpMethod.GET,
            GraphRequest.Callback { response ->
                /* handle the result */
                Log.e("Friends List: 1", response.toString())
                try {
                    val responseObject = response.jsonObject
                    val dataArray = responseObject.getJSONArray("data")

                    for (i in 0 until dataArray.length()) {
                        val dataObject = dataArray.getJSONObject(i)
                        val fbId = dataObject.getString("id")
                        val fbName = dataObject.getString("name")
                        Log.e("FbId", fbId)
                        Log.e("FbName", fbName)
                        friendslist.add(fbId)
                    }
                    Log.e("fbfriendList", friendslist.toString())
                    var friends = ""
                    if (friendslist != null && friendslist.size > 0) {
                        friends = friendslist.toString()
                        if (friends.contains("[")) {
                            friends = friends.substring(1, friends.length-1)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } finally {
                    //                    hideLoadingProgress();
                }
            }).executeAsync()
        return friendslist
    }
}
