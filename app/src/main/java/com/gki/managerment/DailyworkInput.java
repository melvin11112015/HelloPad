package com.gki.managerment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.gki.managerment.bean.DailyReport;
import com.gki.managerment.bean.DailyTitle;;
import com.gki.managerment.bean.ProdMandayList;
import com.gki.managerment.bean.ProdMandayListSV_QueryList_Response;
import com.gki.managerment.bean.WorkerGroupSV_GetDataByLineResponse;
import com.gki.managerment.http.Service.commitToService;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.DateUtils;
import com.gki.managerment.util.StringUtils;
import com.gki.managerment.util.UserTask;
import com.gki.managerment.view.SettingView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DailyworkInput extends BaseActivity implements View.OnKeyListener {

    public static final int CODE_START_QUERY_ACTIVITY_REQUEST = 10001;
    public ProdMandayList CURRENT_DATA;
    public String CUR_GROUP_ID;
    //数据是否改变过
    boolean DATA_CHANGED = false;
    private Button btn_staff; // 配员名录入跳转按钮
    private Button btn_ruler; // 定尺裁断表跳转按钮
    private Button btnBack,btnSubmit;
    private TextView tvUserName,tv_date,tv_time,tv_week;
    private EditText edtBadControl,edtBadMaterial,edtBadEqum,edtbadTrydo;
    private Button btn_stoptime,btn_discard,btn_new,btn_query;
    private SettingView moJuChangeStopTime,elementStopTime,jihuaStopTime,pinzStopTime,electroStopTime;
    private SettingView tryDoStopTime,otherStopTime,et_documentNo,et_orderNo,et_prodDate;
    private SettingView elementNum,elementName,carStyle,ct,txtProdLine,txtProdLineName,et_groupId,quantities;
    private SettingView shift,startTime,stopTime,runTime,txtKey,sv_MandayLstQty,sv_order_quantity,sv_needQty;
    private SettingView labstart,labend;
    private EditText et_remark,et_TotalScrapQty,et_TotalScrapQty2,et_TotalScrapQty3,et_TotalScrapQty4;
    private Context context;
    private GetByDocumentNoTask getByDocumentNoTask;
    private SubmitTask submitTask;
    private MyNewTask mNewTask;
    private GetDataTask getDataTask;
    private AlertDialog dialog;
    private Dialog setTimeDialog;
    private List<WorkerGroupSV_GetDataByLineResponse> groupLeader;
    private int endTimeHour = 0;
    private int endTimeMinute = 0;
    private int startTimeHour = 0;
    private int startMinute = 0;
    private java.util.Timer timer;

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        DATA_CHANGED = true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailywork_input);
        initControl();
        context = this;
        btnBack.setOnClickListener(new MyListener());
        btn_staff.setOnClickListener(new MyListener());
        btn_ruler.setOnClickListener(new MyListener());
        btnSubmit.setOnClickListener(new MyListener());
        /*btnBadMaterial.setOnClickListener(new MyListener());
 		btnBadEqum.setOnClickListener(new MyListener());*/

        btn_discard.setOnClickListener(new MyListener());
        btn_stoptime.setOnClickListener(new MyListener());
        btn_new.setOnClickListener(new MyListener());
        btn_query.setOnClickListener(new MyListener());

        et_orderNo.getEditText().setOnFocusChangeListener(
            new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    // 生产单号的编辑框失去焦点就自动获取里面的值，为带出其他值做准备
                    String ProdOrderCode;
                    if (!hasFocus) {
                        ProdOrderCode = et_orderNo.getInputData();
                        if (!ProdOrderCode.trim().equals("")) {
                            getByDocumentNoTask = new GetByDocumentNoTask();
                            getByDocumentNoTask.execute(ProdOrderCode);
                        }
                    }
                }
            });
        /*
        et_orderNo.setOnKeyListener(keyListener);
        et_prodDate.setOnKeyListener(keyListener);
        runTime.setOnKeyListener(keyListener);
        quantities.setOnKeyListener(keyListener);
        et_remark.setOnKeyListener(keyListener);
        */
    }

    private void initControl() {
        btn_staff = (Button) findViewById(R.id.btn_inputName);
        btn_ruler = (Button) findViewById(R.id.btn_inputTable);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnSubmit = (Button) findViewById(R.id.btn_dailyWork_commit);

        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvUserName.setText(LoginUser.user.User_Name);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_week = (TextView) findViewById(R.id.tv_week);
        timer = new Timer(true);
        timer.schedule(
                new java.util.TimerTask() { public void run()
                {
                    new RefreshTimeTask().execute();
                }}, 0, 1000);

        edtBadMaterial = (EditText) findViewById(R.id.badMaterial);
        edtBadMaterial.setTextColor(0xFF000000);
        edtBadEqum = (EditText) findViewById(R.id.badEqum);
        edtBadEqum.setTextColor(0xFF000000);
        edtBadControl = (EditText) findViewById(R.id.badControl);
        edtBadControl.setTextColor(0xFF000000);
        edtbadTrydo = (EditText) findViewById(R.id.badTrydo);
        edtbadTrydo.setTextColor(0xFF000000);
        txtKey = (SettingView) findViewById(R.id.txt_key);

        btn_stoptime = (Button) findViewById(R.id.btn_stoptime_re);
        btn_discard = (Button) findViewById(R.id.btn_discard);

        btn_new = (Button) findViewById(R.id.btn_new);
        btn_query = (Button) findViewById(R.id.btn_query);

        sv_order_quantity = (SettingView)findViewById(R.id.sv_order_quantity);
        sv_MandayLstQty = (SettingView)findViewById(R.id.sv_MandayLstQty);
        sv_needQty = (SettingView)findViewById(R.id.sv_needQty);
        elementStopTime = (SettingView)findViewById(R.id.view_dailywork_elemtStopTime);
        elementStopTime.getEditText().setTextColor(0xFF000000);
        jihuaStopTime = (SettingView) findViewById(R.id.view_dailywork_jihuaStopTime);
        jihuaStopTime.getEditText().setTextColor(0xFF000000);
        pinzStopTime = (SettingView) findViewById(R.id.view_dailywork_pinzConfmStopTime);
        pinzStopTime.getEditText().setTextColor(0xFF000000);
        moJuChangeStopTime = (SettingView) findViewById(R.id.view_dailywork_mojuChangeStopTime);
        moJuChangeStopTime.getEditText().setTextColor(0xFF000000);
        electroStopTime = (SettingView) findViewById(R.id.view_dailywork_eletroStopTime);
        electroStopTime.getEditText().setTextColor(0xFF000000);
        tryDoStopTime = (SettingView) findViewById(R.id.view_dailywork_tryDoStopTime);
        tryDoStopTime.getEditText().setTextColor(0xFF000000);
        otherStopTime = (SettingView) findViewById(R.id.view_dailywork_otherStopTime);
        otherStopTime.getEditText().setTextColor(0xFF000000);
        et_documentNo = (SettingView) findViewById(R.id.et_documentNo);
        et_documentNo.getEditText().setTextColor(0xFF000000);
        et_orderNo = (SettingView) findViewById(R.id.et_orderNo);// 生产单号
        et_prodDate = (SettingView) findViewById(R.id.view_dailywork_workDate);
        elementNum = (SettingView) findViewById(R.id.view_dailywork_elementNum);
        elementNum.getEditText().setTextColor(0xFF000000);
        elementName = (SettingView) findViewById(R.id.view_dailywork_elementName);
        elementName.getEditText().setTextColor(0xFF000000);
        carStyle = (SettingView) findViewById(R.id.view_dailywork_carStyle);
        carStyle.getEditText().setTextColor(0xFF000000);
        ct = (SettingView) findViewById(R.id.view_dailywork_CT);
        ct.getEditText().setTextColor(0xFF000000);
        txtProdLine = (SettingView) findViewById(R.id.view_dailywork_conductionLine);
        txtProdLineName = (SettingView) findViewById(R.id.view_LineName);
        txtProdLineName.getEditText().setTextColor(0xFF000000);
        et_groupId = (SettingView) findViewById(R.id.et_groupId);
        quantities = (SettingView) findViewById(R.id.view_dailywork_quantity);
        shift = (SettingView) findViewById(R.id.view_dailywork_shift);
        shift.getEditText().setTextColor(0xFF000000);
        startTime = (SettingView) findViewById(R.id.view_dailywork_startTime);
        stopTime = (SettingView) findViewById(R.id.view_dailywork_stopTime);
        labstart = (SettingView) findViewById(R.id.view_labstart);
        labend = (SettingView) findViewById(R.id.view_labend);
        //setupTime = (SettingView) findViewById(R.id.view_dailywork_setupTime);
        runTime = (SettingView) findViewById(R.id.view_dailywork_runTime);
        et_remark = (EditText) findViewById(R.id.et_remark);
        et_TotalScrapQty = (EditText) findViewById(R.id.et_TotalScrapQty);
        et_TotalScrapQty.setTextColor(0xFF000000);
        et_TotalScrapQty2 = (EditText) findViewById(R.id.et_TotalScrapQty2);
        et_TotalScrapQty2.setTextColor(0xFF000000);
        et_TotalScrapQty3 = (EditText) findViewById(R.id.et_TotalScrapQty3);
        et_TotalScrapQty3.setTextColor(0xFF000000);
        et_TotalScrapQty4 = (EditText) findViewById(R.id.et_TotalScrapQty4);
        et_TotalScrapQty4.setTextColor(0xFF000000);

        et_documentNo.getEditText().setEnabled(false);

        et_prodDate.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {
                            @Override
                            public void onDateTimeSet(java.util.Date date) {
                                et_prodDate.getEditText().setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date));
                                DATA_CHANGED = true;
                            }
                        })
                        .setInitialDate(new java.util.Date())
                        .build()
                        .show();
                }
            }
        });

        startTime.getEditText().setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showSetTimeDialog(startTime);
            }
        });
        stopTime.getEditText().setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showSetTimeDialog(stopTime);
            }
        });
        quantities.getEditText().setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        quantities.getEditText().setSelectAllOnFocus(true);
        /*
        setupTime.getEditText().setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        setupTime.getEditText().setSelectAllOnFocus(true);
        setupTime.getEditText().setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        */
        runTime.getEditText().setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        runTime.getEditText().setSelectAllOnFocus(true);
        runTime.getEditText().setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        groupLeader = new ArrayList<WorkerGroupSV_GetDataByLineResponse>();
    }

    private void NewDaily() {
        if (DATA_CHANGED) {
            new AlertDialog.Builder(this).setTitle("您当前的数据未提交，是否先提交再新增？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //先提交数据，然后再新增
                            commitData();
                            if (!DATA_CHANGED) {
                                doNew();
                            }
                        }
                    })
                    .setNeutralButton("忽略", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doNew();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“返回”后的操作,这里不设置没有任何操作
                        }
                    }).show();
        } else {
            doNew();
        }
    }

    private void doNew() {
        DATA_CHANGED = true;
        mNewTask = new MyNewTask();
        mNewTask.execute();
    }

    private void Query() {
        String diaryID = et_documentNo.getInputData();
        Intent intent = new Intent(this, DailyQuery.class);
        intent.putExtra("dairy_id", diaryID);
        startActivityForResult(intent, CODE_START_QUERY_ACTIVITY_REQUEST);
    }

    public boolean commitData() {
        //ProdOrderMessage message = new ProdOrderMessage();//FIXME
        if (et_documentNo.getInputData().trim().equals(""))
        {
            Toast.makeText(DailyworkInput.this, "请先【新增】一个日报！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (et_orderNo.getInputData().trim().equals(""))
        {
            Toast.makeText(DailyworkInput.this, "请先输入一个生产单！", Toast.LENGTH_SHORT).show();
            return false;
        }
        ProdMandayList message = new ProdMandayList();
        message.Document_No = et_documentNo.getInputData();
        message.Prod_Order_No = et_orderNo.getInputData();
        message.Posting_Date = et_prodDate.getInputData();
        message.Item_No = elementNum.getInputData();
        message.Description = elementName.getInputData();
        message.Prod_Line = txtProdLine.getInputData();
        message.Shift = (shift.getInputData().equals("昼") ? 0 : 1);
        if (!TextUtils.isEmpty(startTime.getInputData().toString()))
        {
            message.Starting_Time = startTime.getInputData();
        }
        if (!TextUtils.isEmpty(stopTime.getInputData().toString()))
        {
            message.Ending_Time = stopTime.getInputData();
        }
        //message.Setup_Time = (TextUtils.isEmpty(setupTime.getInputData())) ? 0 :  StringUtils.ToFloat(setupTime.getInputData());
        message.Run_Time = (TextUtils.isEmpty(runTime.getInputData())) ? 0 :  StringUtils.ToFloat(runTime.getInputData());
        message.Output_Quantity = (TextUtils.isEmpty(quantities.getInputData())) ? 0 :  StringUtils.ToFloat(quantities.getInputData());
        message.Status = 0;
        message.LabelStart = (TextUtils.isEmpty(labstart.getInputData())) ? 0 :  Integer.parseInt(labstart.getInputData());
        message.LabelEnd = (TextUtils.isEmpty(labend.getInputData())) ? 0 :  Integer.parseInt(labend.getInputData());
        message.Worker_Group_ID = groupLeader == null ? "" : groupLeader.get((int) et_groupId.getSpinner().getSelectedItemId()).Worker_Group_ID;
        message.Worker_Group_Name = groupLeader == null ? "" : groupLeader.get((int) et_groupId.getSpinner().getSelectedItemId()).Worker_Group_Name;
        message.Car_Model = carStyle.getInputData();
        message.Scrap_Quantity = (TextUtils.isEmpty(edtBadMaterial.getText().toString())) ? 0 :  StringUtils.ToFloat(edtBadMaterial.getText().toString());
        message.Scrap_Quantity2 = (TextUtils.isEmpty(edtBadEqum.getText().toString())) ? 0 :  StringUtils.ToFloat(edtBadEqum.getText().toString());
        message.Scrap_Quantity3 = (TextUtils.isEmpty(edtBadControl.getText().toString())) ? 0 :  StringUtils.ToFloat(edtBadControl.getText().toString());
        message.Scrap_Quantity4 = (TextUtils.isEmpty(edtbadTrydo.getText().toString())) ? 0 :  StringUtils.ToFloat(edtbadTrydo.getText().toString());
        message.Stop_Time = (TextUtils.isEmpty(elementStopTime.getInputData())) ? 0 : StringUtils.ToFloat(elementStopTime.getInputData());
        message.Stoptime2 = (TextUtils.isEmpty(moJuChangeStopTime.getInputData())) ? 0 :  StringUtils.ToFloat(moJuChangeStopTime.getInputData());
        message.Stoptime3 = (TextUtils.isEmpty(jihuaStopTime.getInputData())) ? 0 :  StringUtils.ToFloat(jihuaStopTime.getInputData());
        message.Stoptime4 = (TextUtils.isEmpty(electroStopTime.getInputData())) ? 0 :  StringUtils.ToFloat(electroStopTime.getInputData());
        message.Stoptime5 = (TextUtils.isEmpty(pinzStopTime.getInputData())) ? 0 :  StringUtils.ToFloat(pinzStopTime.getInputData());
        message.Stoptime6 = (TextUtils.isEmpty(otherStopTime.getInputData())) ? 0 :  StringUtils.ToFloat(otherStopTime.getInputData());
        message.Stoptime7 = (TextUtils.isEmpty(tryDoStopTime.getInputData())) ? 0 :  StringUtils.ToFloat(tryDoStopTime.getInputData());
        message.Remark = et_remark.getText().toString();
        message.key = txtKey.getInputData();
        Gson gson = new Gson();
        String strJson = gson.toJson(message);
        submitTask = new SubmitTask();
        submitTask.execute(strJson);
        DATA_CHANGED = false;
        System.out.println("toJson====>" + strJson);
        return true;
    }

    public void ClearAllText() {
        et_documentNo.getEditText().setText("");
        et_orderNo.getEditText().setText("");
        elementNum.getEditText().setText("");
        elementName.getEditText().setText("");
        //setupTime.getEditText().setText("");
        shift.getEditText().setText("");
        startTime.getEditText().setText("");
        stopTime.getEditText().setText("");
        labstart.getEditText().setText("");
        labend.getEditText().setText("");
        runTime.getEditText().setText("");
        et_groupId.getEditText().setText("");
        carStyle.getEditText().setText("");
        edtBadMaterial.setText("");
        edtBadEqum.setText("");
        edtBadControl.setText("");
        edtbadTrydo.setText("");
        et_TotalScrapQty.setText("");
        et_TotalScrapQty2.setText("");
        et_TotalScrapQty3.setText("");
        et_TotalScrapQty4.setText("");
        elementStopTime.getEditText().setText("");
        moJuChangeStopTime.getEditText().setText("");
        jihuaStopTime.getEditText().setText("");
        electroStopTime.getEditText().setText("");
        pinzStopTime.getEditText().setText("");
        otherStopTime.getEditText().setText("");
        tryDoStopTime.getEditText().setText("");
        ct.getEditText().setText("");
        quantities.getEditText().setText("");
        et_prodDate.getEditText().setText("");
        txtProdLine.getEditText().setText("");
        txtProdLineName.getEditText().setText("");
        et_remark.setText("");
        sv_order_quantity.getEditText().setText("");
        sv_MandayLstQty.getEditText().setText("");
        sv_needQty.getEditText().setText("");

    }

    public void showOptionsDialog() {
        View dialogView = View.inflate(context, R.layout.options_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        Button confirmBtn = (Button) dialogView.findViewById(R.id.btn_addtask_confim);
        Button cancelBtn = (Button) dialogView.findViewById(R.id.btn_addtask_cancel);
        final EditText etInput = (EditText) dialogView.findViewById(R.id.et_optionInput);
        TextView tvOptionName = (TextView) dialogView.findViewById(R.id.tv_optionName);
        confirmBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                DATA_CHANGED = true;
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void GetData(String strDocument)
    {
        getDataTask = new GetDataTask();
        getDataTask.execute(strDocument);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent rData) {
        //返回报废情况和停止情况
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            edtBadMaterial.setText(rData.getStringExtra("gz01"));
            edtBadEqum.setText(rData.getStringExtra("gz02"));
            edtBadControl.setText(rData.getStringExtra("gz03"));
            edtbadTrydo.setText(rData.getStringExtra("gz04"));
            GetData(et_documentNo.getInputData());
        }
        else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            elementStopTime.getEditText().setText(rData.getStringExtra("st01"));
            jihuaStopTime.getEditText().setText(rData.getStringExtra("st02"));
            pinzStopTime.getEditText().setText(rData.getStringExtra("st03"));
            moJuChangeStopTime.getEditText().setText(rData.getStringExtra("st04"));
            electroStopTime.getEditText().setText(rData.getStringExtra("st05"));
            tryDoStopTime.getEditText().setText(rData.getStringExtra("st07"));
            otherStopTime.getEditText().setText(rData.getStringExtra("st06"));
            GetData(et_documentNo.getInputData());
        }
        // 开启配员录入界面
        else if (requestCode == 30 && resultCode == Activity.RESULT_OK) {
            btn_staff.performClick();
            return;
        }
        // 开启定尺裁断界面
        else if (requestCode == 40 && resultCode == Activity.RESULT_OK) {
            btn_ruler.performClick();
            return;
        }
        //查询日报后
        else if (requestCode == CODE_START_QUERY_ACTIVITY_REQUEST && resultCode == Activity.RESULT_OK) {
            ProdMandayListSV_QueryList_Response response = (ProdMandayListSV_QueryList_Response) rData.getSerializableExtra("data");
            String strDocumentNo = response.Document_No;
            GetData(strDocumentNo);
        }
    }

    private void showDatePickerDialog(final SettingView settingView) {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(java.util.Date date) {
                        settingView.setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date));
                    }
                })
                .setInitialDate(new java.util.Date())
                .build()
                .show();
    }

    private void showSetTimeDialog(final SettingView settingView) {
        if (setTimeDialog != null) {
            setTimeDialog.dismiss();
            setTimeDialog = null;
        }
        setTimeDialog = new Dialog(this);
        View content = LayoutInflater.from(this).inflate(R.layout.set_time, null);
        final TimePicker tp = (TimePicker) content.findViewById(R.id.tp_time);
        Calendar c = Calendar.getInstance();
        tp.setIs24HourView(true);
        if (settingView.getTitle().equals(startTime.getTitle())) {
            tp.setCurrentHour(startTimeHour == 0 ? c.get(Calendar.HOUR_OF_DAY) + 13 : startTimeHour);
            tp.setCurrentMinute(startMinute == 0 ? c.get(Calendar.MINUTE) : startMinute);
        } else if (settingView.getTitle().equals(stopTime.getTitle())) {
            tp.setCurrentHour(endTimeHour == 0 ? c.get(Calendar.HOUR_OF_DAY) + 13 : endTimeHour);
            tp.setCurrentMinute(endTimeHour == 0 ? c.get(Calendar.MINUTE) : endTimeMinute);
        }

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
                settingView.setValue(getFormatedHourValue(tp.getCurrentHour()) + ":" + getFormatedMinutesValue(tp.getCurrentMinute()));
                if (settingView.getTitle().equals(startTime.getTitle())) {
                    startTimeHour = tp.getCurrentHour();
                    startMinute = tp.getCurrentMinute();
                    if (StringUtils.isEmpty(stopTime.getInputData()))
                    {
                        stopTime.setValue(getFormatedHourValue(tp.getCurrentHour() + 2) + ":" + getFormatedMinutesValue(tp.getCurrentMinute()));
                        endTimeHour = tp.getCurrentHour() + 2;
                        endTimeMinute = tp.getCurrentMinute();
                    }
                } else if (settingView.getTitle().equals(stopTime.getTitle())) {
                    endTimeHour = tp.getCurrentHour();
                    endTimeMinute = tp.getCurrentMinute();
                }
                if (StringUtils.isNotEmpty(startTime.getInputData()) && StringUtils.isNotEmpty(stopTime.getInputData()))
                {
                    int bHour = Integer.parseInt(startTime.getInputData().replace(":",""));
                    int eHour = Integer.parseInt(stopTime.getInputData().replace(":",""));
                    String strToday = DateUtils.getDataString("yyyy-MM-dd");
                    //结束时间大于开始时间
                    if (eHour >= bHour)
                    {
                        String strBDate = strToday + " " + startTime.getInputData() + ":00";
                        String strEDate = strToday + " " + stopTime.getInputData() + ":00";
                        Date dtBDate = DateUtils.parseString2Date(strBDate, DateUtils.DateFormat.FULL_DATE_FORMAT);
                        Date dtEDate = DateUtils.parseString2Date(strEDate, DateUtils.DateFormat.FULL_DATE_FORMAT);
                        runTime.getEditText().setText(String.valueOf(DateUtils.GetDiffMinutes(dtBDate, dtEDate)));
                    }
                    else {
                        //结束时间小于开始时间，实际是隔天
                        String strTomorrow = DateUtils.getDataString("yyyy-MM-dd");
                        String strBDate = strToday + " " + startTime.getInputData() + ":00";
                        Date dtBDate = DateUtils.parseString2Date(strBDate, DateUtils.DateFormat.FULL_DATE_FORMAT);
                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date());
                        c.add(Calendar.HOUR, 24);
                        String strEDate = DateUtils.getFormatDate(c.getTime(),"yyyy-MM-dd") + " " + stopTime.getInputData() + ":00";
                        Date dtEDate = DateUtils.parseString2Date(strEDate, DateUtils.DateFormat.FULL_DATE_FORMAT);
                        runTime.getEditText().setText(String.valueOf(DateUtils.GetDiffMinutes(dtBDate, dtEDate)));
                    }
                }
            }
        });
        setTimeDialog.setTitle("设置时间");
        setTimeDialog.setCanceledOnTouchOutside(true);
        setTimeDialog.setContentView(content);
        setTimeDialog.show();
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

    private void loadGroupLeader(String productLine) {
        new UserTask<String, String, String>() {
            @Override
            protected void onErrorHandle(Context context, Exception error) {
            }
            @Override
            protected void onTaskFinished(Context context, String s) {
                if (!TextUtils.isEmpty(s)) {
                    groupLeader.clear();
                    List<WorkerGroupSV_GetDataByLineResponse> responses = new Gson().fromJson(s, new TypeToken<List<WorkerGroupSV_GetDataByLineResponse>>() {
                    }.getType());
                    if (responses != null) {
                        groupLeader.addAll(responses);
                    }
                    GroupLeaderAdapter adapter = new GroupLeaderAdapter(groupLeader);
                    et_groupId.getSpinner().setAdapter(adapter);
                    for(int i = 0; i < groupLeader.size(); i++)
                    {
                        if (groupLeader.get(i).Worker_Group_ID.equals(CUR_GROUP_ID))
                        {
                            et_groupId.getSpinner().setSelection(i);
                            break;
                        }
                    }
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.WorkerGroupSV_GetDataByLine(params[0]);
            }
        }).execute(productLine);
    }

    public class MyListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.btn_back):
                    DailyworkInput.this.finish();
                    break;
                case (R.id.btn_query):
                    Query();
                    break;
                case (R.id.btn_new):
                    NewDaily();
                    break;
                //报废情况
                case (R.id.btn_discard):
                    if (DATA_CHANGED)
                    {
                        if (commitData())
                        {
                            OpenDiscard();
                        }
                    }
                    else
                    {
                        OpenDiscard();
                    }
                    break;
                //停止情况
                case (R.id.btn_stoptime_re):
                    if (DATA_CHANGED)
                    {
                        if (commitData())
                        {
                            OpenStopTime();
                        }
                    }
                    else
                    {
                        OpenStopTime();
                    }
                    break;
                //配员名录入
                case (R.id.btn_inputName):
                    if (StringUtils.isEmpty(txtKey.getInputData()) || StringUtils.isEmpty(et_orderNo.getInputData()) || StringUtils.isEmpty(txtProdLine.getInputData()) || StringUtils.isEmpty(groupLeader.get((int) et_groupId.getSpinner().getSelectedItemId()).Worker_Group_ID))
                    {
                        ShowTipMessage("请先创建日报，并输入生产单号、生产线和班组！");
                        return;
                    }
                    if (LoginUser.getUser().All_Prod_Line.indexOf(txtProdLine.getInputData()+",") == -1)
                    {

                        ShowTipMessage("【生产单】不正确：该【生产单】不属于您所在【生产线】！");
                        return;
                    }
                    if (CURRENT_DATA == null)
                    {
                        CURRENT_DATA = new ProdMandayList();
                    }
                    CURRENT_DATA.Posting_Date = et_prodDate.getInputData();
                    CURRENT_DATA.Prod_Line = txtProdLine.getInputData();
                    CURRENT_DATA.ProdLineName = txtProdLineName.getInputData();
                    CURRENT_DATA.Shift = (shift.getInputData().equals("昼") ? 0 : 1);
                    CURRENT_DATA.Worker_Group_ID = groupLeader.get((int) et_groupId.getSpinner().getSelectedItemId()).Worker_Group_ID;
                    CURRENT_DATA.Worker_Group_Name = groupLeader.get((int) et_groupId.getSpinner().getSelectedItemId()).Worker_Group_Name;
                    Intent intent = new Intent();
                    intent.setClass(context, StaffInput.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", CURRENT_DATA);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 40);
                    System.out.println("btn_inputName:" + CURRENT_DATA.Prod_Line);
                    break;
                //定尺裁断表
                case (R.id.btn_inputTable):
                    if (StringUtils.isEmpty(txtKey.getInputData()) || StringUtils.isEmpty(et_orderNo.getInputData()) || StringUtils.isEmpty(txtProdLine.getInputData()) || StringUtils.isEmpty(groupLeader.get((int) et_groupId.getSpinner().getSelectedItemId()).Worker_Group_ID))
                    {
                        ShowTipMessage("请先创建日报，并输入生产单号、生产线和班组！");
                        return;
                    }
                    if (LoginUser.getUser().All_Prod_Line.indexOf(txtProdLine.getInputData()+",") == -1)
                    {
                        ShowTipMessage("【生产单】不正确：该【生产单】不属于您所在【生产线】！");
                        return;
                    }
                    String classGroup = (et_groupId.getSpinner().getSelectedView()).getTag().toString();
                    String classGroupId = groupLeader == null || groupLeader.size() == 0 ? "" : groupLeader.get((int) et_groupId.getSpinner().getSelectedItemId()).Worker_Group_ID;
                    String productOrder = et_orderNo.getInputData();
                    String fittingID = elementNum.getInputData();
                    String diaryID = et_documentNo.getInputData();

                    Intent i = new Intent(DailyworkInput.this, RulerTable.class);
                    i.putExtra("product_line",txtProdLine.getInputData());
                    i.putExtra("prodLineName", txtProdLineName.getInputData());
                    i.putExtra("product_date", et_prodDate.getInputData());
                    i.putExtra("class_order", shift.getInputData());
                    i.putExtra("class_group", classGroup);
                    i.putExtra("class_group_id", classGroupId);
                    i.putExtra("product_order", productOrder);
                    i.putExtra("fitting_id", fittingID);
                    i.putExtra("diary_id", diaryID);
                    startActivityForResult(i, 30);
                    break;
                //提交
                case (R.id.btn_dailyWork_commit):
                    commitData();
                    break;
                default:
                    break;
            }
        }
    }

    private void OpenDiscard()
    {
        Intent intent = new Intent();
        intent.setClass(context, DailyScrap.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("blanktype", "");
        intent.putExtras(bundle);
        intent.putExtra("document_no", et_documentNo.getInputData());
        startActivityForResult(intent, 1);
    }

    private void OpenStopTime()
    {
        Intent intent = new Intent();
        intent.setClass(context, DailyStopTime.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("stoptime", "");
        intent.putExtras(bundle);
        intent.putExtra("document_no", et_documentNo.getInputData());
        startActivityForResult(intent, 2);
    }

    private class GetByDocumentNoTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return getService.GetProdOrderService(params[0]);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            if (!result.trim().equals("null")) {
                Gson gson = new Gson();
                ProdMandayList bean = gson.fromJson(result, ProdMandayList.class);
                CURRENT_DATA = bean;
                CUR_GROUP_ID = LoginUser.getUser().GroupID;
                if (LoginUser.getUser().All_Prod_Line.indexOf(bean.Production_line+",") == -1)
                {
                    ShowTipMessage("【生产单】不正确：该【生产单】不属于您所在【生产线】！");
                    et_orderNo.getEditText().setText("");
                    return;
                }
                elementName.getEditText().setText(bean.Description);
                elementNum.getEditText().setText(bean.Source_No);
                carStyle.getEditText().setText(bean.Car_Model);
                ct.getEditText().setText(bean.CT);
                txtProdLineName.getEditText().setText(bean.ProdLineName);
                txtProdLine.getEditText().setText(bean.Production_line);
                if (!TextUtils.isEmpty(txtProdLine.getInputData())) {
                    loadGroupLeader(txtProdLine.getInputData());
                }
                quantities.getEditText().setText(String.valueOf(bean.Output_Quantity));
                shift.getEditText().setText(String.valueOf(bean.Shift).equals("0") ? "昼" : "夜");
                startTime.getEditText().setText(bean.Starting_Time);
                stopTime.getEditText().setText(bean.Ending_Time);
                //labstart.getEditText().setText(String.valueOf(bean.LabelStart));
                //labend.getEditText().setText(String.valueOf(bean.LabelEnd));
                sv_order_quantity.getEditText().setText(bean.Quantity);
                sv_MandayLstQty.getEditText().setText(bean.MandayLstQty);
                sv_needQty.getEditText().setText(String.valueOf(StringUtils.ToFloat(bean.Quantity) - StringUtils.ToFloat(bean.MandayLstQty)));
            }
            else
            {
                ShowTipMessage("找不到该订单，请确认订单是否正确！");
                et_orderNo.getEditText().setText("");
            }
        }
    }

    private class SubmitTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return commitToService.SendProdMandayListService(params[0]);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("execute result:" + result);
            if (result.trim().equals("true")) {
                ShowTipMessage("提交成功！");
                DATA_CHANGED = false;
                GetData(et_documentNo.getInputData());
            } else {
                DATA_CHANGED = true;
                ShowTipMessage("提交失败！");
            }
        }
    }

    //取得明细数据
    private class GetDataTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return commitToService.GetWorkDailyReportService(params[0]);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("execute result:" + result);
            Gson gson = new Gson();
            Type lt = new TypeToken<DailyReport>() {
            }.getType();
            DailyReport lstDaily = gson.fromJson(result, lt);
            if (!lstDaily.equals(null)) {
                ClearAllText();
                CUR_GROUP_ID = lstDaily.Worker_Group_ID;
                txtKey.getEditText().setText(lstDaily.Key);
                et_documentNo.getEditText().setText(lstDaily.Document_No);
                et_orderNo.getEditText().setText(lstDaily.Prod_Order_No);
                et_prodDate.getEditText().setText(StringUtils.isEmpty(lstDaily.Posting_Date) ? "" : lstDaily.Posting_Date.substring(0,10));
                elementNum.getEditText().setText(lstDaily.Item_No);
                elementName.getEditText().setText(lstDaily.Description);
                txtProdLineName.getEditText().setText(lstDaily.Pro_line_name);
                txtProdLine.getEditText().setText(lstDaily.Prod_Line);
                if (!TextUtils.isEmpty(txtProdLine.getInputData())) {
                    loadGroupLeader(txtProdLine.getInputData());
                }
                shift.getEditText().setText(String.valueOf(lstDaily.Shift).equals("0")  ? "昼" : "夜");
                startTime.getEditText().setText(lstDaily.Starting_Time.substring(11, 16));
                stopTime.getEditText().setText(lstDaily.Ending_Time.substring(11, 16));
                labstart.getEditText().setText(Integer.toString(lstDaily.LabelStart));
                labend.getEditText().setText(Integer.toString(lstDaily.LabelEnd));
                //setupTime.getEditText().setText(Float.toString(lstDaily.Setup_Time));
                runTime.getEditText().setText(Float.toString(lstDaily.Run_Time));

                quantities.getEditText().setText(Float.toString(lstDaily.Output_Quantity));
                ct.getEditText().setText(lstDaily.CT);
                carStyle.getEditText().setText(lstDaily.car_model);
                edtBadMaterial.setText(Float.toString(lstDaily.Scrap_Quantity));
                edtBadEqum.setText(Float.toString(lstDaily.Scrap_Quantity2));
                edtBadControl.setText(Float.toString(lstDaily.Scrap_Quantity3));
                edtbadTrydo.setText(Float.toString(lstDaily.Scrap_Quantity4));

                elementStopTime.getEditText().setText(Float.toString(lstDaily.Stop_Time));
                moJuChangeStopTime.getEditText().setText(Float.toString(lstDaily.stoptime2));
                jihuaStopTime.getEditText().setText(Float.toString(lstDaily.stoptime3));
                electroStopTime.getEditText().setText(Float.toString(lstDaily.stoptime4));
                pinzStopTime.getEditText().setText(Float.toString(lstDaily.stoptime5));
                tryDoStopTime.getEditText().setText(Float.toString(lstDaily.stoptime7));
                otherStopTime.getEditText().setText(Float.toString(lstDaily.stoptime6));
                et_remark.setText(lstDaily.remark);
                sv_order_quantity.getEditText().setText(lstDaily.ProdOrderQty);
                sv_MandayLstQty.getEditText().setText(lstDaily.MandayLstQty);
                et_TotalScrapQty.setText(lstDaily.TotalScrapQty);
                et_TotalScrapQty2.setText(lstDaily.TotalScrapQty2);
                et_TotalScrapQty3.setText(lstDaily.TotalScrapQty3);
                et_TotalScrapQty4.setText(lstDaily.TotalScrapQty4);
                sv_needQty.getEditText().setText(String.valueOf(StringUtils.ToFloat(lstDaily.ProdOrderQty) - StringUtils.ToFloat(lstDaily.MandayLstQty)));
            }
        }
    }

    private class MyNewTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return commitToService.SendWorkDailyReportNewService(LoginUser.getUser().User_ID);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("execute result:" + result);
            Gson gson = new Gson();
            Type lt = new TypeToken<DailyTitle>() {
            }.getType();
            DailyTitle lstDaily = gson.fromJson(result, lt);
            if (!lstDaily.equals(null)) {
                ClearAllText();
                CURRENT_DATA = new ProdMandayList();
                CURRENT_DATA.key = lstDaily.Key;
                CURRENT_DATA.Document_No = lstDaily.Document_No;
                CURRENT_DATA.Posting_Date =lstDaily.Posting_Date.substring(0,10);
                CUR_GROUP_ID = LoginUser.getUser().GroupID;
                txtKey.getEditText().setText(lstDaily.Key);
                et_documentNo.getEditText().setText(lstDaily.Document_No);
                et_prodDate.getEditText().setText(lstDaily.Posting_Date.substring(0,10));
                et_orderNo.requestFocus();
            }
        }
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
    private class GroupLeaderAdapter extends BaseAdapter {
        private List<WorkerGroupSV_GetDataByLineResponse> lstGroupData;

        public GroupLeaderAdapter(List<WorkerGroupSV_GetDataByLineResponse> aList) {
            this.lstGroupData = aList;
        }

        @Override
        public int getCount() {
            return lstGroupData == null ? 0 : lstGroupData.size();
        }

        @Override
        public Object getItem(int position) {
            return lstGroupData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.spinner_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(lstGroupData.get(position).Worker_Group_Name);
            //holder.textView.setTag(data.get(position).Worker_Group_Name);
            return convertView;
        }

        private class ViewHolder {
            TextView textView;

            public ViewHolder(View view) {
                textView = (TextView) view.findViewById(R.id.tv_spinner_item);
            }

        }
    }
}