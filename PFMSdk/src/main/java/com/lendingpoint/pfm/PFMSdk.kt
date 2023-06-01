package com.lendingpoint.pfm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import java.lang.ref.WeakReference

object PFMSdk {
    private var contextRef: WeakReference<Context>? = null

    fun open(context: Context, partnerToken: String, userToken: String? = null, partnerURL: String) {
        contextRef = WeakReference(context)
        val intent = Intent(context, PFMSdkActivity::class.java)
        intent.putExtra("partnerToken", partnerToken)
        intent.putExtra("userToken", userToken)
        intent.putExtra("partnerURL", partnerURL)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val partnerToken = intent.getStringExtra("partnerToken")
        val partnerURL = intent.getStringExtra("partnerURL")
        val userToken = intent.getStringExtra("userToken")
        val url = "https://pfm-ten.vercel.app/?partnerURL=$partnerURL&partnerToken=$partnerToken&userToken=$userToken"

        webView = WebView(this)
        setContentView(webView)
        val webViewSettings = webView.settings
        webViewSettings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
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