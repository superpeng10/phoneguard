package com.android.pcc.phoneguard.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.pcc.phoneguard.R;
import com.android.pcc.phoneguard.db.AntivirusDao;
import com.android.pcc.phoneguard.util.MD5Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Administrator on 2016/11/17.
 */
public class AniVirusActivity extends Activity {
    private static final String TAG = "AniVirusActivity";
    private ImageView imageBack;
    private ImageView imageScanning;
    private ProgressBar pb;
    private TextView tv;
    private ScrollView scrollView;
    private LinearLayout linearLayout;

    private static final int BEGING =1;
    private static final int SCANNING =2;
    private static final int FINISH =3;

    private RotateAnimation rotateAnimation;
    private List<Scaninfo> scaninfos;
    private int count;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        initView();
        //copyDB("antivirus.db");
        initData();
        setAnimation();
    }
    private void initView(){
        imageBack = (ImageView)findViewById(R.id.topbarturn_antivirus);
        imageScanning = (ImageView)findViewById(R.id.iv_scanning);
        pb = (ProgressBar)findViewById(R.id.progressBar1);
        scrollView = (ScrollView)findViewById(R.id.scrollV);
        linearLayout = (LinearLayout)findViewById(R.id.ll_content);
        tv = (TextView)findViewById(R.id.tv_init_virus);
    }


    private void initData(){
        scaninfos = new ArrayList<>();
        count = 0;
        new Thread(){
            @Override
            public void run(){
                super.run();
                message = Message.obtain();
                message.what = BEGING;

                PackageManager manager = getPackageManager();
                List<PackageInfo> installpacakge = manager.getInstalledPackages(0);
                int size = installpacakge.size();
                pb.setMax(size);
                int progress = 0;
                for(PackageInfo packageInfo:installpacakge){
                    Scaninfo scaninfo = new Scaninfo();
                    scaninfo.appName= packageInfo.applicationInfo.loadLabel(manager).toString();
                    scaninfo.packageName = packageInfo.applicationInfo.className;
                    Log.d(TAG,"appname........."+scaninfo.appName);
                    Log.d(TAG,"packageName+++++++++++"+scaninfo.packageName);
                    String sourcedir = packageInfo.applicationInfo.sourceDir;
                    String md5 = MD5Utils.getFileMd5(sourcedir);
                   String desc = AntivirusDao.checkFileVirus(md5);
                    //String desc = null;
                    if(desc == null){
                        scaninfo.isVirus = false;
                    }else{
                        scaninfo.isVirus = true;
                        scaninfos.add(scaninfo);
                        count++;
                    }
                    progress++;
                    SystemClock.sleep(100);
                    pb.setProgress(progress);
                    message = Message.obtain();
                    message.what = SCANNING;
                    message.obj = scaninfo;
                    handler.sendMessage(message);


                }
                message = Message.obtain();

                message.what = FINISH;
                Log.d(TAG,"scanningggggggggggggggggggggggfinish");
                handler.sendMessage(message);
            }
        }.start();

    }
 Handler handler = new Handler() {
     @Override
     public void handleMessage(Message msg){
          switch(msg.what){
              case BEGING:
                  tv.setText("初始化...");
                  break;
              case SCANNING:
                  tv.setText("扫描中.........");
                  TextView textView = new TextView(AniVirusActivity.this);
                  Scaninfo scaninfo = (Scaninfo) message.obj;
                  if(scaninfo != null) {
                      if (scaninfo.isVirus) {
                          textView.setTextColor(Color.RED);
                          textView.setText(scaninfo.appName + "有病毒");
                      } else {
                          textView.setTextColor(Color.GRAY);
                          textView.setText(scaninfo.appName + "扫描安全");
                      }
                      linearLayout.addView(textView, 0);
                      scrollView.post(new Runnable() {
                          @Override
                          public void run() {
                              scrollView.fullScroll(scrollView.FOCUS_DOWN);
                          }
                      });
                  }
                  break;
              case FINISH:
                  tv.setText("扫描结束");
                  imageScanning.clearAnimation();
                  imageScanning.setVisibility(View.INVISIBLE);
                  showSelectDialog();
                  break;

          }
     }
 };
    private void setAnimation(){
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(3000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        imageScanning.setVisibility(View.VISIBLE);
        imageScanning.startAnimation(rotateAnimation);
    }
    static class Scaninfo{
        boolean isVirus;
        String appName;
        String packageName;
    }
    private void showSelectDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AniVirusActivity.this);
        dialog.setTitle("扫描结果");
        if(count == 0){
            dialog.setMessage("扫描结束，手机安全");
            dialog.setCancelable(false);
            dialog.setPositiveButton("重新扫描", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    initData();

                    dialog.dismiss();
                }
            });

            dialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else{
            dialog.setMessage("扫描结束，存在病毒，是否清理");
            dialog.setCancelable(false);
            dialog.setPositiveButton("清理", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //initData();

                    clearTrojan();

                    dialog.dismiss();
                }
            });

            dialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    private void clearTrojan() {
        for(Scaninfo scanInfo : scaninfos ){
            String packageName = scanInfo.packageName;

            Intent uninstall_localIntent = new Intent("android.intent.action.DELETE",
                    Uri.parse("package:" + packageName));
            startActivity(uninstall_localIntent);
        }

    }

    private void copyDB(String dbName) {
        File filesDir = getFilesDir();
        System.out.println("路径:" + filesDir.getAbsolutePath());
        File destFile = new File(getFilesDir(), dbName);// 要拷贝的目标地址

        if (destFile.exists()) {
            System.out.println("数据库" + dbName + "已存在!");
            return;
        }

        FileOutputStream out = null;
        InputStream in = null;

        try {
            //in = getAssets().open(dbName);
            in = getClassLoader().getResourceAsStream("assets/" + dbName);
            int length = in.available();
            //System.out.println("databasesize" + length);
            out = new FileOutputStream(destFile);

            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
