package com.gki.managerment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.gki.managerment.adapter.BlankrAdapter;
import com.gki.managerment.http.Service.commitToService;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.parser.Blankentity;
import com.gki.managerment.parser.BlanksParser;
import com.gki.managerment.util.StringUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

//报废情况
public class DailyScrap extends BaseActivity implements OnClickListener {
    List<Blankentity> dataList1, dataList2, dataList3, dataList4;
    float iCount1,iCount2,iCount3,iCount4;
    private SubmitTask saveTask;
    private Context context;
    private Button btnCancle, btnSave;
    private String blanktype, strDocument_no, strSubmitData;
    private BlanksParser blanksParser = new BlanksParser();
    private EditText et_input;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                ShowTipMessage("没有获取到数据！");
            } else if (msg.what == 1) {
                //实例化ListView 并设置Adapter
                ListView listView = (ListView) findViewById(R.id.LV_1);
                BlankrAdapter adapter = new BlankrAdapter(dataList1, DailyScrap.this);
                listView.setAdapter(adapter);

                ListView listView2 = (ListView) findViewById(R.id.LV_2);
                BlankrAdapter adapter2 = new BlankrAdapter(dataList2, DailyScrap.this);
                listView2.setAdapter(adapter2);

                ListView listView3 = (ListView) findViewById(R.id.LV_3);
                BlankrAdapter adapter3 = new BlankrAdapter(dataList3, DailyScrap.this);
                listView3.setAdapter(adapter3);

                ListView listView4 = (ListView) findViewById(R.id.LV_4);
                BlankrAdapter adapter4 = new BlankrAdapter(dataList4, DailyScrap.this);
                listView4.setAdapter(adapter4);
            }
        }
    };
    Runnable runnable = new Runnable() {
        public void run() {
            Message msg = new Message();
            dataList1 = blanksParser.getJsonParser(getService.GetProdMandayParavService(strDocument_no, "1"));
            dataList2 = blanksParser.getJsonParser(getService.GetProdMandayParavService(strDocument_no, "2"));
            dataList3 = blanksParser.getJsonParser(getService.GetProdMandayParavService(strDocument_no, "3"));
            dataList4 = blanksParser.getJsonParser(getService.GetProdMandayParavService(strDocument_no, "4"));
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_scrap);
        init();
        new Thread(runnable).start();
    }

    private void init() {
        dataList1 = new ArrayList<Blankentity>();
        dataList2 = new ArrayList<Blankentity>();
        dataList3 = new ArrayList<Blankentity>();
        dataList4 = new ArrayList<Blankentity>();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        blanktype = (String) bundle.getSerializable("blanktype");
        strDocument_no = getIntent().getStringExtra("document_no");
        System.out.println("===>blanktype" + blanktype);

        btnCancle = (Button) findViewById(R.id.btn_staffInput_cancle);
        btnCancle.setOnClickListener(new MyListener());

        btnSave = (Button) findViewById(R.id.btn_staffInput_comm);
        btnSave.setOnClickListener(new MyListener());
    }

    private void SaveData() {
        strSubmitData = "[";
        iCount1 = GetDataCount(dataList1);
        iCount2 = GetDataCount(dataList2);
        iCount3 = GetDataCount(dataList3);
        iCount4 = GetDataCount(dataList4);
        if (strSubmitData.length() > 1) {
            strSubmitData = strSubmitData.substring(0, strSubmitData.length() - 1);
        }
        strSubmitData += "]";
        saveTask = new SubmitTask();
        saveTask.execute(strSubmitData);
    }

    private float GetDataCount(List<Blankentity> dataList) {
        try {
            Gson gson = new Gson();
            float aFloat = 0;
            String str = "";
            Blankentity aEntity;
            for (int i = 0; i < dataList.size(); i++) {
                aEntity = dataList.get(i);
                aEntity.setDocument_no(strDocument_no);
                if (!aEntity.getQuantity_Time().equals(null) && !aEntity.getQuantity_Time().trim().equals(""))
                {
                    aFloat += StringUtils.ToFloat(aEntity.getQuantity_Time());
                }
                else
                {
                    aEntity.setQuantity_Time("0");
                }
                strSubmitData += gson.toJson(aEntity) + ",";
            }
            return aFloat;
        } catch (Exception ex) {
            String strMsg = ex.getMessage();
            return -1;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
    }

    public class MyListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.btn_staffInput_cancle):
                    finish();
                    break;
                case (R.id.btn_staffInput_comm):
                    showLoadingDialog();
                    SaveData();
                    break;
                default:
                    break;
            }
        }
    }

    private class SubmitTask extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            return commitToService.SaveDailyDetail(params[0]);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.println("execute result:" + result);
                if (result.trim().equals("true")) {
                    Intent mIntent = new Intent();
                    mIntent.putExtra("gz01", "" + iCount1);
                    mIntent.putExtra("gz02", "" + iCount2);
                    mIntent.putExtra("gz03", "" + iCount3);
                    mIntent.putExtra("gz04", "" + iCount4);
                    //setResult(1, mIntent);
                    setResult(Activity.RESULT_OK, mIntent);
                    DailyScrap.this.finish();
                } else {
                    ShowTipMessage("提交失败!", result);
                }
            }
            catch (Exception ex)
            {
                String strErr = ex.getMessage();
            }
            finally
            {
                dismissLoadingDialog();
            }
        }
    }
}