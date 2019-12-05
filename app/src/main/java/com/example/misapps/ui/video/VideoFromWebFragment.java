package com.example.misapps.ui.video;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.misapps.MainActivity;
import com.example.misapps.R;

public class VideoFromWebFragment extends Fragment {


    private String url;
    private WebView webView;

    public VideoFromWebFragment(String url) {
        this.url = url;
    }

    public VideoFromWebFragment(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.video_from_web_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        ((MainActivity) getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        llamarVistas();
        reproducirVideo();

    }

    private void llamarVistas(){
        this.webView = (WebView)getView().findViewById(R.id.webViewYoutube);

    }

    private void reproducirVideo(){

        String html= "<html><body><iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/watch?v=hHW1oY26kxQ\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String ruta){
                return false;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadData(html, "text/html","utf-8");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).getSupportActionBar().show();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }



}
