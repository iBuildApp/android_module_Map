/****************************************************************************
*                                                                           *
*  Copyright (C) 2014-2015 iBuildApp, Inc. ( http://ibuildapp.com )         *
*                                                                           *
*  This file is part of iBuildApp.                                          *
*                                                                           *
*  This Source Code Form is subject to the terms of the iBuildApp License.  *
*  You can obtain one at http://ibuildapp.com/license/                      *
*                                                                           *
****************************************************************************/
package com.ibuildapp.romanblack.MapPlugin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.appbuilder.sdk.android.AppBuilderModule;
import com.ibuildapp.romanblack.MapPlugin.R;

/**
 * This Activity shows user route from user location to selected destination map point.
 */
public class MapRoute extends AppBuilderModule {

    private final int INITIALIZATION_FAILED = 0;
    private final int NEED_INTERNET_CONNECTION = 1;
    private final int SHOW_PROGRESS = 2;
    private final int HIDE_PROGRESS = 3;
    private final int LOADING_ABORTED = 5;
    private String routeUrl = "";
    private WebView webView = null;
    private ProgressDialog progressDialog = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case INITIALIZATION_FAILED: {
                    Toast.makeText(MapRoute.this, R.string.romanblack_map_alert_cannot_init_route,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 5000);
                }
                break;
                case NEED_INTERNET_CONNECTION: {
                    Toast.makeText(MapRoute.this, R.string.alert_no_internet,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 5000);
                }
                break;
                case SHOW_PROGRESS: {
                    showProgress();
                }
                break;
                case HIDE_PROGRESS: {
                    hideProgress();
                }
                break;
                case LOADING_ABORTED: {
                    closeActivity();
                }
                break;
            }
        }
    };

    @Override
    public void create() {
        try {//ErrorLogging


            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.romanblack_mapweb_route);
            setTitle(R.string.romanblack_map_route_upper);

            Intent currentIntent = getIntent();
            Bundle bundle = currentIntent.getExtras();
            routeUrl = bundle.getString("url");

            if (routeUrl.length() == 0) {
                handler.sendEmptyMessage(INITIALIZATION_FAILED);
            }

            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnectedOrConnecting()) {
            } else {
                handler.sendEmptyMessage(NEED_INTERNET_CONNECTION);
            }

            webView = (WebView) findViewById(R.id.romanblack_mapweb_route_webview);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setGeolocationEnabled(true);
//            webView.getSettings().setPluginsEnabled(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.clearHistory();
            webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if (progressDialog != null) {
                        if (!progressDialog.isShowing()) {
                            handler.sendEmptyMessage(SHOW_PROGRESS);
                        }
                    } else {
                        handler.sendEmptyMessage(SHOW_PROGRESS);
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    handler.sendEmptyMessage(HIDE_PROGRESS);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.contains("google") && url.contains("maps")) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });

            webView.loadUrl(routeUrl);


        } catch (Exception e) {
        }
    }

    private void showProgress() {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.common_loading_upper), true);
    }

    private void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void closeActivity() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        finish();
    }
}
