package com.gki.managerment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gki.managerment.adapter.PopupListAdapter;
import com.gki.managerment.constant.SharedPreferenceConstant;
import com.gki.managerment.db.UserDB;
import com.gki.managerment.http.AppContants;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.DateUtils;
import com.gki.managerment.util.SharedPreferencesUtils;
import com.gki.managerment.util.StringUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main extends BaseActivity {
    UserDB userDB;
    private Button btnLogin,btnCancel,btnSetup;
    private EditText txtUsername;
    private EditText txtPassword;
    private CheckBox chk_savePswd;

    // 构建Runnable对象，在runnable中更新界面
    Runnable connectRunnable = new Runnable() {
        public void run() {
            Message msg = new Message();
            String strUser = txtUsername.getText().toString();
            String strPwd = txtPassword.getText().toString();
            try {
                if ("".equals(strUser) || "".equals(strPwd)) // 判断 帐号和密码
                {
                    msg.what = 0;
                    msg.obj = "帐号或者密码不能为空，\n请输入后再登录！";
                    handler.sendMessage(msg);
                } else {
                    getService service = new getService();
                    String strLoginMsg = service.CheckUserPwdService(strUser, strPwd);
                    if (StringUtils.isNotEmpty(strLoginMsg)) {
                        if (strLoginMsg.equals("OK")) {
                            String strUserInfo = service.GetUserInfoService(strUser);
                            Gson gson = new Gson();
                            LoginUser aUser = gson.fromJson(strUserInfo, LoginUser.class);
                            aUser.LoginTime = DateUtils.getDataString("yyyy-MM-dd HH:mm:ss");
                            if (chk_savePswd.isChecked())
                                aUser.setPswd(null);
                            LoginUser.setUser(aUser);
                            userDB.insertLoginUser(aUser);
                            System.out.println(strUserInfo);
                            Main.this.finish();
                            Jump(MainMenu.class);
                            GetUserProdLine getUserProdLine = new GetUserProdLine();
                            getUserProdLine.execute(strUser);
                            //new PopupWindow()
                        } else if (strLoginMsg.equals("UserId_ERROR")) {
                            msg.what = 0;
                            msg.obj = "错误的登录帐号，\n请检查后重新输入！";
                            handler.sendMessage(msg);
                        } else if (strLoginMsg.equals("Password_ERROR")) {
                            msg.what = 0;
                            msg.obj = "您输入的密码不正确，\n请检查后重新输入！";
                            handler.sendMessage(msg);
                        } else if (strLoginMsg.equals("Authorize_ERROR")) {
                            msg.what = 0;
                            msg.obj = "登录用户不是平板用户，\n请联系系统管理员！";
                            handler.sendMessage(msg);
                        } else {
                            msg.what = 0;
                            msg.obj = strLoginMsg.replace("Error:", "");
                            handler.sendMessage(msg);
                        }
                    } else {
                        msg.what = 0;
                        msg.obj = "未知错误！";
                        handler.sendMessage(msg);
                    }
                }
            } catch (Exception e) {
                msg.what = 0;
                msg.obj = "登陆失败" + e.toString();
                handler.sendMessage(msg);
            }
        }
    };

    private class GetUserProdLine extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = getService.GetUserProdLineService(params[0]);
            return result;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result:" + result);
            if (!result.trim().equals("null") && !result.trim().equals("[]") && !result.trim().equals("{}") && !result.trim().equals("") && !result.trim().equals("anyType{}")) {
                try {
                    String strProdLine = "";
                    String strProdName = "";
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        String strKey = jsonObject2.getString("Prod_Line");
                        strProdLine += strKey + ",";
                        String strType = jsonObject2.getString("Prod_Line_Name");
                        strProdName += strType + ",";
                    }
                    LoginUser.getUser().All_Prod_Line = strProdLine;
                    LoginUser.getUser().All_ProdLineName = strProdName;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContants.ServicePath = SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.SERVICE_PATH, AppContants.ServicePath).toString();
        AppContants.ImagePath = SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.IMAGE_PATH, AppContants.ImagePath).toString();
        //获取最新版本情况
        new UpgradeManager(Main.this);

        userDB = new UserDB(Main.this);
        setContentView(R.layout.main);
        btnLogin = (Button) findViewById(R.id.btn_login_commit);
        btnCancel = (Button) findViewById(R.id.btn_Cancel);
        btnSetup = (Button) findViewById(R.id.btn_setup);
        txtUsername = (EditText) findViewById(R.id.et_username);
        txtPassword = (EditText) findViewById(R.id.et_password);
        chk_savePswd = (CheckBox) findViewById(R.id.chk_savePswd);
        ((TextView)findViewById(R.id.tv_version)).setText("V"+AppContants.Version);
        btnCancel.setOnClickListener(new MyListener());
        btnLogin.setOnClickListener(new MyListener());
        btnSetup.setOnClickListener(new MyListener());

        txtUsername.setText((String) SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.USER_NAME, ""));
        txtPassword.setText((String) SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.USER_PASSWORD, ""));
        chk_savePswd.setChecked((Boolean) SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.IS_KEEP_PASSWORD, false));
    }

    public <T> void Jump(Class<T> MyClass) {
        SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.IS_KEEP_PASSWORD, chk_savePswd.isChecked());
        if (chk_savePswd.isChecked()) {
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.USER_NAME, txtUsername.getText() == null ? "" : txtUsername.getText().toString());
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.USER_PASSWORD, txtPassword.getText() == null ? "" : txtPassword.getText().toString());
        } else {
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.USER_NAME, "");
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.USER_PASSWORD, "");
        }
        OpenForm(MyClass);
    }

    public class MyListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.btn_login_commit):
                    showLoadingDialog();
                    new Thread(connectRunnable).start();
                    break;
                case (R.id.btn_Cancel):
                    finish();
                    break;
                case (R.id.btn_setup):
                    OpenForm(Setting.class);
                    break;
                default:
                    break;
            }
        }
    }

    //PopupWindow对象
    private PopupWindow selectPopupWindow= null;
    //自定义Adapter
    private PopupListAdapter popupAdapter = null;
    //下拉框选项数据源
    ArrayList<LoginUser> lstUser = new ArrayList<LoginUser>();;
    //下拉框依附组件
    private LinearLayout parent;
    //下拉框依附组件宽度，也将作为下拉框的宽度
    private int pwidth;
    //下拉箭头图片组件
    private ImageView image;
    //展示所有下拉选项的ListView
    private ListView listView = null;
    //是否初始化完成标志
    private boolean flag = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            Bundle data = msg.getData();
            if (msg.what == 0) {
                dismissLoadingDialog();
                new AlertDialog.Builder(Main.this)
                        // .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                        .setTitle("登录错误").setMessage(msg.obj.toString())
                        .create().show();
            }
            else if (msg.what == 901) {
                txtUsername.setText(data.getString("userId"));
                txtPassword.setText(data.getString("pswd"));
                dismiss();
            }
            else if (msg.what == 902) {
                lstUser.remove(data.getInt("position"));
                userDB.deleteLoginUser(data.getString("userId"));
                popupAdapter.notifyDataSetChanged();
                if (lstUser.size() == 0)
                {
                    dismiss();
                }
            }
            else
            {
                new AlertDialog.Builder(Main.this).setTitle("错误").setMessage(msg.obj.toString()).create().show();
            }
        }
    };
    /**
     * 没有在onCreate方法中调用initWedget()，而是在onWindowFocusChanged方法中调用，
     * 是因为initWedget()中需要获取PopupWindow浮动下拉框依附的组件宽度，在onCreate方法中是无法获取到该宽度的
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        while(!flag){
            initWedget();
            flag = true;
        }
    }

    /**
     * 初始化界面控件
     */
    private void initWedget(){
        //初始化界面组件
        parent = (LinearLayout)findViewById(R.id.ll_userId);
        image = (ImageView)findViewById(R.id.img_account);

        //获取下拉框依附的组件宽度
        int width = parent.getWidth();
        pwidth = width;

        //设置点击下拉箭头图片事件，点击弹出PopupWindow浮动下拉框
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    //显示PopupWindow窗口
                    popupWindwShowing();
                }
            }
        });

        //初始化PopupWindow
        initPopuWindow();
    }
    /**
     * 初始化PopupWindow
     */
    private void initPopuWindow(){
        lstUser = userDB.findAllLoginUser();

        //PopupWindow浮动下拉框布局
        View loginwindow = (View)this.getLayoutInflater().inflate(R.layout.popup_list, null);
        listView = (ListView) loginwindow.findViewById(R.id.list);

        //设置自定义Adapter
        popupAdapter = new PopupListAdapter(this, handler, lstUser);
        listView.setAdapter(popupAdapter);

        selectPopupWindow = new PopupWindow(loginwindow, pwidth, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        selectPopupWindow.setOutsideTouchable(true);

        //这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失，
        //没有这一句则效果不能出来，但并不会影响背景
        //本人能力极其有限，不明白其原因，还望高手、知情者指点一下
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 显示PopupWindow窗口
     *
     */
    public void popupWindwShowing() {
        //将selectPopupWindow作为parent的下拉框显示，并指定selectPopupWindow在Y方向上向上偏移3pix，
        //这是为了防止下拉框与文本框之间产生缝隙，影响界面美化
        //（是否会产生缝隙，及产生缝隙的大小，可能会根据机型、Android系统版本不同而异吧，不太清楚）
        selectPopupWindow.showAsDropDown(parent,0,-3);
    }

    /**
     * PopupWindow消失
     */
    public void dismiss(){
        selectPopupWindow.dismiss();
    }
}