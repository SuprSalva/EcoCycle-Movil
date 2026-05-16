package com.example.appmovil

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val headerLayout = findViewById<android.view.View>(R.id.headerLayout)
        val loginCard = findViewById<android.view.View>(R.id.loginCard)
        val bgDeco1 = findViewById<android.view.View>(R.id.bgDeco1)
        val bgDeco2 = findViewById<android.view.View>(R.id.bgDeco2)

        // Animate Background Decorations
        bgDeco1.alpha = 0f
        bgDeco2.alpha = 0f
        bgDeco1.scaleX = 0.8f
        bgDeco1.scaleY = 0.8f
        bgDeco2.scaleX = 0.8f
        bgDeco2.scaleY = 0.8f

        bgDeco1.animate()
            .alpha(0.03f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(2000)
            .setStartDelay(0)
            .start()

        bgDeco2.animate()
            .alpha(0.03f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(2000)
            .setStartDelay(200)
            .start()

        // Set initial state
        headerLayout.alpha = 0f
        headerLayout.translationY = 50f
        loginCard.alpha = 0f
        loginCard.translationY = 50f

        // Animate Header
        headerLayout.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(100)
            .start()

        // Animate Login Card
        loginCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(300)
            .start()

        // Continuous subtle movement
        val rotAnim1 = ObjectAnimator.ofFloat(bgDeco1, "rotation", 30f, 45f, 30f)
        rotAnim1.duration = 8000
        rotAnim1.repeatCount = ValueAnimator.INFINITE
        rotAnim1.repeatMode = ValueAnimator.REVERSE
        rotAnim1.startDelay = 2000
        rotAnim1.start()

        val transAnim2 = ObjectAnimator.ofFloat(bgDeco2, "translationY", 0f, 40f, 0f)
        transAnim2.duration = 6000
        transAnim2.repeatCount = ValueAnimator.INFINITE
        transAnim2.repeatMode = ValueAnimator.REVERSE
        transAnim2.startDelay = 2000
        transAnim2.start()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
