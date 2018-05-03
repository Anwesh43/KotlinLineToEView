package com.example.linetoeview

/**
 * Created by anweshmishra on 03/05/18.
 */

import android.graphics.*
import android.content.Context
import android.view.View
import android.view.MotionEvent

class LineToEView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator (var view : View, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class LineToE (var i : Int, val state : State = State()) {

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val size : Float = Math.min(w, h)/3
            paint.strokeWidth = size / 20
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = Color.parseColor("#1abc9c")
            canvas.save()
            canvas.translate(w/2, h/2)
            canvas.drawLine(0f, -size, 0f, size, paint)
            for (i in 0..2) {
                canvas.save()
                canvas.rotate(90f * state.scales[0])
                val x : Float = (i - 1) * size * state.scales[1]
                canvas.drawLine(x, 0f, x, -size, paint)
                canvas.restore()
            }
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LineToEView) {

        private val animator : Animator = Animator(view)

        private val lineToE : LineToE = LineToE(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            lineToE.draw(canvas, paint)
            animator.animate {
                lineToE.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lineToE.startUpdating {
                animator.start()
            }
        }
    }
}