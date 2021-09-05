package com.app.tts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import java.lang.Exception

class SplashScreen : AppCompatActivity() {
    private lateinit var textview : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        textview = findViewById(R.id.SplashText)
        val a: Animation = AnimationUtils.loadAnimation(applicationContext,R.anim.fade)
        textview.startAnimation(a)

        val background = object : Thread()
        {
            override fun run()
            {
                try
                {
                    Thread.sleep(2000)

                    val intent = Intent(baseContext, UserLogin::class.java)
                    startActivity((intent))
                    finish()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
        }

        background.start()
    }
}