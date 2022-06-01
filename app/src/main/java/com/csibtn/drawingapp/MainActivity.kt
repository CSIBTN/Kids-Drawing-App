package com.csibtn.drawingapp

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.csibtn.drawingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView : DrawingView
    private lateinit var binder : ActivityMainBinding
    private lateinit var imageButtonCurrentColor : ImageButton
    private lateinit var requestPermission : ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        val brushBtn = binder.ibBrushPick
        drawingView = binder.drawingView

        drawingView.setSizeForBrush(30f)
        val linearLayout = binder.llColors
        imageButtonCurrentColor = linearLayout[0] as ImageButton
        imageButtonCurrentColor.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))

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

    fun paintClicked(view: View){
        if(view != imageButtonCurrentColor){
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView.setColor(colorTag)
            imageButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))
            imageButtonCurrentColor.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet))
            imageButtonCurrentColor = imageButton
        }
    }
    private fun showRationaleDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this).apply{
        setTitle(title)
        setMessage(message)
        setPositiveButton("Cancel") {dialog, _ -> dialog.dismiss()}
        }
        builder.create().show()
    }
}