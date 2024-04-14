package com.tes.presentation.mypage

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal fun PrivacyPolicyWebView(){
    val webView = rememberWebView()
}

@Composable
fun rememberWebView(): WebView{
    val context = LocalContext.current
    val webView = remember{
        WebView(context).apply{
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl("https://sites.google.com/view/vodle/%ED%99%88")
        }
    }
    return webView
}
