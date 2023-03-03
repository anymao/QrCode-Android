package com.anymore.qrcode.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import com.anymore.qrcode.core.R

/**
 * Created by anymore on 2023/2/27.
 */
class ViewFinderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val ivLine:ImageView
    private val animation:Animation

    init {
        ivLine = ImageView(context,attrs, defStyleAttr)
        ivLine.setImageResource(R.drawable.icon_scan_line_1)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        addView(ivLine,params)
        animation = AnimationSet(true).apply {
            val transform = TranslateAnimation(0f,0f,-500f,500f)
            transform.interpolator = LinearInterpolator()
            transform.duration = 3000L
            transform.repeatMode = Animation.RESTART
            transform.repeatCount = Animation.INFINITE
            addAnimation(transform)
        }
        startAnimation()
    }

    fun startAnimation(){
        ivLine.startAnimation(animation)
    }

    fun stopAnimation(){
        ivLine.clearAnimation()
    }

}