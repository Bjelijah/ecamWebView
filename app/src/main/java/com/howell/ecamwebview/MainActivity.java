package com.howell.ecamwebview;

import android.annotation.SuppressLint;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class MainActivity extends AppCompatActivity {
    private Toolbar mTb;
    private WebView mWebView;
    private String mErrorHtml = "";
    private View myView = null;
    private WebChromeClient.CustomViewCallback myCallback = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("123","on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initToobar();
        initFun();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("AddJavascriptInterface")
    private void initView(){
        mWebView = (WebView) findViewById(R.id.web_main);
        mWebView.loadUrl("http://116.228.67.70:8800/");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.requestFocus();
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.addJavascriptInterface(new MyWebClickCallback(),"demo");

//        mWebView.addJavascriptInterface(new JsObject(), "injectedObject");
//        mWebView.loadData("", "text/html", null);
//        mWebView.loadUrl("javascript:alert(injectedObject.toString())");

    }

    private void addClickListner(){
//        mWebView.loadData("", "text/html", null);
//        mWebView.loadUrl("javascript:function(){"+
//                "alert(\" test \");"+
//                "var objs = document.getElementById(\"divVideo\");"+
//                "objs.onclick=function(){"+
//                "window.webViewCallback.onWebClick();"+
//                "};"+
//                "}"+""
//        );
        String url= "javascript:function android_fun(){" +

                        "window.demo.onWebPrint(\" 1111 \");"+
//                        "var objs = document.getElementById(\"divVideo\");"+
                        "var objs=document.getElementsByClassName(\"col-md-9\");"+
//                        "objs.style.cursor = \'pointer\'"+
//                        "var obj=objs.getElementById(\"divVideo\");"+
                        "window.demo.onWebPrint(\" 22222 \");"+
                        "window.demo.onWebPrint(objs.toString());"+

//                        "alert(obj)"+
                        "objs[0].onclick=function(){"+
                            "window.demo.onWebClick();"+
                            "alert(\" test \")"+
                        "};"+
                    "}"+
                    "android_fun()";

        String url3 = "javascript:function androidf(){"+
                "alert(\" test \")"+
                "}";

        mWebView.loadUrl(url);
//        String url2 = "javascript:android_fun()";
//        mWebView.loadUrl(url2);
    }

    public class MyWebClickCallback{
        @JavascriptInterface
        public void onWebClick(){
            Log.i("123","web click");
        }
        @JavascriptInterface
        public void onWebPrint(String msg){
            Log.i("123",msg);
        }
    }



    private void initToobar(){
        mTb = (Toolbar) findViewById(R.id.web_toolbar);
        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));
        mTb.setTitle("皓维数据中心");
        setSupportActionBar(mTb);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        mTb.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mWebView.goBack();
            }
        });
        mTb.setVisibility(View.GONE);
    }
//divVideo





    private void initFun(){
        mErrorHtml = "<html><body><h1>Page not find！</h1></body></html>";
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
//land
        }
        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
//port
        }
    }




    class JsObject {
        @JavascriptInterface
        public String toString() { return "injectedObject"; }
    }

    class MyWebViewClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.loadUrl(request.getUrl().toString());
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            Log.i("123","onpage finished");
            addClickListner();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            view.loadData(mErrorHtml,"test/html","UTF-8");
        }
    }

    class MyWebChromeClient extends WebChromeClient{

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Log.i("123","on Show custom view");
//            super.onShowCustomView(view, callback);
            if (myCallback!=null){
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            long id = Thread.currentThread().getId();
            Log.i("123","thread id="+id);

            ViewGroup parent = (ViewGroup) mWebView.getParent();
            String s = parent.getClass().getName();
            Log.i("123","parent name="+s);
            view.setBackgroundColor(getResources().getColor(R.color.black));

            parent.removeView(mWebView);
            parent.addView(view);
            myView = view;
            myCallback = callback;

            setFullScreen();

        }

        @Override
        public void onHideCustomView() {
           hideCustomViewFun();
        }
    }

    private void hideCustomViewFun(){
        if (myView==null)return;
        if (myCallback!=null){
            myCallback.onCustomViewHidden();
            myCallback = null;
        }
        ViewGroup parent = (ViewGroup) myView.getParent();
        parent.removeView(myView);
        parent.addView(mWebView);
        myView = null;
        quitFullScreen();
    }

    private void setFullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }



    }
    private void quitFullScreen(){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &=(~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }



}
