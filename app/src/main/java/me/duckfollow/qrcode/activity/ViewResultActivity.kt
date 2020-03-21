package me.duckfollow.qrcode.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_view_result.*
import kotlinx.android.synthetic.main.activity_view_result.adView
import me.duckfollow.qrcode.R

class ViewResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_result)
        btn_back.setOnClickListener {
            this.finish()
        }
        MobileAds.initialize(this, "ca-app-pub-2582707291059118~8882306426")
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val result = intent.extras.getString("text").toString()

        txt_result.setText(result)
    }
}
