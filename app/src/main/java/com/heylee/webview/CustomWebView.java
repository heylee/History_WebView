package com.heylee.webview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebView extends Activity {

    private WebView webview;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        url = intent.getExtras().getString("url");

        setContentView(R.layout.web_view);
        
        webview = findViewById(R.id.webview);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
        webview.loadUrl(url);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(webview.canGoBack()) {
                webview.goBack();
            }else {
               this.finish();
            }
        }
        return true;
    }


    public class MyWebViewClient extends WebViewClient {
        public static final String INTENT_URI_START = "intent:";
        public static final String INTENT_FALLBACK_URL = "browser_fallback_url";
        public static final String URI_SCHEME_MARKET = "market://details?id=";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String uri) {
//            if (uri.toLowerCase().startsWith(INTENT_URI_START)) {
//                Intent parsedIntent = null;
//                try {
//                    parsedIntent = Intent.parseUri(uri, 0);
//                    startActivity(parsedIntent);
//                } catch(ActivityNotFoundException | URISyntaxException e) {
//                    return doFallback(view, parsedIntent);
//                }
//            } else {
//                view.loadUrl(uri);
//            }
            return false;
        }

        public boolean doFallback(WebView view, Intent parsedIntent) {
//            if (parsedIntent == null) {
//                return false;
//            }
//            String fallbackUrl = parsedIntent.getStringExtra(INTENT_FALLBACK_URL);
//            if (fallbackUrl != null) {
//                view.loadUrl(fallbackUrl);
//                return true;
//            }
//
//            String packageName = parsedIntent.getPackage();
//            if (packageName != null) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_SCHEME_MARKET + "'packagename'")));
//                return true;
//            }
            return false;
        }
    }
}
