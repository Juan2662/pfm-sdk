package com.lendingpoint.pfm

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

object PFMSdk {
    private var contextRef: WeakReference<Context>? = null

    fun show(context: Context, partnerURL: String, partnerToken: String, userToken: String? = null) {
        contextRef = WeakReference(context)
        val intent = Intent(context, PFMSdkActivity::class.java)
        intent.putExtra("partnerURL", partnerURL)
        intent.putExtra("partnerToken", partnerToken)
        intent.putExtra("userToken", userToken)
        context.startActivity(intent)
    }

    fun close() {
        val context = contextRef?.get()
        if(context is Activity){
            context.finish()
        }
    }
}

class PFMSdkActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pfmsdk_layout)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        val partnerToken = intent.getStringExtra("partnerToken")
        val partnerURL = intent.getStringExtra("partnerURL")
        val userToken = intent.getStringExtra("userToken")
        val url = "https://pfm-ten.vercel.app/?partnerURL=$partnerURL&partnerToken=$partnerToken&userToken=$userToken"

        val webViewSettings = webView.settings
        webViewSettings.javaScriptEnabled = true
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