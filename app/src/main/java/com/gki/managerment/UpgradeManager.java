package com.gki.managerment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.gki.managerment.http.AppContants;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.StringUtils;
import com.gki.managerment.util.UnZipUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class UpgradeManager {
    // 应用程序Context
    private Context mContext;
    // 提示消息
    private String updateMsg = "有最新的软件包，请下载！";
    // 下载安装包的网络路径
    private String APK_URL = "http://120.25.75.151:8888/UpgradeFiles/GKI PAD.apk";
    private Dialog noticeDialog;// 提示有软件更新的对话框
    private Dialog downloadDialog;// 下载对话框
    private static final String savePath = "/sdcard/updatedemo/";// 保存apk的文件夹
    private static final String saveFileName = savePath + "GKI PAD.apk";
    private static final String zipFileName = savePath + "GKI PAD.zip";
    // 进度条与通知UI刷新的handler和msg常量
    private ProgressBar mProgress;
    private static final int PROCESS_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int UNZIP_SUCCESS = 3;
    private static final int DOWN_ERROR = 4;
    private static final int UNZIP_ERROR = 5;

    private int progress;// 当前进度
    private Thread downLoadThread; // 下载线程
    private boolean interceptFlag = false;// 用户取消下载
    // 通知处理刷新界面的handler
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESS_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    unzip();
                    break;
                case UNZIP_SUCCESS:
                    downloadDialog.dismiss();
                    installApk();
                    break;
                case DOWN_ERROR:
                    //downloadDialog.setTitle(msg.getData().getString("MSG"));
                    downloadDialog.dismiss();
                    break;
                case UNZIP_ERROR:
                    //downloadDialog.setTitle(msg.getData().getString("MSG"));
                    downloadDialog.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    public UpgradeManager(Context context) {
        this.mContext = context;
        new GetNewVersion().execute();
    }
    private class GetNewVersion extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return getService.GetNewVersion(AppContants.Version);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String strUrl) {
            if (StringUtils.isNotEmpty(strUrl) && strUrl.toLowerCase().indexOf("http://") == 0) {
                showNoticeDialog(strUrl);
            }
        }
    }
    public void showNoticeDialog(String apkUrl) {
        APK_URL = apkUrl;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle("发现新版的程序，是否马上更新？").setMessage(updateMsg)
            .setPositiveButton("马上更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                    showDownloadDialog();
                }
            })
            .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }
    protected void showDownloadDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);

        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        builder.setView(v);// 设置对话框的内容为一个View
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();
        downloadApk();
    }
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }
    protected void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");// File.toString()会返回路径信息
        mContext.startActivity(i);
    }
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            URL url;
            try {
                url = new URL(APK_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();

                File inputFile = new File(savePath);
                if (!inputFile.exists()) {
                    inputFile.mkdir();
                }
                String apkFile = zipFileName;
                zipFile = new File(apkFile);
                FileOutputStream outStream = new FileOutputStream(zipFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = ins.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 下载进度
                    mHandler.sendEmptyMessage(PROCESS_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    outStream.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消停止下载
                outStream.close();
                ins.close();
            } catch (Exception e) {
                Bundle bundle = new Bundle();
                if (e.toString().toUpperCase().indexOf("FileNoFound") >= 0)
                {
                    bundle.putString("MSG","找不到文件，错误信息："+e.toString());
                }
                else
                {
                    bundle.putString("MSG","下载失败，错误：" + e.toString());
                }
                Message msg = new Message();
                msg.what = DOWN_ERROR;
                msg.setData(bundle);
                mHandler.handleMessage(msg);
                e.printStackTrace();
            }
        }
    };
    private File zipFile;
    private File unzipFile;

    private long unzip(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("正在解压文件...");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);

        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        builder.setView(v);// 设置对话框的内容为一个View
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        String unzipUrl = "/sdcard/updatedemo/";
        unzipFile = new File(unzipUrl);
        if(!unzipFile.exists()){
            if(!unzipFile.mkdir()){
                Log.e("Unzip", "Failed to make directories:"+unzipFile.getAbsolutePath());
            }
        }

        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile);
            long uncompressedSize = getOriginalSize(zip);
            mProgress.setMax((int) uncompressedSize);
            progress = 0;
            mHandler.sendEmptyMessage(PROCESS_UPDATE);

            entries = (Enumeration<ZipEntry>) zip.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.isDirectory()){
                    continue;
                }
                File destination = new File(unzipFile, entry.getName());
                if(!destination.getParentFile().exists()){
                    Log.e("unzip", "make="+destination.getParentFile().getAbsolutePath());
                    destination.getParentFile().mkdirs();
                }
                ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destination);
                extractedSize+=copy(zip.getInputStream(entry),outStream);
                outStream.close();
                progress += extractedSize;
                mHandler.sendEmptyMessage(PROCESS_UPDATE);
                if (progress >= mProgress.getMax())
                {
                    mHandler.sendEmptyMessage(UNZIP_SUCCESS);
                }
            }
        } catch (ZipException e) {
            Bundle bundle = new Bundle();
            bundle.putString("MSG","解压失败，错误信息："+e.toString());
            Message msg = new Message();
            msg.what = UNZIP_ERROR;
            msg.setData(bundle);
            mHandler.handleMessage(msg);
            e.printStackTrace();
        } catch (IOException e) {
            Bundle bundle = new Bundle();
            bundle.putString("MSG","解压失败，错误信息："+e.toString());
            Message msg = new Message();
            msg.what = UNZIP_ERROR;
            msg.setData(bundle);
            mHandler.handleMessage(msg);
            e.printStackTrace();
        }finally{
            zipFile = null;
            unzipFile = null;
            try {
                zip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return extractedSize;
    }
    private int copy(InputStream input, OutputStream output){
        byte[] buffer = new byte[1024*8];
        BufferedInputStream in = new BufferedInputStream(input, 1024*8);
        BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
        int count =0,n=0;
        try {
            while((n=in.read(buffer, 0, 1024*8))!=-1){
                out.write(buffer, 0, n);
                count+=n;
            }
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return count;
    }
    private long getOriginalSize(ZipFile file){
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
        long originalSize = 0l;
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if(entry.getSize()>=0){
                originalSize+=entry.getSize();
            }
        }
        return originalSize;
    }
    private final class ProgressReportingOutputStream extends FileOutputStream{
        public ProgressReportingOutputStream(File file)
                throws FileNotFoundException {
            super(file);
            // TODO Auto-generated constructor stub
        }
        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount)
                throws IOException {
            // TODO Auto-generated method stub
            super.write(buffer, byteOffset, byteCount);
        }
    }
}