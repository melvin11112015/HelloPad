package com.gki.managerment;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.os.Handler.Callback;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import java.util.ArrayList;

import com.gki.managerment.adapter.OptionsAdapter;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.gki.managerment.bean.ConditionEntity;
import com.gki.managerment.bean.ProdMandayList;
import com.gki.managerment.global.BaseListItem;
import com.gki.managerment.global.BaseListItemParser;
import com.gki.managerment.global.ListItemAdapter;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.DateUtils;
import com.gki.managerment.util.StringUtils;
import com.gki.managerment.util.UserTask;
import com.gki.managerment.view.SettingView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class ConditionConfirm extends BaseActivity {
    private java.util.Timer timer;
    //记录是否由生产单触发的生产线改动
    private String PROD_LINE;
    private TextView tv_UserName, tv_date, tv_time, tv_week;
    private ListView lvData;
    private SettingView v_confitmTime;
    private EditText et_orderNo, et_confirmDate, et_confirmTime,et_item, et_carType;
    private Spinner sp_prodLine;
    List<ConditionEntity> lstData;
    GetByDocumentNoTask getByDocumentNoTask;
    private Dialog setTimeDialog;
    List<BaseListItem> lstProdLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.condition_confirm);
        initControl();
    }

    private void initControl() {
        tv_UserName = (TextView) findViewById(R.id.tv_username);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_week = (TextView) findViewById(R.id.tv_week);
        timer = new Timer(true);
        timer.schedule(
                new java.util.TimerTask() { public void run()
                {
                    new RefreshTimeTask().execute();
                }}, 0, 1000);

        et_orderNo = (EditText)findViewById(R.id.et_orderNo);
        et_confirmDate = (EditText)findViewById(R.id.et_confirmDate);
        et_confirmTime = (EditText)findViewById(R.id.et_confirmTime);
        et_item = (EditText)findViewById(R.id.et_item);
        et_carType = (EditText)findViewById(R.id.et_carType);
        lvData = (ListView)findViewById(R.id.lv_confirm);
        sp_prodLine = (Spinner)findViewById(R.id.sp_prodLine);

        findViewById(R.id.btn_back).setOnClickListener(new MyListener());
        findViewById(R.id.btn_add).setOnClickListener(new MyListener());
        findViewById(R.id.btn_query).setOnClickListener(new MyListener());
        findViewById(R.id.btn_submit).setOnClickListener(new MyListener());
        findViewById(R.id.btn_delete).setOnClickListener(new MyListener());

        tv_UserName.setText(LoginUser.user.User_Name);
        tv_date.setText(LoginUser.user.LoginTime.substring(0, 10));
        et_confirmDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
        et_confirmTime.setText(new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis())));
        //event.getKeyCode()==KeyEvent.KEYCODE_ENTER
        et_orderNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent curEvent) {
                if (curEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (curEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        if (StringUtils.isNotEmpty(et_orderNo.getText().toString())) {
                            getByDocumentNoTask = new GetByDocumentNoTask();
                            getByDocumentNoTask.execute(et_orderNo.getText().toString());
                            et_orderNo.selectAll();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        et_confirmDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {
                            @Override
                            public void onDateTimeSet(java.util.Date date) {
                                et_confirmDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date));
                                initDatas();
                            }
                        })
                        .setInitialDate(new java.util.Date())
                        .build()
                        .show();
            }
        });
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        et_confirmDate.setText(formatter.format(new Date(System.currentTimeMillis())));

        et_confirmTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetTimeDialog(et_confirmTime);
            }
        });

        sp_prodLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_prodLine.hasFocus() && !((BaseListItem) sp_prodLine.getSelectedItem()).getItemId().toString().equals(PROD_LINE)) {
                    et_orderNo.setText("");
                    et_item.setText("");
                    et_carType.setText("");
                }
                initDatas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        new GetUserProdLine().execute(LoginUser.getUser().User_ID);
        initDatas();
    }

    public class MyListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.btn_back):
                    ConditionConfirm.this.finish();
                    break;
                case (R.id.btn_query):
                    queryData();
                    break;
                case (R.id.btn_add):
                    newData();
                    break;
                case (R.id.btn_delete):
                    deleteData();
                    break;
                case (R.id.btn_submit):
                    submitData();
                    break;
                default:
                    break;
            }
        }
    }
    private void submitData() {
        new UserTask<String, String, String>() {
            @Override
            protected void onTaskPrepare() {
                super.onTaskPrepare();
                showLoadingDialog();
            }
            @Override
            protected void onErrorHandle(Context context, Exception error) {
                Toast.makeText(ConditionConfirm.this, "保存失败！", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (StringUtils.isNotEmpty(s)) {
                    Toast.makeText(ConditionConfirm.this, "保存成功！", Toast.LENGTH_SHORT).show();
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                Gson gson = new Gson();
                String strData = gson.toJson(lstData);
                return getService.SaveData_Confirm(strData);
            }
        }).execute();
    }
    private void deleteData() {
        new UserTask<String, String, String>() {
            @Override
            protected void onTaskPrepare() {
                super.onTaskPrepare();
                showLoadingDialog();
            }
            @Override
            protected void onErrorHandle(Context context, Exception error) {
                Toast.makeText(ConditionConfirm.this, "删除失败！", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (StringUtils.isNotEmpty(s)) {
                    Toast.makeText(ConditionConfirm.this, "删除成功！", Toast.LENGTH_SHORT).show();
                    queryData();
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.DeleteData_Confirm(((BaseListItem)sp_prodLine.getSelectedItem()).getItemId(),et_confirmDate.getText().toString()+" "+et_confirmTime.getText().toString()+":00");
            }
        }).execute();
    }
    private void queryData() {
        if (sp_prodLine.getCount() == 0) {
            Toast.makeText(ConditionConfirm.this, "未设置用户生产线信息，请设置之后再尝试，谢谢！", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ConditionConfirm.this, "操作失败！", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                Gson gson = new Gson();
                lstData = gson.fromJson(s, new TypeToken<List<ConditionEntity>>() {
                }.getType());
                ListAdapter lstAdapter = new ListAdapter(ConditionConfirm.this, lstData);
                lvData.setAdapter(lstAdapter);
                lstAdapter.notifyDataSetChanged();
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.GetData_Confirm(((BaseListItem)sp_prodLine.getSelectedItem()).getItemId(), et_confirmDate.getText().toString()+" "+et_confirmTime.getText().toString()+":00");
            }
        }).execute();
    }
    private void newData() {
        if (sp_prodLine.getCount() == 0) {
            ShowTipMessage("未设置用户生产线信息，请设置之后再尝试，谢谢！");
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
                ShowTipMessage("加载失败!", error.getMessage());
                dismissLoadingDialog();
            }
            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (TextUtils.isEmpty(s)) {
                    ShowTipMessage("加载失败，未获取到返回数据!");
                    return;
                }
                if (s.indexOf("Error:") == 0) {
                    ShowTipMessage("加载失败!", s);
                    return;
                }
                Gson gson = new Gson();
                lstData = gson.fromJson(s, new TypeToken<List<ConditionEntity>>() {
                }.getType());
                ListAdapter lstAdapter = new ListAdapter(ConditionConfirm.this, lstData);
                lvData.setAdapter(lstAdapter);
                lstAdapter.notifyDataSetChanged();
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.InitData_Confirm(et_orderNo.getText().toString(), ((BaseListItem)sp_prodLine.getSelectedItem()).getItemId(), et_confirmDate.getText().toString()+" "+et_confirmTime.getText().toString()+":00", LoginUser.getUser().User_ID);
            }
        }).execute();
    }

    @SuppressWarnings("all")
    public class ListAdapter extends BaseAdapter {
        private List<ConditionEntity> list;
        private Context context;

        public ListAdapter(Context context, List<ConditionEntity> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        NumberKeyListener numberKeyListener = new NumberKeyListener() {
            // 0无键盘 1英文键盘 2模拟键盘 3数字键盘
            @Override
            public int getInputType() {
                return 3;
            }
            // 返回允许输入的字符
            @Override
            protected char[] getAcceptedChars() {
                return getAcceptedChars();
            }
        };

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.condition_confirm_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ConditionEntity element = list.get(position);
            holder.tv_itemName.setText(element.Processing_Con_Desc);
            holder.et_Setup_Value.setText(element.Setup_Value);
            holder.et_Setup_Value.setSelectAllOnFocus(true);
            holder.et_Setup_Value.setInputType(InputType.TYPE_CLASS_NUMBER);
            //holder.et_Confirm_Result.setKeyListener(numberKeyListener);
            holder.et_Confirm_Result.setSelectAllOnFocus(true);
            holder.et_Confirm_Result.setInputType(InputType.TYPE_CLASS_NUMBER);
            //holder.et_Confirm_Result.setKeyListener(numberKeyListener);
            holder.et_Confirm_Result.setText(element.Confirm_Result);
            holder.et_remark.setText(element.Remark);
            holder.et_remark.setSelectAllOnFocus(true);
            holder.et_remark2.setText(element.Remark_2);
            holder.et_remark2.setSelectAllOnFocus(true);
            final ViewHolder h = holder;
            holder.et_Setup_Value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        element.Setup_Value = h.et_Setup_Value.getText().toString();
                    }
                }
            });
            holder.et_Confirm_Result.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        element.Confirm_Result = h.et_Confirm_Result.getText().toString();
                    }
                }
            });
            holder.et_remark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        element.Remark = h.et_remark.getText().toString();
                    }
                }
            });
            holder.et_remark2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        element.Remark_2 = h.et_remark2.getText().toString();
                    }
                }
            });
            return convertView;
        }

        public class ViewHolder {
            TextView tv_itemName;
            EditText et_Setup_Value, et_Confirm_Result, et_remark, et_remark2;
            //CheckBox chb_select;

            public ViewHolder(View view) {
                tv_itemName = (TextView)view.findViewById(R.id.tv_itemName);
                et_Setup_Value = (EditText)view.findViewById(R.id.et_Setup_Value);
                et_Confirm_Result = (EditText)view.findViewById(R.id.et_Confirm_Value);
                et_remark = (EditText)view.findViewById(R.id.et_remark);
                et_remark2 = (EditText)view.findViewById(R.id.et_remark2);
                //chb_select = (CheckBox)view.findViewById(R.id.chb_select);
            }
        }
    }
    private class GetByDocumentNoTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return getService.GetProdOrderService(params[0]);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            if (StringUtils.isNotEmpty(result) || result == "{}" || result == "[]") {
                Gson gson = new Gson();
                ProdMandayList bean = gson.fromJson(result, ProdMandayList.class);
                if (LoginUser.getUser().All_Prod_Line.indexOf(bean.Production_line+",") == -1)
                {
                    ShowTipMessage("【生产单】不正确：该【生产单】不属于您所在【生产线】！");
                    return;
                }
                PROD_LINE = bean.Production_line;
                et_confirmDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
                et_confirmTime.setText(new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis())));
                if (lstProdLine != null) {
                    for (int i = 0 ; i < lstProdLine.size(); i ++) {
                        if (lstProdLine.get(i).getItemId().equals(PROD_LINE))
                        {
                            sp_prodLine.setSelection(i);
                            break;
                        }
                    }
                }
                et_carType.setText(bean.Car_Model);
                et_item.setText(bean.Source_No);
            }
            else
            {
                et_item.setText("");
                et_carType.setText("");
                ShowTipMessage("找不到该生产单号，请重新输入！");
            }
        }
    }
    private void showSetTimeDialog(final EditText aCtrl) {
        if (setTimeDialog != null) {
            setTimeDialog.dismiss();
            setTimeDialog = null;
        }
        setTimeDialog = new Dialog(this);
        View content = LayoutInflater.from(this).inflate(R.layout.set_time, null);
        final TimePicker tp = (TimePicker) content.findViewById(R.id.tp_time);
        Calendar c = Calendar.getInstance();
        tp.setIs24HourView(true);
        tp.setCurrentHour(Integer.parseInt(aCtrl.getText().toString().substring(0,2)));
        tp.setCurrentMinute(Integer.parseInt(aCtrl.getText().toString().substring(3,5)));

        Button cancel = (Button) content.findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeDialog.dismiss();
            }
        });

        Button confirm = (Button) content.findViewById(R.id.okButton);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeDialog.dismiss();
                aCtrl.setText(getFormatedHourValue(tp.getCurrentHour()) + ":" + getFormatedMinutesValue(tp.getCurrentMinute()));
            }
        });
        setTimeDialog.setTitle("设置时间");
        setTimeDialog.setCanceledOnTouchOutside(true);
        setTimeDialog.setContentView(content);
        setTimeDialog.show();
    }
    private class GetUserProdLine extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = getService.GetUserProdLineService(LoginUser.getUser().User_ID);
            return result;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result:" + result);
            if (!result.trim().equals("null") && !result.trim().equals("[]") && !result.trim().equals("{}") && !result.trim().equals("") && !result.trim().equals("anyType{}")) {
                lstProdLine = new BaseListItemParser().getListFromJson(result, "Prod_Line", "Prod_Line_Name", false);
                ListItemAdapter adapter = new ListItemAdapter(getBaseContext(), lstProdLine);
                sp_prodLine.setAdapter(adapter);
                for(int i=0; i< lstProdLine.size(); i++)
                {
                    if (lstProdLine.get(i).getItemId().equals(PROD_LINE))
                    {
                        sp_prodLine.setSelection(i);
                        break;
                    }
                }
            }
        }
    }
    private String getFormatedHourValue(int value) {
        if (value == 0) {
            return "00";
        }
        if (value > 24) {
            return value - 24 + "";
        }

        if (value > 0 && value < 10) {
            return "0" + value;
        }
        return value + "";
    }
    private String getFormatedMinutesValue(int value) {
        if (value == 0) {
            return "00";
        }
        if (value > 0 && value < 10) {
            return "0" + value;
        }
        return value + "";
    }

    private class RefreshTimeTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            tv_date.setText(DateUtils.getDataString(DateUtils.DateFormat.PART_DATE_FORMAT));
            tv_time.setText(DateUtils.getDataString(DateUtils.DateFormat.TIME_FORMAT));
            tv_week.setText(DateUtils.getWeekString(DateUtils.getDataString(DateUtils.DateFormat.FULL_DATE_FORMAT)));
        }
    }

    //PopupWindow对象
    private PopupWindow selectPopupWindow= null;
    //自定义Adapter
    private OptionsAdapter optionsAdapter = null;
    //下拉框选项数据源
    private ArrayList<String> datas = new ArrayList<String>();;
    //下拉框依附组件
    private LinearLayout parent;
    //下拉框依附组件宽度，也将作为下拉框的宽度
    private int pwidth;
    //文本框
    private EditText et;
    //下拉箭头图片组件
    private ImageView image;
    //展示所有下拉选项的ListView
    private ListView listView = null;
    //用来处理选中或者删除下拉项消息
    private Handler handler;
    //是否初始化完成标志
    private boolean flag = false;


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
        //初始化Handler,用来处理消息
        handler = new Handler(){
            @Override
            public void handleMessage(Message message) {
                Bundle data = message.getData();
                switch(message.what){
                    case 1:
                        //选中下拉项，下拉框消失
                        int selIndex = data.getInt("selIndex");
                        et.setText(datas.get(selIndex));
                        dismiss();
                        break;
                }
            }
        };

        //初始化界面组件
        parent = (LinearLayout)findViewById(R.id.ll_confirmTime);
        et = (EditText)findViewById(R.id.et_confirmTime);
        image = (ImageView)findViewById(R.id.img_confirmTimes);


        //获取下拉框依附的组件宽度
        int width = parent.getWidth();
        pwidth = width;

        //设置点击下拉箭头图片事件，点击弹出PopupWindow浮动下拉框
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    //显示PopupWindow窗口
                    popupWindwShowing();
                }
            }
        });

        //初始化PopupWindow
        initPopuWindow();

    }

    /**
     * 初始化填充Adapter所用List数据
     */
    private void initDatas(){

        datas.clear();
/*
        datas.add("10:30");
        datas.add("11:30");
        datas.add("13:30");
        datas.add("14:30");
*/
        if (sp_prodLine.getCount() == 0) {
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
                Toast.makeText(ConditionConfirm.this, "操作失败！", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                String[] strTimes = s.split("\\|");
                for (int i = 0 ; i <strTimes.length ; i++ ) {
                    datas.add(strTimes[i]);
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.GetInputTimes_Confirm(((BaseListItem) sp_prodLine.getSelectedItem()).getItemId(), et_confirmDate.getText().toString());
            }
        }).execute();
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopuWindow(){

        //initDatas();

        //PopupWindow浮动下拉框布局
        View loginwindow = (View)this.getLayoutInflater().inflate(R.layout.popup_list, null);
        listView = (ListView) loginwindow.findViewById(R.id.list);

        //设置自定义Adapter
        optionsAdapter = new OptionsAdapter(this, handler,datas);
        listView.setAdapter(optionsAdapter);

        selectPopupWindow = new PopupWindow(loginwindow, pwidth,LayoutParams.WRAP_CONTENT, true);

        selectPopupWindow.setOutsideTouchable(true);

        //这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失，
        //没有这一句则效果不能出来，但并不会影响背景
        //本人能力极其有限，不明白其原因，还望高手、知情者指点一下
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }



    /**
     * 显示PopupWindow窗口
     *
     * @param popupwindow
     */
    public void popupWindwShowing() {
        //将selectPopupWindow作为parent的下拉框显示，并指定selectPopupWindow在Y方向上向上偏移3pix，
        //这是为了防止下拉框与文本框之间产生缝隙，影响界面美化
        //（是否会产生缝隙，及产生缝隙的大小，可能会根据机型、Android系统版本不同而异吧，不太清楚）
        selectPopupWindow.showAsDropDown(parent, 0, -3);
    }

    /**
     * PopupWindow消失
     */
    public void dismiss(){
        selectPopupWindow.dismiss();
    }


}