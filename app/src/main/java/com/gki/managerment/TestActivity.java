package com.gki.managerment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity {
    private String result;
    private Spinner spinner;
    private Button bt;
    private List<String> list;
    private Handler handler;
    Runnable runnable = new Runnable() {
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            String result = getService.GetProdMandayParavService("1", "");
            data.putString("value", result);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("value");
                LogUtil.i("dataBean", "请求结果>>>>" + val);
            }
        };
        new Thread(runnable).start();

        spinner = (Spinner) findViewById(R.id.spinner);
        bt = (Button) findViewById(R.id.bt);
        list = new ArrayList<String>();
        list.add("text");
        list.add("text");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_textitem, R.id.spinner_itemtext, list);
        spinner.setAdapter(adapter);
        spinner.setPrompt("ceshi");
        bt.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                list.add("要添加的数据");
            }
        });
    }

}
