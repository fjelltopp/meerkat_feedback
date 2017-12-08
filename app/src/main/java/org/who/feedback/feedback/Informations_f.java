package org.who.feedback.feedback;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by A-Souhei on 6/15/2017.
 */

public class Informations_f extends Fragment {
    private static String TAG = "informations";

    public String deviceid;
    public String sims_sn_number;
    public String simid;
    String customURL = getResources().getString(R.string.informations_url);


    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //String imeistring = telephonyManager.getDeviceId();

        try {
            this.deviceid = telephonyManager.getDeviceId() == null ? "UNRESOLVED" : telephonyManager.getDeviceId();
            this.simid = telephonyManager.getSubscriberId() == null ? "UNRESOLVED" : telephonyManager.getSubscriberId();
            this.sims_sn_number = telephonyManager.getSimSerialNumber() == null ? "UNRESOLVED" : telephonyManager.getSimSerialNumber();
        } catch (SecurityException ex) {
            Log.e(TAG, "Unavailable device permissions: " + ex.getMessage());
        }

        rootView = inflater.inflate(R.layout.informations_f, container, false);

        MyTask task = new MyTask();
        task.execute(customURL);


        return rootView;
    }

    private class MyTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =  (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                System.out.println(con.getResponseCode());
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;
            if (bResponse==true)
            {
                Toast.makeText(getActivity(), getResources().getString(R.string.loading_toast), Toast.LENGTH_SHORT).show();

                WebView mWebView = (WebView) rootView.findViewById(R.id.webview_inf);

                mWebView.loadUrl(customURL + deviceid + "/" + sims_sn_number);

                // Enable Javascript
                WebSettings webSettings = mWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                // Force links and redirects to open in the WebView instead of in a browser
                mWebView.setWebViewClient(new WebViewClient());

            }
            else
            {
                Toast.makeText(getActivity(), getResources().getString(R.string.loading_error_toast), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
