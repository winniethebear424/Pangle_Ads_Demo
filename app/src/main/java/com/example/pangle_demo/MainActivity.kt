package com.example.pangle_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdInteractionListener
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdLoadListener
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialRequest
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import com.bytedance.sdk.openadsdk.api.init.PAGConfig
import com.bytedance.sdk.openadsdk.api.init.PAGSdk
import com.example.pangle_demo.ui.theme.Pangle_DemoTheme

class MainActivity : ComponentActivity() {

    private val TAG = "PangleAdsDemo"
    private val APP_ID = "8025677"
    private val PLACEMENT_ID = "980088188"
    private var fullScreenAd: PAGInterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//  // Initialize Pangle SDK
        initPangleSdk()

        // Set up load ad button
        enableEdgeToEdge()
        setContent {
            Pangle_DemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AdScreen(
                        onLoadAdClick = {
                            loadInterstitialAd()
                        }
                    )
                }
            }
        }
    }

    private fun initPangleSdk() {
        // Create PAG config
        val config = PAGConfig.Builder()
            .appId(APP_ID)
            .debugLog(true) // Enable debug logs for testing
            .build()

        // Initialize SDK
        PAGSdk.init(this, config, object : PAGSdk.PAGInitCallback {
            override fun success() {
                Log.d("pangle", "Pangle SDK initialized successfully")
                Toast.makeText(
                    this@MainActivity,
                    "SDK initialized successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun fail(code: Int, message: String) {
                Log.e("pangle", "Pangle SDK initialization failed: $code, $message")
                Toast.makeText(
                    this@MainActivity,
                    "SDK initialization failed: $message",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadInterstitialAd() {
        // Create ad request
        val adRequest = PAGInterstitialRequest()

        // Load interstitial ad
        PAGInterstitialAd.loadAd(
            PLACEMENT_ID,
            adRequest,
            object : PAGInterstitialAdLoadListener {
                override fun onError(code: Int, message: String) {
                    Log.e("pangle", "Failed to load interstitial ad: $code, $message")
                    Toast.makeText(
                        this@MainActivity,
                        "Ad load failed: $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(pagInterstitialAd: PAGInterstitialAd) {
                    Log.d("pangle", "Interstitial ad loaded successfully")
                    Toast.makeText(this@MainActivity, "Ad loaded successfully", Toast.LENGTH_SHORT)
                        .show()
                    fullScreenAd = pagInterstitialAd

                    // Show the ad immediately after it's loaded
                    showFullScreenAd()
                }
            }
        )
    }

    private fun showFullScreenAd() {
        fullScreenAd?.let { ad ->
            ad.setAdInteractionListener(object : PAGInterstitialAdInteractionListener {
                override fun onAdShowed() {
                    Log.d(TAG, "Interstitial ad showed")
                }

                override fun onAdClicked() {
                    Log.d(TAG, "Interstitial ad clicked")
                }

                override fun onAdDismissed() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    fullScreenAd = null
                }
            })

            // Show the ad
            ad.show(this)
        } ?: run {
            Log.e(TAG, "Interstitial ad not loaded yet")
            Toast.makeText(this, "Ad not loaded yet", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun AdScreen(onLoadAdClick: () -> Unit) {

    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        androidx.compose.material3.Button(
            onClick = {
                if (!isLoading) {
                    isLoading = true
                    onLoadAdClick()
                }
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                Text("Loading...")
            } else {
                Text("Load Fullscreen Ad")
            }
        }
    }
}


