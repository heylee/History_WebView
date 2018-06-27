package com.heylee.webview;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private String[] urlList = {
            "https://naver.com",
            "https://nate.com",
            "https://google.com" };

    private static final String INIT_URL = "https://naver.com";
    private ArrayList<String> mHistoryItems = new ArrayList<>();
    private ArrayList<String> mStaticItems = new ArrayList<>();
    private SimpleHeaderRecyclerAdapter mHistoryAdapter;
    private SimpleHeaderRecyclerAdapter mStaticAdapter;

    private TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String saveUrl = Prefs.getString(MainActivity.this, Prefs.keys.URL, INIT_URL);

        final Button btnGoWebView = findViewById(R.id.main_web_view);
        textInputLayout = findViewById(R.id.textInputLayout);


        final TextView tvAdd = findViewById(R.id.add);

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.input_info), Snackbar.LENGTH_LONG).setAction(getString(R.string.input), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, getString(R.string.revert_info), Toast.LENGTH_SHORT).show();
                        showFileChooser();
                    }
                }).show();
            }
        });

        tvAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Snackbar.make(view, getString(R.string.revert_do), Snackbar.LENGTH_LONG).setAction(getString(R.string.revert), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Prefs.putString(MainActivity.this, Prefs.keys.CUSTOM_URL, "");
                        mStaticItems.clear();
                        mStaticItems = new ArrayList<>(Arrays.asList(urlList));

//                        Log.d("heylee","mStaticItems"+mStaticItems.size());
                        if (mStaticAdapter != null) {
                            mStaticAdapter.setData(mStaticItems);
                            mStaticAdapter.notifyDataSetChanged();
                        }
                    }
                }).show();
                return true;
            }
        });

        checkCustomAndSet(); // Data Setting
        setInitListView(); // ListView Init

        textInputLayout.getEditText().setText(saveUrl);
        textInputLayout.getEditText().setSelection(textInputLayout.getEditText().getText().length());

        btnGoWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String moveUrl = textInputLayout.getEditText().getText().toString();

                String history = Prefs.getString(MainActivity.this, Prefs.keys.HISTORY, "");
                StringBuilder sb = new StringBuilder();
                sb.append(history);
                if (history.length() > 0) {
                    sb.append("ꋂ");
                }
                sb.append(moveUrl);

                Prefs.putString(MainActivity.this, Prefs.keys.URL, moveUrl);
                Prefs.putString(MainActivity.this, Prefs.keys.HISTORY, sb.toString());

                setHistoryData();

                Intent intent = new Intent(MainActivity.this, CustomWebView.class);
                intent.putExtra("url", textInputLayout.getEditText().getText().toString());
                startActivity(intent);
            }
        });
    }

    /**
     * ListView Init
     */
    private void setInitListView(){

        //------- rv History [ ------
        RecyclerView rvHistory = findViewById(R.id.recyclerview);


        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 3;
            }
        });

        rvHistory.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvHistory.setLayoutManager(mLayoutManager);
        rvHistory.setHasFixedSize(true);

        mHistoryAdapter = new SimpleHeaderRecyclerAdapter(this, mHistoryItems, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TextView) {
                    String url = ((TextView) view).getText().toString();
                    textInputLayout.getEditText().setText(url);
                    textInputLayout.getEditText().setSelection(textInputLayout.getEditText().getText().length());
                }
            }
        });
        rvHistory.setAdapter(mHistoryAdapter);
        //------- ]rv History ------

        //------- rv Static [ ------
        RecyclerView rvStaticUrl = findViewById(R.id.rv_static_url);
        GridLayoutManager mLMURL = new GridLayoutManager(this, 3);
        mLMURL.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 3;
            }
        });

        rvStaticUrl.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvStaticUrl.setLayoutManager(mLMURL);
        rvStaticUrl.setHasFixedSize(true);

        mStaticAdapter = new SimpleHeaderRecyclerAdapter(this, mStaticItems, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TextView) {
                    String url = ((TextView) view).getText().toString();
                    textInputLayout.getEditText().setText(url);
                    textInputLayout.getEditText().setSelection(textInputLayout.getEditText().getText().length());
                }
            }
        });
        rvStaticUrl.setAdapter(mStaticAdapter);
        //------- ] rv Static  ------
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHistoryData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        Intent intent;
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    getFileString(data.getData());
                }
                break;
        }
    }

    private void setHistoryData() {

        String history = Prefs.getString(MainActivity.this, Prefs.keys.HISTORY, "");
        ArrayList<String> mThum = new ArrayList<String>(Arrays.asList(history.split("ꋂ")));
        Collections.reverse(mThum);
        mHistoryItems.clear();

        if (mThum != null) {
            for (String str : mThum) {
                if (!"".equals(str)) {
                    mHistoryItems.add(str);
                }
            }
        }
        if (mHistoryAdapter != null) {
            mHistoryAdapter.notifyDataSetChanged();
        }
    }


    private static final int FILE_SELECT_CODE = 0;

    /**
     *  OS File Chooser Call
     */
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Read Text to File
     * @param uri  File uri
     */
    public void getFileString(Uri uri) {
        Log.d("heylee", "uri " + uri);
        if (uri == null) {
            return;
        }
        StringBuilder text = new StringBuilder();

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                Log.d("heylee", "line : " + line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("heylee", "text " + text.toString());
        Prefs.putString(MainActivity.this, Prefs.keys.CUSTOM_URL, text.toString());
        checkCustomAndSet();
    }

    /**
     *
     * User Direct Input Url List Check.
     *
     * @return
     */
    public boolean checkCustomAndSet() {
        String strr = Prefs.getString(MainActivity.this, Prefs.keys.CUSTOM_URL, "");
        if (!"".equals(strr)) {
            Log.d("heylee", "!!! strr " + strr);
            String custom_url = Prefs.getString(MainActivity.this, Prefs.keys.CUSTOM_URL, "");
            ArrayList<String> mThum = new ArrayList<String>(Arrays.asList(custom_url.split("\n")));
//            Collections.reverse(mThum);
            mStaticItems.clear();

            if (mThum != null) {
                for (String str : mThum) {
                    if (!"".equals(str)) {
                        Log.d("heylee", "!!! add " + str);
                        mStaticItems.add(str);
                    }
                }
            }
            if (mStaticAdapter != null) {
                mStaticAdapter.setData(mStaticItems);
                mStaticAdapter.notifyDataSetChanged();
            }

            return true;
        }else{
            mStaticItems = new ArrayList<>(Arrays.asList(urlList));
        }
        return false;
    }

}
