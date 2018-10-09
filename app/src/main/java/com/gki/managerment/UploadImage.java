package com.gki.managerment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.gki.managerment.http.AppContants;
import com.gki.managerment.util.FileUtils;
import com.gki.managerment.util.StringUtils;
import com.gki.managerment.util.UserTask;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by summit on 2/20/16.
 */
public class UploadImage extends BaseActivity {

    File file = null;
    private static final int CAMERA_REQUEST = 1888;
    private Button btnTake, btnUpload;
    private TextView tvTitle;
    private String DOCUMENT_NO;
    private String url;
    private ImageView image;
    private Uri photoUri;
    private String picPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        btnTake = (Button) findViewById(R.id.btnTake);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        image = (ImageView) findViewById(R.id.iv_cover);

        DOCUMENT_NO = getIntent().getStringExtra("diary_id");
        url = getIntent().getStringExtra("url");
        tvTitle.setText(getIntent().getStringExtra("title"));
        //tvTitle.setText(url);
        //DOCUMENT_NO = "ABCD";

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DOCUMENT_NO == null || file == null) {
                    ShowTipMessage("请先拍照!");
                    return;
                }
                new UserTask<String, String, String>() {

                    @Override
                    protected void onTaskPrepare() {
                        super.onTaskPrepare();
                        showLoadingDialog();
                    }

                    @Override
                    protected void onErrorHandle(Context context, Exception error) {
                        dismissLoadingDialog();
                        ShowTipMessage("上传失败,请重试。",error.getMessage());
                    }

                    @Override
                    protected void onTaskFinished(Context context, String s) {
                        dismissLoadingDialog();
                        System.out.println(s);
                        if (!TextUtils.isEmpty(s) && s.indexOf("Error:") != 0) {
                            Intent intent = new Intent();
                            intent.putExtra("url", s);
                            setResult(Activity.RESULT_OK, intent);
                            //tvTitle.setText( AppContants.ImagePath + DOCUMENT_NO + "/" + s);
                            finish();
                        } else {
                            ShowTipMessage("上传失败,请重试。",s);
                        }
                        photoUri = null;
                        file = null;
                        System.gc();
                    }
                }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
                    @Override
                    public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                        return FileUtils.UploadImage(file, AppContants.ServicePath + "UploadFile.aspx");
                    }
                }).execute();
            }
        });
        //image.setDefaultImageResId(R.drawable.test);
        image.setImageResource(R.drawable.test);
        //image.setErrorImageResId(R.drawable.test);
        //image.setImageURI(Uri.parse(url == null ? "" : url));
        if (StringUtils.isNotEmpty(url)) {
            RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
            ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(image, R.drawable.test, R.drawable.test);
            imageLoader.get(AppContants.ImagePath + DOCUMENT_NO + "/" + url, listener);
        }
    }

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        //执行拍照前，应该先判断SD卡是否存在
        file = null;
        photoUri = null;
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            //intent.putExtra(android.provider.MediaStore., photoUri);
            /**-----------------*/
            startActivityForResult(intent, CAMERA_REQUEST);
        } else {
            ShowTipMessage("内存卡不存在!");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //上传照片
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            String[] pojo = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                cursor.moveToFirst();
                picPath = cursor.getString(columnIndex);
                cursor.close();
            }
            if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
                BitmapFactory.Options zipOptions = new BitmapFactory.Options();
                zipOptions.inJustDecodeBounds = true;
                Bitmap bm = BitmapFactory.decodeFile(picPath, zipOptions);
                zipOptions.inSampleSize = zipOptions.outWidth / 800;
                zipOptions.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(picPath,zipOptions);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                image.setImageBitmap(bm);

                String saveDir = Environment.getExternalStorageDirectory()
                        + "/temple";
                File dir = new File(saveDir);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                file = new File(saveDir, DOCUMENT_NO);
                if (file.exists()) {
                    file.delete();
                }
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    byte[] byteArray = baos.toByteArray();
                    bos.write(byteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ShowTipMessage("无效的文件路径！");
            }
        }
    }
    public class BitmapCache implements ImageLoader.ImageCache {
        private LruCache<String, Bitmap> cache;

        public BitmapCache() {
            cache = new LruCache<String, Bitmap>(8 * 1024 * 1024) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            cache.put(url, bitmap);
        }
    }
}