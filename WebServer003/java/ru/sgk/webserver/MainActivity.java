package ru.sgk.webserver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

import ru.sgk.webserver.AndroidLib.Android;
import ru.sgk.webserver.AndroidLib.WebServerAndroid;
import ru.sgk.webserver.Lib.LockOrientation;

public class MainActivity extends Activity {

    private WebView webView ;
    private Android andoid;
    private WebServerAndroid webServerAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        webView= (WebView) findViewById(R.id.webView);
        webView.getSettings().setUserAgentString("Desktop");
        new LockOrientation(this).lock();

        webView.getSettings().setJavaScriptEnabled(true);
        andoid=new Android(this,webView);
        webView.addJavascriptInterface(andoid, "Android");

        webServerAndroid = new WebServerAndroid(this,webView);
        webView.addJavascriptInterface(webServerAndroid, "WebServer");

        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(true);
        webView.loadUrl("file:///android_asset/html/index.html");

        WebServerAndroid.AppPacedjName=getApplicationContext().getPackageName();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) &&webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }



    public static final int READ_REQUEST_CODE = 42;


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == MainActivity.READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Log.i(TAG, "Uri: " + uri.toString());
                // showImage(uri);
                Toast.makeText(this, uri.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static void chooseFile(MainActivity parentActivity ,String SelectDir) {


        // Intent intent = new Intent();
        // intent.setAction(Intent.ACTION_PICK);

        // Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT);

        if (SelectDir.length()==0){
            String ApplicationName=parentActivity.getApplicationInfo().loadLabel(parentActivity.getPackageManager()).toString();
            SelectDir= Environment.getExternalStorageDirectory()+File.separator+ApplicationName+File.separator;
        }

        File folder=new File(SelectDir);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        Uri startDir = Uri.fromFile(folder);
        intent.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.file");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra("browser_filter_extension_whitelist", "*.*");
        intent.putExtra("explorer_title", "Open File...");
        intent.putExtra("browser_title_background_color", "440000AA");
        intent.putExtra("browser_title_foreground_color", "FFFFFFFF");
        intent.putExtra("browser_list_background_color", "66000000");
        intent.putExtra("browser_list_fontscale", "120%");
        intent.putExtra("browser_list_layout", "2");
  //      parentActivity.startActivityForResult(intent,  READ_REQUEST_CODE);
        parentActivity.startActivityForResult(Intent.createChooser(intent, "File Browser"), MainActivity.READ_REQUEST_CODE);

        /*
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.fromFile(new File(folder.getPath()))); // uri of directory from FileProvider, DocumentProvider or uri with file scheme, can be empty.
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("location", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        //  intent.putExtra("org.openintents.extra.ABSOLUTE_PATH", folder.getAbsolutePath()); // String
        if (intent.resolveActivity(parentActivity.getPackageManager()) != null) {
            parentActivity.startActivity(intent);
        }

        /*
     //    Intent intent = new Intent(Intent.ACTION_VIEW);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setData(Uri.fromFile(new File(folder.getPath())));
        intent.putExtra("org.openintents.extra.ABSOLUTE_PATH", folder.getPath());

        if (intent.resolveActivity( parentActivity.getPackageManager()) != null) {
            parentActivity.startActivity(intent);
        }
            /*
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()  + "/myFolder/");
        Uri uri = Uri.parse(SelectDir);
        intent.setDataAndType(uri, "resource/folder");
        parentActivity.startActivity(Intent.createChooser(intent, "Open folder"));

       /*
        if (SelectDir.length()==0){
            String ApplicationName=parentActivity.getApplicationInfo().loadLabel(parentActivity.getPackageManager()).toString();
            SelectDir= Environment.getExternalStorageDirectory()+File.separator+ApplicationName+File.separator;
        }
        parentActivity.startActivityForResult(Intent.createChooser(intent, SelectDir), 123);

        Uri startDir = Uri.fromFile(new File(SelectDir));
        intent.setDataAndType(startDir,"vnd.android.cursor.dir/lysesoft.andexplorer.file");


       //  intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            parentActivity.startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    READ_REQUEST_CODE);

        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(parentActivity, "Please install a File Manager.",          Toast.LENGTH_SHORT).show();
        }
//        Intent intent = new Intent(getActivity(), NormalFilePickActivity.class);
//        intent.putExtra(Constant.MAX_NUMBER, 1);
//        intent.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
//        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
      */
    }



    public void openFolder()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()  + "/myFolder/");
        intent.setDataAndType(uri, "text/csv");
        startActivity(Intent.createChooser(intent, "Open folder"));
    }

}
