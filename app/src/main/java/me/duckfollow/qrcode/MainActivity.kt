package me.duckfollow.qrcode

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Secure
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_main.*
import me.duckfollow.qrcode.activity.GenerateQRActivity
import me.duckfollow.qrcode.activity.ScannerActivity
import java.util.*


class MainActivity : AppCompatActivity() {
    private val REQUEST_CAMERA = 24
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
}
