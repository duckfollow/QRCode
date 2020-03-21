package me.duckfollow.qrcode

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.img_logo
import me.duckfollow.qrcode.activity.GenerateQRActivity
import me.duckfollow.qrcode.activity.ScannerActivity
import me.duckfollow.qrcode.user.UserProfile
import me.duckfollow.qrcode.util.ConvertImagetoBase64
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    private val REQUEST_CAMERA = 24
    private val REQUEST_PICK = 23
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val android_id = Secure.getString(
            this.getContentResolver(),
            Secure.ANDROID_ID
        )
        MobileAds.initialize(this, "ca-app-pub-2582707291059118~8882306426")
//        val testDeviceIds = Arrays.asList(android_id)
////        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
////        MobileAds.setRequestConfiguration(configuration)
        btn_qr_scan.setOnClickListener {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)

            }else {
                val i_scanner = Intent(this,ScannerActivity::class.java)
                startActivity(i_scanner)
            }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        btn_create.setOnClickListener {
            val i_generate = Intent(this,GenerateQRActivity::class.java)
            i_generate.putExtra("text",txt_details.text.toString())
            startActivity(i_generate)
        }

        pick_img_logo.setOnClickListener {
            pickFromGallery()
        }

        if (UserProfile(this).getImgLogo() != "") {
            img_logo.setImageBitmap(ConvertImagetoBase64().base64ToBitmap(UserProfile(this).getImgLogo()))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CAMERA){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val i_scanner = Intent(this,ScannerActivity::class.java)
                startActivity(i_scanner)
            } else {

            }
        }
    }

    private fun pickFromGallery() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PICK)

        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val mimeTypes = arrayOf("image/jpeg", "image/png")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK) {
                val selectedUri = data!!.data
                if (selectedUri != null) {
                    Log.d("pathUri",selectedUri.toString())
                    val bitmap = getBitmapFromUri(selectedUri)
                    val resizeBitmap = ConvertImagetoBase64().getResizedBitmap(bitmap,512,512)
                    val imgbase64 = ConvertImagetoBase64().bitmapToBase64(resizeBitmap)
                    UserProfile(this).setImgLogo(imgbase64)
                    img_logo.setImageBitmap(resizeBitmap)
                } else {

                }
            }else {

            }
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }
}
