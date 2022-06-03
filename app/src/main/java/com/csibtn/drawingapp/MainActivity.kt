package com.csibtn.drawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
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
import androidx.lifecycle.lifecycleScope
import com.csibtn.drawingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView : DrawingView
    private lateinit var binder : ActivityMainBinding
    private lateinit var imageButtonCurrentColor : ImageButton
    private lateinit var customProgressDialog : Dialog
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
        val saveBtn = binder.ibSave
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
        saveBtn.setOnClickListener{
            if(isReadStorageAllowed()){
                showProgressDialog()
                lifecycleScope.launch(){
                    saveBitmapFile(getBitMapFromView(binder.flContainer))
                }
            }
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
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }
    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }
    private fun getBitMapFromView(view:View) : Bitmap{
        val returnBitmap =  Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnBitmap)
        val bgDrawable = view.background
        if(bgDrawable != null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnBitmap
    }
    private suspend fun saveBitmapFile(bitMap : Bitmap?) : String{
        var resultFilePath = ""
        withContext(Dispatchers.IO){
            if(bitMap != null){
                try {
                    val bytes = ByteArrayOutputStream()
                    bitMap.compress(Bitmap.CompressFormat.PNG,100,bytes)

                    val file = File(externalCacheDir?.absoluteFile.toString() + File.separator + "KidsDrawingApp" + System.currentTimeMillis() / 1000.0 + ".png")
                    val outputStream = FileOutputStream(file)
                    outputStream.write(bytes.toByteArray())
                    outputStream.close()
                    resultFilePath = file.absolutePath
                    closeProgressDialog()
                    shareImage(resultFilePath)
                }catch(e : Exception){
                    e.printStackTrace()
                }
            }
        }
        return resultFilePath
    }
    private fun showProgressDialog(){
        customProgressDialog = Dialog(this)

        customProgressDialog.setContentView(R.layout.new_custom_dialog)

        customProgressDialog.show()
    }
    private fun closeProgressDialog(){
        customProgressDialog.dismiss()
    }
    private fun shareImage(path : String){
        MediaScannerConnection.scanFile(this,arrayOf(path),null){
            _,uri ->
            Intent().also{
                it.action = Intent.ACTION_SEND
                it.putExtra(Intent.EXTRA_STREAM,uri)
                it.type = "image/png"
                startActivity(Intent.createChooser(it,"Share"))
            }
        }
    }
}