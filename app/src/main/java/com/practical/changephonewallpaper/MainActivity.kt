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
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.facebook.CallbackManager
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import com.facebook.AccessToken





class MainActivity : AppCompatActivity() {

    var wallpaperArray =
        arrayOf(
            R.drawable.ic_wallpaper_4,R.drawable.ic_wall1,R.drawable.ic_wall2,R.drawable.ic_wall3)

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
        login_button.setPermissions(Arrays.asList(email))
        // Callback registration
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code

                val accessToken = AccessToken.getCurrentAccessToken()
                val isLoggedIn = accessToken != null && !accessToken.isExpired

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
}
