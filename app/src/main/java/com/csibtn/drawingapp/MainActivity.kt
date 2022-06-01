package com.csibtn.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.csibtn.drawingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView : DrawingView
    private lateinit var binder : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        val brushBtn = binder.ibBrushPick
        drawingView = binder.drawingView

        drawingView.setSizeForBrush(30f)


        brushBtn.setOnClickListener{
            showBrushSizeDialog()
        }
    }

    private fun  showBrushSizeDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallBtn = brushDialog.findViewById<ImageButton>(R.id.smallBrush)
        val mediumBtn = brushDialog.findViewById<ImageButton>(R.id.mediumBrush)
        val largeBtn = brushDialog.findViewById<ImageButton>(R.id.largeBrush)

        smallBtn.setOnClickListener{
            drawingView.setSizeForBrush(15f)
            brushDialog.dismiss()
        }

        mediumBtn.setOnClickListener{
            drawingView.setSizeForBrush(35f)
            brushDialog.dismiss()
        }
        largeBtn.setOnClickListener{
            drawingView.setSizeForBrush(60f)
            brushDialog.dismiss()
        }
        brushDialog.show()
    }
}