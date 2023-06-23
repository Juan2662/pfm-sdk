package com.lendingpoint.pfm

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import java.lang.ref.WeakReference

data class Body(
    val partnerURL: String,
    val partnerToken: String,
    val userToken: String? = null,
    val userUUID: String? = null
)
object PFMSdk {
    private var contextRef: WeakReference<Context>? = null

    fun show(context: Context, body: Body) {
        contextRef = WeakReference(context)
        val intent = Intent(context, PFMSdkActivity::class.java)

        if (!body.partnerURL.startsWith("https://") && !body.partnerToken.isNullOrEmpty() ) {
            return
        }

        intent.putExtra("partnerURL", body.partnerURL)
        intent.putExtra("partnerToken", body.partnerToken)
        intent.putExtra("userToken", body.userToken)
        intent.putExtra("userUUID", body.userUUID)
        context.startActivity(intent)
    }

    fun hide() {
        val context = contextRef?.get()
        if(context is Activity){
            context.finish()
        }
    }
}

class PFMSdkActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pfmsdk_layout)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        val partnerToken = intent.getStringExtra("partnerToken")
        val partnerURL = intent.getStringExtra("partnerURL")
        val userToken = intent.getStringExtra("userToken")
        val userUUID = intent.getStringExtra("userUUID")
        val url = "$partnerURL?partnerToken=$partnerToken" + if (!userToken.isNullOrEmpty()) "&userToken=$userToken" else "" + if (!userUUID.isNullOrEmpty()) "&userUUID=$userUUID" else ""

        val webViewSettings = webView.settings
        webViewSettings.javaScriptEnabled = true
        webView.clearCache(true)
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack()
        }else {
            finish()
        }
    }

}