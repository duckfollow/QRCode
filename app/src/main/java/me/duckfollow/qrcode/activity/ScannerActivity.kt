package me.duckfollow.qrcode.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.duckfollow.qrcode.R

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private lateinit var mScannerView: ZXingScannerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        val contentFrame = findViewById(R.id.content_frame) as ViewGroup
        mScannerView = ZXingScannerView(this@ScannerActivity)
        mScannerView.setBorderLineLength(80)
        mScannerView.setBorderStrokeWidth(30)
        mScannerView.setBorderCornerRadius(3)
        mScannerView.setBorderColor(Color.parseColor("#EDC9CAC6"))
        contentFrame.addView(mScannerView)
    }
    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this@ScannerActivity)
        mScannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    override fun handleResult(p0: Result?) {

        val i_view = Intent(this@ScannerActivity,ViewResultActivity::class.java)
        i_view.putExtra("text",p0.toString())
        startActivity(i_view)
        this@ScannerActivity.finish()

        Handler().postDelayed(Runnable {
            mScannerView.resumeCameraPreview(this@ScannerActivity)
        },2000)
    }
}

