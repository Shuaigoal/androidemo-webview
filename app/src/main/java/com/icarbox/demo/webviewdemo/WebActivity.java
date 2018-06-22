package com.icarbox.demo.webviewdemo;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Set;

public class WebActivity extends AppCompatActivity {
    private WebView webView;
    private Button next;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_web);

        webView = (WebView) findViewById(R.id.webView);
        next = (Button) findViewById(R.id.next);

        initEvent();
        initData();

        //保存状态，否则加载
        if(savedInstanceState!=null){
            webView.restoreState(savedInstanceState);
        }else {
            webView.loadUrl("file:///android_asset/demo.html");
        }
    }


    private void initEvent() {
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                //loadUrl本地调用js方法，会刷新web页面
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//                    //webView.loadUrl("javascript:sayHello()");
                    webView.loadUrl("javascript:getContent()");
//                    return;
//                }
                //Android 4.4 后才可使用，可获取js function返回的值，不会刷新页面
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//                webView.evaluateJavascript("javascript:getName()", new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String value) {
//                        Log.e("web",value);
//                        Toast.makeText(getApplicationContext(),"直接调用JS函数获得的返回内容\n"+value, Toast.LENGTH_LONG).show();
//                    }
//                });
            }
        });
    }

    private void initData() {
        //关于Android硬件加速 开始于Android 3.0 (API level 11),开启硬件加速后，WebView渲染页面更加快速，
        // 拖动也更加顺滑。但有个副作用就是容易会出现页面加载白块同时界面闪烁现象。
        // 解决这个问题的方法是设置WebView暂时关闭硬件加速 代码如下：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        WebSettings set = webView.getSettings();
        //设置webview支持js
        set.setJavaScriptEnabled(true);
        //设置本地调用对象及其接口
        webView.addJavascriptInterface(new JsInteraction(), "android");

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.e("progress",""+newProgress);
                //加载完网页才可以进行操作,比如按钮点击
                //todo
            }
        });
    }

    /**
     * 供网页JS可使用的接口
     */
    public class JsInteraction {
        @JavascriptInterface
        public void content(String message) {   //提供给js调用的方法
            if(message!=null && message.length()>0){
                UserInfo userInfo = new Gson().fromJson(message,UserInfo.class);
                //内容为空提示
                if (userInfo.gender==null||userInfo.gender.length()<1){
                    Toast.makeText(getApplicationContext(), "请选择性别", Toast.LENGTH_LONG).show();
                    return;
                }
                if (userInfo.name==null||userInfo.name.length()<1){
                    Toast.makeText(getApplicationContext(), "请输入姓名", Toast.LENGTH_LONG).show();
                    return;
                }
                //内容合法，进行下一步操作
                Toast.makeText(getApplicationContext(),"成功获取到用户输入的数据\n"+ message, Toast.LENGTH_LONG).show();
                Log.e("json",new Gson().toJson(userInfo));
            }
        }
    }

    class UserInfo{
        String name;
        String gender;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        webView.saveState(outState);
    }
}



// webView.setWebChromeClient(new WebChromeClient(){
//@Override
//public void onProgressChanged(WebView view, int newProgress) {
//        Log.e("progress",""+newProgress);
//        //加载完网页才可以进行操作
//        //todo
//        }
//            // 拦截输入框(原理同方式2)
//            // 参数message:代表promt（）的内容（不是url）
//            // 参数result:代表输入框的返回值
//            @Override
//            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                // 根据协议的参数，判断是否是所需要的url(原理同方式2)
//                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
//                //假定传入进来的 url = "js://webview?content=111"（同时也是约定好的需要拦截的）
//                Uri uri = Uri.parse(message);
//                // 如果url的协议 = 预先约定的 js 协议，就解析往下解析参数
//                if ( uri.getScheme().equals("js")) {
//                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
//                    // 所以拦截url,下面JS开始调用Android需要的方法
//                    if (uri.getAuthority().equals("webview")) {
//                        // 执行JS所需要调用的逻辑
//                        System.out.println("js调用了Android的方法");
//                        // 可以在协议上带有参数并传递到Android上
//                        HashMap<String, String> params = new HashMap<>();
//                        Set<String> collection = uri.getQueryParameterNames();
//                        System.out.println(collection+"");
//                        //参数result:代表消息框的返回值(输入值)
//                        result.confirm("js调用了Android的方法成功啦");
//                    }
//                    return true;
//                }
//                return super.onJsPrompt(view, url, message, defaultValue, result);
//            }
