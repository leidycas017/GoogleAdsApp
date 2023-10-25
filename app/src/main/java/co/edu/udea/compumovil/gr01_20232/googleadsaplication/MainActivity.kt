package co.edu.udea.compumovil.gr01_20232.googleadsaplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import co.edu.udea.compumovil.gr01_20232.googleadsaplication.ui.theme.GoogleAdsAplicationTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : ComponentActivity() {

    private var mInterstitialAd: InterstitialAd? = null
    private var adId: String = "ca-app-pub-3940256099942544/1033173712"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleAdsAplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var adStatus by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            if (adStatus) {
                                showInterstitialAd()
                                adStatus = false
                            } else {
                                loadInterstitialAd {
                                    adStatus = it
                                }
                            }
                        }) {
                            if (adStatus) Text(text = "Load AD") else Text(text = "Show Ad")
                        }
                    }

                    MobileAds.initialize(this)
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ){
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth(),
                            factory = { context ->
                                AdView(context).apply {
                                    setAdSize(AdSize.LARGE_BANNER)
                                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                                    loadAd(AdRequest.Builder().build())
                                }
                            }
                        )
                    }
                }
            }


        }
    }

    private fun loadInterstitialAd(adStatus: (Boolean) -> Unit){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, adId, adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(error:LoadAdError){
                super.onAdFailedToLoad(error)
                mInterstitialAd = null
                Log.i("AD_TAG", "onAdFailedToLoad: ${error.message}")
                adStatus.invoke(false)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd){
                super.onAdLoaded(interstitialAd)
                mInterstitialAd = interstitialAd
                Log.i("AD_TAG", "onAdLoaded: " )
                adStatus.invoke(true)
            }
        })
    }


    private fun showInterstitialAd(){
        mInterstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent(){
                    super.onAdDismissedFullScreenContent()
                    Log.i("AD_TAG", "onAdDismissedFullScreenContent: ")
                    mInterstitialAd = null
                }

                override fun onAdImpression(){
                    super.onAdImpression()
                    Log.i("AD_TAG", "onAdImpression: ")
                }
                override fun onAdClicked(){
                    super.onAdClicked()
                }
            }
            ad.show(this)
        }?: kotlin.run{
            Toast.makeText(this, "Ad is null", Toast.LENGTH_SHORT).show()
        }
    }



}





