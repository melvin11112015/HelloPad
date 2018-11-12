package com.gki.v107.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.gki.managerment.LoginUser;
import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.PadMessageAddon;
import com.gki.v107.entity.PadMessageInfo;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.BaseOdataCallback;
import com.gki.v107.tool.DatetimeTool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuildMessageActivity extends AppCompatActivity {

    private EditText editText1, editText2, editText3, editText4, editText5, editText6;
    private Button button;

    private TextView tvProdDate, tvShift, tvName;

    private PadMessageAddon addon = new PadMessageAddon();
    private boolean isEditing = false;
    private int editEntry = -1;
    private boolean isDay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_build_message);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
            actionBar.setTitle("留言消息");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        isDay = getIntent().getBooleanExtra("shift", true);

        editText1 = (EditText) findViewById(R.id.et2_message_build1);
        editText2 = (EditText) findViewById(R.id.et2_message_build2);
        editText3 = (EditText) findViewById(R.id.et2_message_build3);
        editText4 = (EditText) findViewById(R.id.et2_message_build4);
        editText5 = (EditText) findViewById(R.id.et2_message_build5);
        editText6 = (EditText) findViewById(R.id.et2_message_build6);

        tvShift = (TextView) findViewById(R.id.tv2_message_build_shift);
        tvShift.setText(isDay ? "白班" : "夜班");

        tvName = (TextView) findViewById(R.id.tv2_message_build_name);
        String nameStr2 = LoginUser.getUser().getUserId() + " " + LoginUser.getUser().ProdLineName;
        tvName.setText(nameStr2);


        tvProdDate = (TextView) findViewById(R.id.tv2_message_date2);
        String datetime = getIntent().getStringExtra("datetime");
        if (datetime == null)
            tvProdDate.setText(DatetimeTool.getCurrentOdataDate());
        else
            tvProdDate.setText(datetime);


        tvProdDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(BuildMessageActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                SimpleDateFormat sdfDat = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.set(year, monthOfYear, dayOfMonth);
                                tvProdDate.setText(sdfDat.format(c.getTime()));
                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        button = (Button) findViewById(R.id.button2_build_message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = editText1.getText().toString().trim();
                String msg2 = editText2.getText().toString().trim();
                String msg3 = editText3.getText().toString().trim();
                String msg4 = editText4.getText().toString().trim();
                String msg5 = editText5.getText().toString().trim();
                String msg6 = editText6.getText().toString().trim();
                String prodDatetime = tvProdDate.getText().toString().trim() + "T00:00:00";

                if (msg.isEmpty() && msg2.isEmpty() && msg3.isEmpty() && msg4.isEmpty() && msg5.isEmpty() && msg6.isEmpty()) {
                    ToastUtil.show(BuildMessageActivity.this, "请输入留言消息");
                    return;
                }


                addon.setMsg(msg);
                addon.setMsg2(msg2);
                addon.setMsg3(msg3);
                addon.setMsg4(msg4);
                addon.setMsg5(msg5);
                addon.setMsg6(msg6);
                addon.setProdDate(prodDatetime);


                if (isEditing) {
                    String itemDesc = String.format(
                            "EntryNo=%d",
                            editEntry);

                    ApiTool.updatePadMessage(itemDesc, addon, new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                ToastUtil.show(BuildMessageActivity.this, "提交成功");
                                finish();
                            } else {
                                ToastUtil.show(BuildMessageActivity.this, "提交失败,");
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            t.printStackTrace();
                            ToastUtil.show(BuildMessageActivity.this, "提交失败,");
                        }
                    });


                } else {

                    addon.setShift(isDay ? "Day" : "Night");
                    addon.setCreate_Name(LoginUser.getUser().getUserId());
                    addon.setCreate_User(LoginUser.getUser().getUserId());
                    addon.setCreate_DateTime(DatetimeTool.getCurrentOdataDatetime());
                    addon.setProdLineName(LoginUser.getUser().ProdLineName);
                    addon.setProdLine(LoginUser.getUser().Prod_Line);

                    ApiTool.addPadMessage(addon, new BaseOdataCallback<Map<String, Object>>() {
                        @Override
                        public void onDataAvailable(Map<String, Object> datas) {
                            ToastUtil.show(BuildMessageActivity.this, "提交成功");
                            finish();
                        }

                        @Override
                        public void onDataUnAvailable(String msg, int errorCode) {
                            ToastUtil.show(BuildMessageActivity.this, "提交失败," + msg);
                        }
                    });
                }

            }
        });

        if (getIntent().getSerializableExtra("padmessage") != null) {
            PadMessageInfo info = (PadMessageInfo) getIntent().getSerializableExtra("padmessage");

            editText1.setText(info.getMsg());
            editText2.setText(info.getMsg2());
            editText3.setText(info.getMsg3());
            editText4.setText(info.getMsg4());
            editText5.setText(info.getMsg5());
            editText6.setText(info.getMsg6());

            editEntry = info.getEntryNo();

            addon.setShift(info.getShift());
            addon.setCreate_Name(info.getCreate_Name());
            addon.setCreate_User(info.getCreate_User());
            addon.setCreate_DateTime(info.getCreate_DateTime());
            addon.setProdLineName(info.getProdLineName());
            addon.setProdLine(info.getProdLine());

            tvProdDate.setText(info.getProdDate().split("T")[0]);
            String nameStr = info.getCreate_Name() + " " + info.getProdLineName();
            tvName.setText(nameStr);
            tvShift.setText(isDay ? "白班" : "夜班");

            isEditing = true;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
