package com.yesmin.ai.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class PulsingAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class State {
        IDLE, LISTENING, THINKING, SPEAKING
    }

    private var currentState: State = State.IDLE
    private var pulseFraction = 0f

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1800
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            pulseFraction = it.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }

    private val paintCore = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val paintRing1 = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 5f }
    private val paintRing2 = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 3f }
    private val paintRing3 = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 2f }

    init {
        animator.start()
    }

    fun setState(state: State) {
        this.currentState = state
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val baseRadius = (minOf(cx, cy) * 0.45f)

        val (coreColor, ringColor) = when (currentState) {
            State.IDLE -> Pair(Color.parseColor("#334155"), Color.parseColor("#64748B"))
            State.LISTENING -> Pair(Color.parseColor("#00F5D4"), Color.parseColor("#8000F5D4"))
            State.THINKING -> Pair(Color.parseColor("#FF007F"), Color.parseColor("#80FF007F"))
            State.SPEAKING -> Pair(Color.parseColor("#9D4EDD"), Color.parseColor("#80C77DFF"))
        }

        // Draw Expanding Outer Waves
        val r1 = baseRadius + (baseRadius * 0.4f * pulseFraction)
        val alpha1 = ((1f - pulseFraction) * 200).toInt().coerceIn(0, 255)
        paintRing1.color = ringColor
        paintRing1.alpha = alpha1
        canvas.drawCircle(cx, cy, r1, paintRing1)

        val frac2 = (pulseFraction + 0.33f) % 1f
        val r2 = baseRadius + (baseRadius * 0.6f * frac2)
        val alpha2 = ((1f - frac2) * 160).toInt().coerceIn(0, 255)
        paintRing2.color = ringColor
        paintRing2.alpha = alpha2
        canvas.drawCircle(cx, cy, r2, paintRing2)

        val frac3 = (pulseFraction + 0.66f) % 1f
        val r3 = baseRadius + (baseRadius * 0.85f * frac3)
        val alpha3 = ((1f - frac3) * 100).toInt().coerceIn(0, 255)
        paintRing3.color = ringColor
        paintRing3.alpha = alpha3
        canvas.drawCircle(cx, cy, r3, paintRing3)

        // Draw Glowing Core
        paintCore.color = coreColor
        canvas.drawCircle(cx, cy, baseRadius, paintCore)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}
