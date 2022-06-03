package com.csibtn.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context : Context, attrs : AttributeSet) : View(context, attrs) {
    private lateinit var drawPath : CustomPath
    private lateinit var canvasBitmap: Bitmap
    private lateinit var drawingPaint : Paint
    private lateinit var canvasPaint : Paint
    private var brushSize : Float = 0f
    private var color = Color.BLACK
    private lateinit var canvas: Canvas
    private val paths = mutableListOf<CustomPath>()

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
    }

    internal inner class CustomPath(var color : Int, var brushSickness : Float) : Path(){

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap,0f,0f,canvasPaint)

        for (path in paths){
            drawingPaint.strokeWidth = path.brushSickness
            drawingPaint.color = path.color
            canvas.drawPath(path, drawingPaint)
        }

        if(!drawPath.isEmpty) {
            drawingPaint.strokeWidth = drawPath.brushSickness
            drawingPaint.color = drawPath.color
            canvas.drawPath(drawPath, drawingPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                drawPath.color = color
                drawPath.brushSickness = brushSize

                drawPath.reset()
                drawPath.moveTo(touchX!!,touchY!!)
            }
            MotionEvent.ACTION_MOVE ->{
                drawPath.lineTo(touchX!!,touchY!!)
            }
            MotionEvent.ACTION_UP -> {
                paths.add(drawPath)
                drawPath = CustomPath(color,brushSize)
            }
            else -> return false
        }
        invalidate()

        return true
    }

    fun setSizeForBrush(newSize : Float){
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        newSize,
        resources.displayMetrics)
        drawingPaint.strokeWidth = brushSize
    }
    fun setColor(newColor : String){
        color =  Color.parseColor(newColor)
        drawingPaint.color = color
    }
    fun undo(){
        if(paths.size > 0) {
            paths.removeAt(paths.lastIndex)
            invalidate()
        }
    }
}