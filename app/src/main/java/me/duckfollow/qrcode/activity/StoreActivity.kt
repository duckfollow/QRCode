package me.duckfollow.qrcode.activity

import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_store.*
import me.duckfollow.qrcode.R
import me.duckfollow.qrcode.user.UserProfile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StoreActivity : AppCompatActivity() {

    private lateinit var rewardedAd:RewardedAd
    lateinit var adLoadCallback:RewardedAdLoadCallback
    var tempalte = "0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)
        tempalte = UserProfile(this).getTemplate()

        rewardedAd = RewardedAd(this, "ca-app-pub-2582707291059118/2613245693")
//        rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917") // test
        adLoadCallback = object: RewardedAdLoadCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onRewardedAdLoaded() {
                // Ad successfully loaded.
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.BASIC_ISO_DATE
                val formatted = current.format(formatter)
                Log.d("Date_Test",formatted)
                if (rewardedAd.isLoaded) {
                    val adCallback = object: RewardedAdCallback() {
                        override fun onRewardedAdOpened() {
                            // Ad opened.
                        }
                        override fun onRewardedAdClosed() {
                            // Ad closed.

                        }
                        override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                            // User earned reward.
                            UserProfile(this@StoreActivity).setTemplate(tempalte)
                            showAlertBack()
                        }
                        override fun onRewardedAdFailedToShow(errorCode: Int) {
                            // Ad failed to display.
                        }
                    }

                    rewardedAd.show(this@StoreActivity, adCallback)

                }
                else {
                    Log.d("TAG", "The rewarded ad wasn't loaded yet.")
                }
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                // Ad failed to load.
            }
        }


        btn_back.setOnClickListener {
            this.finish()
        }

        btn_template1.setOnClickListener {
            tempalte = "0"
            UserProfile(this@StoreActivity).setTemplate(tempalte)
            this.finish()
//            rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        }

        btn_template2.setOnClickListener {
            tempalte = "1"
            showAlert()
        }

        btn_template3.setOnClickListener {
            tempalte = "2"
            showAlert()
        }
    }

    fun showAlert() {
        val alertDialog: AlertDialog? = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {

                setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
                    })
                setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            }
            builder.setTitle(R.string.txt_view_ads)
            builder.create()
        }

        alertDialog!!.show()
    }

    fun showAlertBack() {
        val alertDialog: AlertDialog? = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {

                setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        this@StoreActivity.finish()
                    })
                setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            }
            builder.setTitle(R.string.use_template)
            builder.create()
        }

        alertDialog!!.show()
    }

    override fun onResume() {
        super.onResume()
        setButtonText()
    }

    fun setButtonText() {
        if (tempalte == "0") {
            btn_template1.setText(R.string.btn_use)
            btn_template1.isEnabled = false
        } else {
            btn_template1.setText(R.string.btn_free)
            btn_template1.isEnabled = true
        }

        if (tempalte == "1") {
            btn_template2.setText(R.string.btn_use)
            btn_template2.isEnabled = false
        } else {
            btn_template2.setText(R.string.btn_free)
            btn_template2.isEnabled = true
        }

        if (tempalte == "2") {
            btn_template3.setText(R.string.btn_use)
            btn_template3.isEnabled = false
        } else {
            btn_template3.setText(R.string.btn_free)
            btn_template3.isEnabled = true
        }
    }

}
