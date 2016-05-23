package com.ddg.xwalkpdfexample;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.championswimmer.sfg.lib.SimpleFingerGestures;
import rx.subscriptions.CompositeSubscription;

public class PDFFragment extends Fragment implements  MyXwalkView.TouchEventListener {

    public static final String TAG = PDFFragment.class.getSimpleName();

    @Bind(R.id.currentPage)
    TextView mPage;
    @Bind(R.id.webView)
    MyXwalkView webView;

    private int currentpage;
    private int mPageCount;
    private File pdfFile;
    private SimpleFingerGestures mySFG;
    public MainActivity mActivity;
    private Menu mMenu;
    private CompositeSubscription mSubscription;
    private String html;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
       // mMenu.clear();
        Log.d(TAG, "onCreateOptions");
        inflater.inflate(R.menu.pdf_menu, mMenu);
        initSearch();
    }

    private void initSearch() {
        MenuItem searchItem = mMenu.findItem(R.id.menu_item_search);
        Log.d(TAG, "menu item " + searchItem);

        SearchManager searchManager = (SearchManager) mActivity.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        Log.d(TAG, "search view null - " + (searchView == null));
        if (searchView != null) {
            SearchableInfo si = searchManager.getSearchableInfo(mActivity.getComponentName());

            int options = searchView.getImeOptions();
            searchView.setImeOptions(options | EditorInfo.IME_FLAG_NO_EXTRACT_UI);

            searchView.setSearchableInfo(si);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (TextUtils.isEmpty(newText)){
                        //showBookmarks(View.GONE);
                        Log.d(TAG, "change");
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    webView.load("javascript:findWord('" + query + "')", null);
                    Log.d(TAG, "Find all async " + query);
                    return false;
                }

            });
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pdf, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        mSubscription = new CompositeSubscription();

        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);

        webView.setUIClient(new XWalkUIClient(webView){
            @Override
            public boolean onJavascriptModalDialog(XWalkView view, JavascriptMessageType type, String url, String message, String defaultValue, XWalkJavascriptResult result) {
                switch (type){
                    case JAVASCRIPT_ALERT:{
                        Log.d("ALERT", message);
                        if(message.startsWith("_total")){
                            mPageCount = Integer.parseInt(message.substring(6));
                        }
                        if(message.startsWith("_cur")){
                            currentpage = Integer.parseInt(message.substring(4));
                        }
                        String page = currentpage + " / " + mPageCount;
                        mPage.setText(page);
                        result.confirm();
                    }
                }
                return true;
            }
        });

        mySFG = new SimpleFingerGestures();
        mySFG.setDebug(false);
        mySFG.setConsumeTouchEvents(true);
        mySFG.setOnFingerGestureListener(new SimpleFingerGestures.OnFingerGestureListener() {
            @Override
            public boolean onSwipeUp(int i, long l, double v) {
                Log.d(TAG, "onSwipeUp");
                return false;
            }

            @Override
            public boolean onSwipeDown(int i, long l, double v) {
                Log.d(TAG, "onSwipeDown " + " l " + l + " v " + v);
                return false;
            }

            @Override
            public boolean onSwipeLeft(int i, long l, double v) {
                Log.d(TAG, "left " + " l " + l + " v " + v);
                if(v>250){
                    webView.load("javascript:nextPage()", null);
                }

                return false;
            }

            @Override
            public boolean onSwipeRight(int i, long l, double v) {
                if(v>250){
                    webView.load("javascript:prevPage()", null);
                }
                Log.d(TAG, "onSwipeRight " + " l " + l + " v " + v);
                return false;
            }

            @Override
            public boolean onPinch(int i, long l, double v) {
                Log.d(TAG, "pinch");
                return false;
            }

            @Override
            public boolean onUnpinch(int i, long l, double v) {
                Log.d(TAG, "upPinch");
                return false;
            }

            @Override
            public boolean onDoubleTap(int i) {
                return false;
            }

        });





        try {
            pdfFile = new File(mActivity.getFilesDir(), "pdf.pdf");
            writeBytesToFile(mActivity.getAssets().open("pdf.pdf"), pdfFile);
            InputStream ims = mActivity.getAssets().open("index.html");
            html = convertStreamToString(ims).replace("reggggegggegeg", Uri.fromFile(pdfFile).toString());
            Log.d(TAG, "html " + html);

            webView.setTouchEventListener(this);
            webView.load(Uri.fromFile(pdfFile.getParentFile()).toString(), html);
            Log.d(TAG, "file EXIST " + pdfFile.exists());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




        return v;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("current", currentpage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        mSubscription.unsubscribe();
        super.onDestroyView();
    }

    @OnClick(R.id.next)
    void onNextClick(){
        webView.load("javascript:findNext()", null);
    }


    @Override
    public void onTouchEventInvoked(MotionEvent ev) {
        Log.d(TAG, "event + " + mySFG.onTouch(webView, ev));
    }


    public static void writeBytesToFile(InputStream input, File file) throws IOException {
        OutputStream output = new FileOutputStream(file);
        Log.d("READ", "start");
        try {
            try {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;

                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            } finally {
                output.close();
            }
        } catch (Exception e) {
            e.printStackTrace(); // handle exception, define IOException and others

        } finally {
            input.close();
        }
        Log.d("READ", "finish");
    }

    public static synchronized String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }


}
