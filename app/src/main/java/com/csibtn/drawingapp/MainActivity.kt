package com.csibtn.drawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.csibtn.drawingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView : DrawingView
    private lateinit var binder : ActivityMainBinding
    private lateinit var imageButtonCurrentColor : ImageButton
    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK && result.data!=null){
            val background = findViewById<ImageView>(R.id.iv_background)
            background.setImageURI(result.data?.data)
        }
    }
    private val requestPermission : ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        permissions -> permissions.entries.forEach{
        val permissionName = it.key
        val isGranted = it.value

        if(isGranted){
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also{
                openGalleryLauncher.launch(it)
            }
        }else{
            if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){

            }
        }
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        val galleryBtn = binder.ibGallery
        val brushBtn = binder.ibBrushPick
        val undoBtn = binder.ibUndo
        drawingView = binder.drawingView

        drawingView.setSizeForBrush(30f)
        val linearLayout = binder.llColors
        imageButtonCurrentColor = linearLayout[0] as ImageButton
        imageButtonCurrentColor.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))

        brushBtn.setOnClickListener{
            showBrushSizeDialog()
        }
        galleryBtn.setOnClickListener{
            requestStoragePermission()
        }
        undoBtn.setOnClickListener{
            drawingView.undo()
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
    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationaleDialog("Kids Drawing App","Kids Drawing App need to Access Your External Storage")
        }else{
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }
}