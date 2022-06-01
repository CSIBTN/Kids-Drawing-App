package com.csibtn.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class DrawingView(context : Context, attrs : AttributeSet) : View(context, attrs) {
    private lateinit var drawPath : CustomPath
    private lateinit var canvasBitmap: Bitmap
    private lateinit var drawingPaint : Paint
    private lateinit var canvasPaint : Paint
    private var brushSize : Float = 0f
    private var color = Color.BLACK
    private lateinit var canvas: Canvas

    init {
        setUp()
    }

    private fun setUp() {
        drawingPaint = Paint()
        drawPath = CustomPath(color,brushSize)
        drawingPaint.color = color
        drawingPaint.style = Paint.Style.STROKE
        drawingPaint.strokeJoin = Paint.Join.ROUND
        drawingPaint.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
        brushSize = 20f
    }

    internal inner class CustomPath(val color : Int, val brushSickness : Float) : Path(){

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap,0f,0f,canvasPaint)

        canvas.drawPath(drawPath,drawingPaint)
    }
}