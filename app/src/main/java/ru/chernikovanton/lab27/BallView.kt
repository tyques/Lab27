package ru.chernikovanton.lab27

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class BallView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DAMPING_FACTOR = 0.99f
        private const val BOUNCE_FACTOR = 0.8f
    }

    private val ballPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var ballRadius = 0f
    private var ballX = 0f
    private var ballY = 0f
    private var velocityX = 0f
    private var velocityY = 0f

    private var viewWidth = 0
    private var viewHeight = 0
    private var lastUpdateTime = 0L

    init {
        ballPaint.color = ContextCompat.getColor(context, R.color.red)
        shadowPaint.color = ContextCompat.getColor(context, R.color.dark_gray)
        ballRadius = resources.getDimension(R.dimen.ball_radius)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        resetBallPosition()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(ballX + 4, ballY + 4, ballRadius, shadowPaint)
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint)
    }

    fun updateState(gravityX: Float, gravityY: Float) {
        val currentTime = System.currentTimeMillis()
        val timeDelta = if (lastUpdateTime == 0L) 0.0f else (currentTime - lastUpdateTime) / 1000.0f
        lastUpdateTime = currentTime

        if (timeDelta > 0) {
            velocityX += -gravityX * timeDelta
            velocityY += gravityY * timeDelta

            velocityX *= DAMPING_FACTOR
            velocityY *= DAMPING_FACTOR

            val physicsMultiplier = 50f
            ballX += velocityX * timeDelta * physicsMultiplier
            ballY += velocityY * timeDelta * physicsMultiplier

            handleBoundaryCollisions()

            invalidate()
        }
    }

    private fun handleBoundaryCollisions() {
        if (ballX - ballRadius < 0) {
            ballX = ballRadius
            velocityX = -velocityX * BOUNCE_FACTOR
        } else if (ballX + ballRadius > viewWidth) {
            ballX = (viewWidth - ballRadius)
            velocityX = -velocityX * BOUNCE_FACTOR
        }

        if (ballY - ballRadius < 0) {
            ballY = ballRadius
            velocityY = -velocityY * BOUNCE_FACTOR
        } else if (ballY + ballRadius > viewHeight) {
            ballY = (viewHeight - ballRadius)
            velocityY = -velocityY * BOUNCE_FACTOR
        }
    }

    fun resetBallPosition() {
        ballX = (viewWidth / 2).toFloat()
        ballY = (viewHeight / 2).toFloat()
        velocityX = 0f
        velocityY = 0f
        lastUpdateTime = 0L
        invalidate()
    }

    fun getState(): Bundle {
        return Bundle().apply {
            putFloat("ballX", ballX)
            putFloat("ballY", ballY)
            putFloat("velocityX", velocityX)
            putFloat("velocityY", velocityY)
        }
    }

    fun restoreState(state: Bundle) {
        ballX = state.getFloat("ballX")
        ballY = state.getFloat("ballY")
        velocityX = state.getFloat("velocityX")
        velocityY = state.getFloat("velocityY")
        invalidate()
    }
}