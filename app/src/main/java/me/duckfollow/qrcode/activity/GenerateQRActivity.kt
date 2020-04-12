package me.duckfollow.qrcode.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.activity_generate_qr.*
import me.duckfollow.qrcode.R
import me.duckfollow.qrcode.user.UserProfile
import me.duckfollow.qrcode.util.ConvertImagetoBase64
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class GenerateQRActivity : AppCompatActivity() {
    private lateinit var rewardedAd:RewardedAd
    var QRcodeWidth = 500
    private val STORAGE_PERMISSION = 3
    var text_qr_code = ""
    var template = "0"
    var template_backup = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_qr)
        template = UserProfile(this).getTemplate()
        template_backup = template
        btn_back.setOnClickListener {
            this.finish()
        }

        btn_store.setOnClickListener {
            val i_store = Intent(this,StoreActivity::class.java)
            startActivity(i_store)
        }

        MobileAds.initialize(this, "ca-app-pub-2582707291059118~8882306426")
        val android_id = Settings.Secure.getString(
            this.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
//        val testDeviceIds = Arrays.asList(android_id)
//        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
//        MobileAds.setRequestConfiguration(configuration)
        rewardedAd = RewardedAd(this, "ca-app-pub-2582707291059118/2613245693")
//        rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917") // test
        val adLoadCallback = object: RewardedAdLoadCallback() {
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
                            if (UserProfile(this@GenerateQRActivity).getDateUser() != formatted) {
                                adsReward("close")
                            }
                        }
                        override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                            // User earned reward.
                            UserProfile(this@GenerateQRActivity).setDateUser(formatted)
                            adsReward("Reward")
                        }
                        override fun onRewardedAdFailedToShow(errorCode: Int) {
                            // Ad failed to display.
                        }
                    }

                    if (UserProfile(this@GenerateQRActivity).getDateUser() != formatted) {
                        rewardedAd.show(this@GenerateQRActivity, adCallback)
                    }
                }
                else {
                    Log.d("TAG", "The rewarded ad wasn't loaded yet.")
                }
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                // Ad failed to load.
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)

        text_qr_code = intent.extras.getString("text").toString()

        Handler().postDelayed(Runnable {
            if (template == "0") {
                view_template1.visibility = View.VISIBLE
                view_template2.visibility = View.GONE
                view_template3.visibility = View.GONE
                img_qr_code.setImageBitmap(TextToImageEncode(text_qr_code))
                img_qr_code2.setImageBitmap(TextToImageEncode(text_qr_code))
            } else if (template == "1") {
                view_template2.visibility = View.VISIBLE
                view_template1.visibility = View.GONE
                view_template3.visibility = View.GONE
                img_qr_code_template2.setImageBitmap(TextToImageEncode(text_qr_code))
                img_qr_code_template2_shared.setImageBitmap(TextToImageEncode(text_qr_code))
            } else if (template == "2") {
                view_template3.visibility = View.VISIBLE
                view_template1.visibility = View.GONE
                view_template2.visibility = View.GONE
                img_qr_code_template3.setImageBitmap(TextToImageEncode(text_qr_code))
                img_qr_code_template3_shared.setImageBitmap(TextToImageEncode(text_qr_code))
            }

            shimmer_view_container.visibility = View.GONE
        },1000)

        btn_shared.setOnClickListener {

            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION)

            }else {
                shared()
            }
        }
        if (UserProfile(this).getImgLogo() != "") {
            img_logo.setImageBitmap(ConvertImagetoBase64().base64ToBitmap(UserProfile(this).getImgLogo()))
            img_logo2.setImageBitmap(ConvertImagetoBase64().base64ToBitmap(UserProfile(this).getImgLogo()))
            img_logo_template2.setImageBitmap(ConvertImagetoBase64().base64ToBitmap(UserProfile(this).getImgLogo()))
            img_logo_template2_shared.setImageBitmap(ConvertImagetoBase64().base64ToBitmap(UserProfile(this).getImgLogo()))
            img_logo_template3.setImageBitmap(ConvertImagetoBase64().base64ToBitmap(UserProfile(this).getImgLogo()))
            img_logo_template3_shared.setImageBitmap(ConvertImagetoBase64().base64ToBitmap(UserProfile(this).getImgLogo()))
        }

        seekbar_qr_code.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar!!.progress
                Log.d("process_test",progress.toString())
            }
        })

        val isSwitchChecked = UserProfile(this).getLogoSwitch().toBoolean()
        if (!isSwitchChecked) {
            img_logo.visibility = View.GONE
            img_logo2.visibility = View.GONE
            img_logo_template2.visibility = View.GONE
            img_logo_template2_shared.visibility = View.GONE
            img_logo_template3.visibility = View.GONE
            img_logo_template3_shared.visibility = View.GONE
        }
    }

    private fun shared(){
        var b:Uri? = null
        if (template == "0") {
            b = getImageUri(getBitmapFromView(view_shared2))
        }else if (template == "1") {
            b = getImageUri(getBitmapFromView(view_template2_shared))
        } else if (template == "2") {
            b = getImageUri(getBitmapFromView(view_template3_shared))
        }
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, b)
            type = "image/*"
        }

        val shareIntent = Intent.createChooser(sendIntent, "OzoneNotIncluded_Share")
        startActivity(shareIntent)
    }

    fun getImageUri(inImage:Bitmap): Uri {
        val bytes = ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        val path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "OzoneNotIncluded_Share", null);
        return Uri.parse(path);
    }

    private fun getBitmapFromView(view: View):Bitmap {
        view.setLayoutParams(
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        val dm = this.getResources().getDisplayMetrics();
        view.measure(
            View.MeasureSpec.makeMeasureSpec(dm.widthPixels,
                View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(dm.heightPixels,
                View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        val bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
            view.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888);

        val canvas = Canvas(bitmap);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(canvas);
        return bitmap;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shared()
            } else {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        template = UserProfile(this).getTemplate()

        if (template_backup != template) {
            shimmer_view_container.visibility = View.VISIBLE
            Handler().postDelayed(Runnable {
                if (template == "0") {
                    view_template1.visibility = View.VISIBLE
                    view_template2.visibility = View.GONE
                    view_template3.visibility = View.GONE
                    img_qr_code.setImageBitmap(TextToImageEncode(text_qr_code))
                    img_qr_code2.setImageBitmap(TextToImageEncode(text_qr_code))
                } else if (template == "1") {
                    view_template2.visibility = View.VISIBLE
                    view_template1.visibility = View.GONE
                    view_template3.visibility = View.GONE
                    img_qr_code_template2.setImageBitmap(TextToImageEncode(text_qr_code))
                    img_qr_code_template2_shared.setImageBitmap(TextToImageEncode(text_qr_code))
                } else if (template == "2") {
                    view_template3.visibility = View.VISIBLE
                    view_template1.visibility = View.GONE
                    view_template2.visibility = View.GONE
                    img_qr_code_template3.setImageBitmap(TextToImageEncode(text_qr_code))
                    img_qr_code_template3_shared.setImageBitmap(TextToImageEncode(text_qr_code))
                }

                shimmer_view_container.visibility = View.GONE
            }, 1000)

            template_backup = template
        }
    }

    @Throws(WriterException::class)
    private fun TextToImageEncode(Value: String): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(
                Value,
                BarcodeFormat.QR_CODE,
                QRcodeWidth, QRcodeWidth, null
            )

        } catch (Illegalargumentexception: IllegalArgumentException) {

            return null
        }

        val bitMatrixWidth = bitMatrix.getWidth()

        val bitMatrixHeight = bitMatrix.getHeight()

        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth

            for (x in 0 until bitMatrixWidth) {

                pixels[offset + x] = if (bitMatrix.get(x, y))
                    resources.getColor(R.color.black)
                else
                    resources.getColor(R.color.white)
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)

        bitmap.setPixels(pixels, 0, QRcodeWidth, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

    fun adsReward(type:String) {
        val mView = layoutInflater.inflate(R.layout.layout_ads_reward, null)
        val bottomSheetDialogLoading = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetDialogLoading.setContentView(mView)
        bottomSheetDialogLoading.setCancelable(true)

        val bottomSheet = bottomSheetDialogLoading.findViewById<View>(R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.peekHeight = Resources.getSystem().getDisplayMetrics().heightPixels* Resources.getSystem().displayMetrics.density.toInt()

        val view_root = mView.findViewById<RelativeLayout>(R.id.view_root)
        view_root.setOnClickListener {
            bottomSheetDialogLoading.cancel()
        }

        val text_view_ads = mView.findViewById<TextView>(R.id.text_view_ads)
        if (type == "Reward") {
            text_view_ads.setText(R.string.txt_support)
        } else {
            text_view_ads.setText(R.string.txt_not_support)
        }

        bottomSheetDialogLoading.show()
    }
}

