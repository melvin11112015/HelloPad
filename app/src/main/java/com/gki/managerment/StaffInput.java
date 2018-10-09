package com.gki.managerment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.gki.managerment.bean.ProdMandayList;
import com.gki.managerment.bean.WorkDailyReport;
import com.gki.managerment.bean.WorkerGrpMemberSVGetDataResponse;
import com.gki.managerment.global.BaseListItem;
import com.gki.managerment.global.BaseListItemParser;
import com.gki.managerment.global.ListItemAdapter;
import com.gki.managerment.http.Service.commitToService;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.DateUtils;
import com.gki.managerment.util.StringUtils;
import com.gki.managerment.util.UserTask;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class StaffInput extends BaseActivity implements OnClickListener {
    private Timer timer;
    private boolean IsFirstIn = true;
    private WorkDailyReport DefaultSet;
    private Boolean CanEdit = false;
    public int screenHeight = 0;
    public int screenWidth = 0;
    private Button btn_back, btn_add_new, btnAddRouting, btnSubmit, btn_query;
    private String PROD_LINE, PROD_DATE, GROUP_ID, SHIFT;
    private EditText et_prodDate;
    private Spinner sp_prodLine, sp_shift, sp_group;
    private ProdMandayList bean;
    private TextView tv_UserName, tv_date, tv_time, tv_week;
    private ListView listView;
    private GetDataTask getDataTask;
    private SubmitTask submitTask;
    private MyAddRoundingTask mAddRoundingTask;
    private Activity CurrentActivity;
    private Context context;
    private StaffInputListViewAdapter adapter = null;
    private List<WorkerGrpMemberSVGetDataResponse> addnewresposne = new ArrayList<WorkerGrpMemberSVGetDataResponse>();
    private AddNewAdapter addNewAdapter;
    private CheckBox chbCheckAll, chbAddNewAll;
    private CheckBox chbDayOvertime, chbNightOvertime, chbDayTempOT, chbNightTempOT, chbDayMealSub, chbNightMealSub, chbDaySub, chbNightSub;
    private List<WorkDailyReport> workDailyReportList = new ArrayList<WorkDailyReport>();

    OnItemClickListener listClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            WorkDailyReport item = workDailyReportList.get(position);
            Intent intent = new Intent();
            intent.setClass(context, StaffInputItem.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("StaffInputItem", item);
            bundle.putSerializable("DefaultSet", DefaultSet);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1000);
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_input);
        context = this;
        this.CurrentActivity = this;
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenHeight = windowManager.getDefaultDisplay().getHeight();
        screenWidth = windowManager.getDefaultDisplay().getWidth();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        CanEdit = (bundle == null);
        initControl();

        GetUserProdLine objUserProdLine = new GetUserProdLine();
        objUserProdLine.execute(LoginUser.getUser().User_ID);
        if (!CanEdit) {
            bean = (ProdMandayList) bundle.getSerializable("data");
            PROD_DATE = StringUtils.isEmpty(bean.Posting_Date) ? "" : bean.Posting_Date.substring(0, 10);
            PROD_LINE = bean.Prod_Line;
            SHIFT = String.valueOf(bean.Shift);
            GROUP_ID = bean.Worker_Group_ID;
            sp_shift.setSelection(bean.Shift);
            getDataTask = new GetDataTask();
            getDataTask.execute(PROD_LINE, PROD_DATE, SHIFT, GROUP_ID);
            loadGroup(PROD_LINE);
        } else {
            PROD_DATE = LoginUser.getUser().LoginTime.substring(0, 10);
            //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            //PROD_DATE = formatter.format(new java.sql.Date(System.currentTimeMillis()));
            PROD_LINE = LoginUser.getUser().Prod_Line;
            SHIFT = LoginUser.getUser().Shift == null ? "0" : LoginUser.getUser().Shift;
            GROUP_ID = LoginUser.getUser().GroupID;
            sp_shift.setSelection(Integer.parseInt(SHIFT));
        }
        if (GROUP_ID != null) {
            loadDefaultSet(GROUP_ID);
        }
        setConditionEnable(CanEdit);

        tv_UserName.setText(LoginUser.user.User_Name);
        tv_date.setText(LoginUser.user.LoginTime.substring(0, 10));
        et_prodDate.setText(PROD_DATE);

        btn_back.setOnClickListener(new MyListener());
        btnAddRouting.setOnClickListener(new MyListener());
        btnSubmit.setOnClickListener(new MyListener());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setConditionEnable(boolean isEnable) {
        btn_query.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        et_prodDate.setEnabled(isEnable);
        sp_prodLine.setEnabled(isEnable);
        sp_shift.setEnabled(isEnable);
        sp_group.setEnabled(isEnable);
        sp_prodLine.setFocusable(true);
        sp_prodLine.requestFocus();

        if (isEnable) {
            findViewById(R.id.btn_daily).setVisibility(View.GONE);
            findViewById(R.id.btn_ruler).setVisibility(View.GONE);
            btn_query.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetListData();
                }
            });
            sp_prodLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    PROD_LINE = ((BaseListItem) adapterView.getSelectedItem()).getItemId().toString();
                    if (!IsFirstIn) {
                        ClearListData();
                    }
                    loadGroup(PROD_LINE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            et_prodDate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(new SlideDateTimeListener() {
                                @Override
                                public void onDateTimeSet(Date date) {
                                    et_prodDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date));
                                    PROD_DATE = et_prodDate.getText().toString();
                                    if (!IsFirstIn) {
                                        ClearListData();
                                    }
                                }
                            })
                            .setInitialDate(new Date())
                            .build()
                            .show();
                }
            });
            sp_shift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SHIFT = ((BaseListItem) sp_shift.getSelectedItem()).getItemId();
                    if (!IsFirstIn) {
                        ClearListData();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            sp_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    GROUP_ID = ((BaseListItem) sp_group.getSelectedItem()).getItemId();
                    if (IsFirstIn) {
                        loadDefaultSet(GROUP_ID);
                        GetListData();
                        IsFirstIn = false;
                    } else {
                        loadDefaultSet(getGROUP_ID());
                        ClearListData();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        } else {
            findViewById(R.id.btn_daily).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    StaffInput.this.finish();
                }
            });
            findViewById(R.id.btn_ruler).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(Activity.RESULT_OK);
                    StaffInput.this.finish();
                }
            });
        }
    }

    private void ClearListData() {
        workDailyReportList.clear();
        adapter = new StaffInputListViewAdapter(CurrentActivity, workDailyReportList);
        listView.setAdapter(adapter);
    }

    private void initControl() {
        sp_prodLine = (Spinner) findViewById(R.id.sp_prodLine);
        sp_shift = (Spinner) findViewById(R.id.sp_shift);
        sp_group = (Spinner) findViewById(R.id.sp_group);
        tv_UserName = (TextView) findViewById(R.id.tv_username);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_week = (TextView) findViewById(R.id.tv_week);
        timer = new Timer(true);
        timer.schedule(
                new TimerTask() {
                    public void run() {
                        new RefreshTimeTask().execute();
                    }
                }, 0, 1000);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_query = (Button) findViewById(R.id.btn_query);
        btnAddRouting = (Button) findViewById(R.id.btn_add_Routing);
        btnSubmit = (Button) findViewById(R.id.btn_staffInput_comm);
        et_prodDate = (EditText) findViewById(R.id.et_prodDate);
        listView = (ListView) this.findViewById(R.id.lv_staffInput_add2list);
        chbCheckAll = (CheckBox) findViewById(R.id.chb_all);
        btn_add_new = (Button) findViewById(R.id.btn_add_new);

        chbDayOvertime = (CheckBox) findViewById(R.id.chbDayOvertime);
        chbNightOvertime = (CheckBox) findViewById(R.id.chbNightOvertime);
        chbDayTempOT = (CheckBox) findViewById(R.id.chbDayTempOT);
        chbNightTempOT = (CheckBox) findViewById(R.id.chbNightTempOT);
        chbDayMealSub = (CheckBox) findViewById(R.id.chbDayMealSub);
        chbNightMealSub = (CheckBox) findViewById(R.id.chbNightMealSub);
        chbDaySub = (CheckBox) findViewById(R.id.chbDaySub);
        chbNightSub = (CheckBox) findViewById(R.id.chbNightSub);

        List<BaseListItem> lstShift = new ArrayList<BaseListItem>();
        BaseListItem aShift = new BaseListItem();
        aShift.setItemId("0");
        aShift.setItemName("昼");
        lstShift.add(aShift);
        BaseListItem bShift = new BaseListItem();
        bShift.setItemId("1");
        bShift.setItemName("夜");
        lstShift.add(bShift);
        ListItemAdapter shiftAdapter = new ListItemAdapter(getBaseContext(), lstShift);
        sp_shift.setAdapter(shiftAdapter);

        chbCheckAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = chbCheckAll.isChecked();
                if (isChecked) {
                    chbCheckAll.setChecked(true);
                } else {
                    chbCheckAll.setChecked(false);
                }
                if (chbCheckAll.isChecked()) {
                    selectAll();
                } else {
                    disselectAll();
                }
            }
        });

        findViewById(R.id.btn_stuff_delete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        tv_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(new SlideDateTimeListener() {
                                @Override
                                public void onDateTimeSet(Date date) {
                                    tv_date.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date));
                                }
                            })
                            .setInitialDate(new Date())
                            .build()
                            .show();
                }
            }
        });
        btn_add_new.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewDialog();
            }
        });
        chbDayOvertime.setOnClickListener(this);
        chbNightOvertime.setOnClickListener(this);
        chbDayTempOT.setOnClickListener(this);
        chbNightTempOT.setOnClickListener(this);
        chbDayMealSub.setOnClickListener(this);
        chbNightMealSub.setOnClickListener(this);
        chbDaySub.setOnClickListener(this);
        chbNightSub.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (data == null) {
                return;
            }
            GetListData();
        }
    }

    private void GetListData() {
        getDataTask = new GetDataTask();
        getDataTask.execute(getPROD_LINE(), getPROD_DATE(), getSHIFT(), getGROUP_ID());
    }

    public void commitData() {
        Gson gson = new Gson();
        String strJson = "";
        strJson = gson.toJson(adapter.workDailyReportList);
        System.out.println("commit json:" + strJson);
        submitTask = new SubmitTask();
        submitTask.execute(strJson);
        System.out.println("toJson====>" + strJson);
    }

    //取班组默认设置信息
    private void loadDefaultSet(String strGroupId) {
        new UserTask<String, String, String>() {
            @Override
            protected void onTaskPrepare() {
                super.onTaskPrepare();
                showLoadingDialog();
            }

            @Override
            protected void onErrorHandle(Context context, Exception error) {
                ShowTipMessage("配员初始信息加载失败!");
                dismissLoadingDialog();
            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (StringUtils.isNotEmpty(s)) {
                    WorkDailyReport responses = new Gson().fromJson(s, new TypeToken<WorkDailyReport>() {
                    }.getType());
                    DefaultSet = responses;
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.GetDefaultSetup_WorkGroup(params[0]);
            }
        }).execute(strGroupId);
    }

    private void selectAll() {
        if (workDailyReportList == null) {
            return;
        }
        for (WorkDailyReport value : workDailyReportList) {
            value.is_selected = true;
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void disselectAll() {
        if (workDailyReportList == null) {
            return;
        }

        for (WorkDailyReport report : workDailyReportList) {
            report.is_selected = false;
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void checkIsSelectAll() {
        int selectedSize = 0;
        if (workDailyReportList == null) {
            return;
        }
        for (WorkDailyReport value : workDailyReportList) {
            if (value.is_selected) {
                selectedSize += 1;
            }
        }
        boolean isSelectedAll = selectedSize == workDailyReportList.size();
        chbCheckAll.setChecked(isSelectedAll);
    }

    //删除
    private void delete() {
        if (workDailyReportList == null) {
            return;
        }
        int selectedSize = 0;
        final StringBuilder build = new StringBuilder();
        for (int i = 0; i < workDailyReportList.size(); i++) {
            WorkDailyReport report = workDailyReportList.get(i);
            if (report.is_selected) {
                selectedSize += 1;
                if (report.Key != null) {
                    if (i == workDailyReportList.size() - 1) {
                        build.append(report.Key);
                    } else {
                        build.append(report.Key).append(",");
                    }
                }
            }
        }
        if (selectedSize == 0) {
            Toast.makeText(this, "请勾选要删除的数据", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (s != null && s.equalsIgnoreCase("true")) {
                    Toast.makeText(StaffInput.this, "删除成功", Toast.LENGTH_SHORT).show();
                    removeSeletedItems(build.toString());
                } else {
                    Toast.makeText(StaffInput.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.WorkDailyReportSV_DeleteData(params[0]);
            }
        }).execute(build.toString());
    }

    private void removeSeletedItems(String removedKeys) {
        if (removedKeys == null) {
            return;
        }
        String[] keys = removedKeys.split(",");
        if (keys.length == 0) {
            return;
        }
        for (int i = 0; i < workDailyReportList.size(); i++) {
            for (String key : keys) {
                if (workDailyReportList.get(i).Key.equals(key)) {
                    workDailyReportList.remove(i);
                }

            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    //新增窗口
    private void showAddNewDialog() {
        new UserTask<String, String, String>() {
            @Override
            protected void onTaskPrepare() {
                super.onTaskPrepare();
                showLoadingDialog();
            }

            @Override
            protected void onErrorHandle(Context context, Exception error) {
                ShowTipMessage("加载失败!");
                dismissLoadingDialog();
            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                addnewresposne.clear();
                Gson gson = new Gson();
                try {
                    List<WorkerGrpMemberSVGetDataResponse> temp = gson.fromJson(s, new TypeToken<List<WorkerGrpMemberSVGetDataResponse>>() {
                    }.getType());
                    if (temp != null) {
                        addnewresposne.addAll(temp);
                    }
                    final Dialog dialog = new Dialog(StaffInput.this);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setTitle("添加员工");
                    View content = LayoutInflater.from(getBaseContext()).inflate(R.layout.stuff_add_new, null);
                    chbAddNewAll = (CheckBox) content.findViewById(R.id.chb_all);
                    content.findViewById(R.id.btn_dismiss).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    content.findViewById(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<AddNewStaff> news = new ArrayList<AddNewStaff>();
                            for (WorkerGrpMemberSVGetDataResponse response : addnewresposne) {
                                if (response.isChecked) {
                                    AddNewStaff newStaff = new AddNewStaff();
                                    newStaff.Prod_Line = getPROD_LINE();
                                    newStaff.Production_Date = getPROD_DATE();
                                    newStaff.Shift = Integer.parseInt(getSHIFT());
                                    newStaff.Worker_Group_ID = response.Worker_Group_ID;
                                    newStaff.Worker_Group_Name = response.Worker_Group_Name;
                                    newStaff.Worker_ID = response.Worker_ID;
                                    newStaff.Worker_Name = response.Worker_Name;
                                    newStaff.Routing = response.Routing;
                                    news.add(newStaff);
                                }
                            }
                            dialog.dismiss();
                            if (news.size() == 0) {
                                return;
                            }
                            newData(new Gson().toJson(news));
                        }
                    });
                    chbAddNewAll.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            boolean isChecked = chbAddNewAll.isChecked();
                            if (isChecked) {
                                for (WorkerGrpMemberSVGetDataResponse response : addnewresposne) {
                                    response.isChecked = true;
                                }
                            } else {
                                for (WorkerGrpMemberSVGetDataResponse response : addnewresposne) {
                                    response.isChecked = false;
                                }
                            }
                            addNewAdapter.notifyDataSetChanged();
                        }
                    });
                    dialog.show();
                    ListView lv = (ListView) content.findViewById(R.id.list);
                    addNewAdapter = new AddNewAdapter();
                    lv.setAdapter(addNewAdapter);
                    dialog.setContentView(content);
                    final WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
                    attrs.width = (int) (screenWidth * 0.4);
                    attrs.height = (int) (screenHeight * 0.7);
                    dialog.getWindow().setAttributes(attrs);
                } catch (Exception ex) {
                    String strMsg = ex.getMessage();
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.WorkerGrpMemberSV_GetData("");
            }
        }).execute("");
    }

    private void checkAddNewIsSelectAll() {
        int checkedSize = 0;
        for (WorkerGrpMemberSVGetDataResponse response : addnewresposne) {
            if (response.isChecked) {
                checkedSize += 1;
            }
        }
        if (checkedSize == addnewresposne.size()) {
            chbAddNewAll.setChecked(true);
        } else {
            chbAddNewAll.setChecked(false);
        }
    }

    private void newData(String data) {
        new UserTask<String, String, String>() {

            @Override
            protected void onTaskPrepare() {
                super.onTaskPrepare();
                showLoadingDialog();
            }

            @Override
            protected void onErrorHandle(Context context, Exception error) {
                dismissLoadingDialog();
                ShowTipMessage("提交失败!", error.getMessage());
            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                GetListData();
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.InsertData_Staff(params[0], getGROUP_ID());
            }
        }).execute(data);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chbDayOvertime) {
            if (workDailyReportList != null) {
                if (DefaultSet == null) {
                    DefaultSet = new WorkDailyReport();
                }
                if (chbDayOvertime.isChecked()) {
                    for (WorkDailyReport report : workDailyReportList) {
                        if (!report.Over_Time_Day_Request) {
                            report.Over_Time_Day = DefaultSet.Def_Over_Time_Day;
                        }
                        report.Over_Time_Day_Request = true;
                        report.Over_Time = report.Over_Time_Day + report.Over_Time_Night + report.Over_Time_Day_Temp + report.Over_Time_Night_Temp;
                    }
                } else {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Over_Time_Day = 0;
                        report.Over_Time_Day_Request = false;
                        report.Over_Time = report.Over_Time_Day + report.Over_Time_Night + report.Over_Time_Day_Temp + report.Over_Time_Night_Temp;
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        if (v.getId() == R.id.chbNightOvertime) {
            if (workDailyReportList != null) {
                if (chbNightOvertime.isChecked()) {
                    for (WorkDailyReport report : workDailyReportList) {
                        if (!report.Over_Time_Night_Request) {
                            report.Over_Time_Night = DefaultSet.Def_Over_Time_Night;
                        }
                        report.Over_Time_Night_Request = true;
                        report.Over_Time = report.Over_Time_Day + report.Over_Time_Night + report.Over_Time_Day_Temp + report.Over_Time_Night_Temp;
                    }
                } else {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Over_Time_Night = 0;
                        report.Over_Time_Night_Request = false;
                        report.Over_Time = report.Over_Time_Day + report.Over_Time_Night + report.Over_Time_Day_Temp + report.Over_Time_Night_Temp;
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        if (v.getId() == R.id.chbDayTempOT) {
            if (workDailyReportList != null) {
                if (chbDayTempOT.isChecked()) {
                    for (WorkDailyReport report : workDailyReportList) {
                        if (!report.Over_Time_Day_Temp_Request) {
                            report.Over_Time_Day_Temp = DefaultSet.Def_Over_Time_Day_Temp;
                        }
                        report.Over_Time_Day_Temp_Request = true;
                        report.Over_Time = report.Over_Time_Day + report.Over_Time_Night + report.Over_Time_Day_Temp + report.Over_Time_Night_Temp;
                    }
                } else {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Over_Time_Day_Temp = 0;
                        report.Over_Time_Day_Temp_Request = false;
                        report.Over_Time = report.Over_Time_Day + report.Over_Time_Night + report.Over_Time_Day_Temp + report.Over_Time_Night_Temp;
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        if (v.getId() == R.id.chbNightTempOT) {
            if (workDailyReportList != null) {
                if (chbNightTempOT.isChecked()) {
                    for (WorkDailyReport report : workDailyReportList) {
                        if (!report.Over_Time_Night_Temp_Request) {
                            report.Over_Time_Night_Temp = DefaultSet.Def_Over_Time_Night_Temp;
                        }
                        report.Over_Time_Night_Temp_Request = true;
                        report.Over_Time = report.Over_Time_Day + report.Over_Time_Night + report.Over_Time_Day_Temp + report.Over_Time_Night_Temp;
                    }
                } else {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Over_Time_Night_Temp = 0;
                        report.Over_Time_Night_Temp_Request = false;
                        report.Over_Time = report.Over_Time_Day + report.Over_Time_Night + report.Over_Time_Day_Temp + report.Over_Time_Night_Temp;
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        if (v.getId() == R.id.chbDayMealSub) {
            if (workDailyReportList != null) {
                if (chbDayMealSub.isChecked()) {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Meal_Allowance_Day_Req = true;
                    }
                } else {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Meal_Allowance_Day_Req = false;
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        if (v.getId() == R.id.chbNightMealSub) {
            if (workDailyReportList != null) {
                if (chbNightMealSub.isChecked()) {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Meal_Allowance_Night_Req = true;
                    }
                } else {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Meal_Allowance_Night_Req = false;
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        if (v.getId() == R.id.chbDaySub) {
            if (workDailyReportList != null) {
                if (chbDaySub.isChecked()) {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Allowance_Day_Req = true;
                    }
                } else {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Allowance_Day_Req = false;
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        if (v.getId() == R.id.chbNightSub) {
            if (workDailyReportList != null) {
                if (chbNightSub.isChecked()) {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Allowance_Night_Req = true;
                    }
                } else {
                    for (WorkDailyReport report : workDailyReportList) {
                        report.Allowance_Night_Req = false;
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "StaffInput Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.gki.managerment/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "StaffInput Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.gki.managerment/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class MyListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.btn_back):
                    StaffInput.this.finish();
                    break;
                case (R.id.btn_staffInput_comm):
                    commitData();
                    break;
                case (R.id.btn_add_Routing):
                    mAddRoundingTask = new MyAddRoundingTask();
                    mAddRoundingTask.execute(getPROD_LINE(), getPROD_DATE(), getSHIFT(), getGROUP_ID());
                    break;
                default:
                    break;
            }
        }
    }

    private class GetDataTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            System.out.println(params[0] + "|" + params[1] + "|" + params[2]);
            String result = getService.GetData_Staff(params[0], params[1], params[2], params[3]);
            return result;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("onPostExecute begin:" + result);
            workDailyReportList.clear();
            if (!result.trim().equals("null") && !result.trim().equals("[]")) {
                System.out.println(result);
                Gson gson = new Gson();
                Type lt = new TypeToken<List<WorkDailyReport>>() {
                }.getType();
                List<WorkDailyReport> lstStaff = gson.fromJson(result, lt);
                if (lstStaff != null) {
                    workDailyReportList.addAll(lstStaff);
                }
                adapter = new StaffInputListViewAdapter(CurrentActivity, workDailyReportList);
                listView.setOnItemClickListener(listClickListener);
                listView.setAdapter(adapter);
            } else {
                listView.setAdapter(adapter);
            }
            dismissLoadingDialog();
        }

        @Override
        protected void onPreExecute() {
            showLoadingDialog();
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showLoadingDialog();
        }
    }

    //提交
    private class SubmitTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = commitToService.SendWorkDailyReportUpdateService(params[0]);
            return result;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("execute result:" + result);
            if (result.trim().equals("true")) {
                ShowTipMessage("提交成功！");
                GetListData();
            } else {
                ShowTipMessage("提交失败！", result);
            }
        }
    }

    //初始配员
    private class MyAddRoundingTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String result = getService.InitBYService(params[0], params[1], params[2], params[3]);
                return result;
            } catch (Exception ex) {
                String strMsg = ex.getMessage();
                return null;
            }
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                System.out.println("MyAddRoundingTask1");
                workDailyReportList.clear();
                if (result.trim().equals("error")) {
                    ShowTipMessage("当前班组成员在其他班次已经初始化！");
                    return;
                }
                if (!result.trim().equals("null") && !result.trim().equals("[]")) {
                    System.out.println("MyAddRoundingTask2" + result);
                    Gson gson = new Gson();
                    Type lt = new TypeToken<List<WorkDailyReport>>() {
                    }.getType();
                    List<WorkDailyReport> lstStaff = gson.fromJson(result, lt);
                    if (lstStaff != null) {
                        workDailyReportList.addAll(lstStaff);
                    }
                    adapter = new StaffInputListViewAdapter(
                            CurrentActivity, workDailyReportList);
                    listView.setAdapter(adapter);
                }
            }
        }
    }

    public class StaffInputListViewAdapter extends BaseAdapter {

        public List<WorkDailyReport> workDailyReportList;
        Activity activity;

        public StaffInputListViewAdapter(Activity activity, List<WorkDailyReport> workDailyReportList) {
            super();
            this.activity = activity;
            this.workDailyReportList = workDailyReportList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return workDailyReportList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return workDailyReportList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                System.out.println("marcus1");
                convertView = inflater.inflate(R.layout.staff_item, null);
                holder = new ViewHolder();
                holder.view_Routing = (TextView) convertView.findViewById(R.id.view_Routing);
                holder.view_Worker_ID = (TextView) convertView.findViewById(R.id.view_Worker_ID);
                holder.view_Worker_Name = (TextView) convertView.findViewById(R.id.view_Worker_Name);
                holder.view_Working_Time = (EditText) convertView.findViewById(R.id.view_Working_Time);
                holder.view_Over_Time = (EditText) convertView.findViewById(R.id.view_Over_Time);
                holder.view_Support_Time = (CheckBox) convertView.findViewById(R.id.view_Support_Time);
                holder.view_Over_Time_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Request);
                holder.view_Over_Time_Approval = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Approval);
                holder.view_Over_Time_Day = (EditText) convertView.findViewById(R.id.view_Over_Time_Day);
                holder.view_Over_Time_Night = (EditText) convertView.findViewById(R.id.view_Over_Time_Night);
                holder.view_Over_Time_Day_Temp = (EditText) convertView.findViewById(R.id.view_Over_Time_Day_Temp);
                holder.view_Over_Time_Night_Temp = (EditText) convertView.findViewById(R.id.view_Over_Time_Night_Temp);
                holder.view_Over_Time_Day_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Day_Request);
                holder.view_Over_Time_Night_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Night_Request);
                holder.view_Over_Time_Day_Temp_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Day_Temp_Request);
                holder.view_Over_Time_Night_Temp_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Night_Temp_Request);
                holder.view_Leave_Category_Code = (TextView) convertView.findViewById(R.id.view_Leave_Category_Code);
                holder.view_Leave_Category_Description = (TextView) convertView.findViewById(R.id.view_Leave_Category_Description);
                holder.view_Leave_Hours = (EditText) convertView.findViewById(R.id.view_Leave_Hours);
                holder.view_Meal_Allowance_Day_Req = (CheckBox) convertView.findViewById(R.id.view_Meal_Allowance_Day_Req);
                holder.view_Meal_Allowance_Night_Req = (CheckBox) convertView.findViewById(R.id.view_Meal_Allowance_Night_Req);
                holder.view_Allowance_Day_Req = (CheckBox) convertView.findViewById(R.id.view_Allowance_Day_Req);
                holder.view_Allowance_Night_Req = (CheckBox) convertView.findViewById(R.id.view_Allowance_Night_Req);
                holder.chb = (CheckBox) convertView.findViewById(R.id.chb_select);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final WorkDailyReport report = workDailyReportList.get(position);
            holder.view_Routing.setText(report.Worker_Position_Description);
            holder.view_Worker_ID.setText(report.Worker_ID);
            holder.view_Worker_Name.setText(report.Worker_Name);
            holder.view_Working_Time.setText(report.Working_Time + "");
            holder.view_Over_Time.setText(report.Over_Time + "");
            holder.view_Support_Time.setChecked(report.Support_Time);
            holder.view_Over_Time_Request.setChecked(report.Over_Time_Request);
            holder.view_Over_Time_Approval.setChecked(report.Over_Time_Approval);
            holder.view_Over_Time_Day.setText(report.Over_Time_Day + "");
            holder.view_Over_Time_Night.setText(report.Over_Time_Night + "");
            holder.view_Over_Time_Day_Temp.setText(report.Over_Time_Day_Temp + "");
            holder.view_Over_Time_Night_Temp.setText(report.Over_Time_Night_Temp + "");
            holder.view_Over_Time_Day_Request.setChecked(report.Over_Time_Day_Request);
            holder.view_Over_Time_Night_Request.setChecked(report.Over_Time_Night_Request);
            holder.view_Over_Time_Day_Temp_Request.setChecked(report.Over_Time_Day_Temp_Request);
            holder.view_Over_Time_Night_Temp_Request.setChecked(report.Over_Time_Night_Temp_Request);
            holder.view_Leave_Category_Code.setText(report.Leave_Category_Code == null || report.Leave_Category_Code.equalsIgnoreCase("null") ? "" : report.Leave_Category_Code);
            holder.view_Leave_Category_Description.setText(report.Leave_Category_Description == null || report.Leave_Category_Description.equalsIgnoreCase("null") ? "" : report.Leave_Category_Description);
            holder.view_Leave_Hours.setText(report.Leave_Hours + "");
            holder.view_Meal_Allowance_Day_Req.setChecked(report.Meal_Allowance_Day_Req);
            holder.view_Meal_Allowance_Night_Req.setChecked(report.Meal_Allowance_Night_Req);
            holder.view_Allowance_Day_Req.setChecked(report.Allowance_Day_Req);
            holder.view_Allowance_Night_Req.setChecked(report.Allowance_Night_Req);
            holder.chb.setChecked(report.is_selected);
            holder.chb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    report.is_selected = !report.is_selected;
                    notifyDataSetChanged();
                    checkIsSelectAll();
                }
            });

            holder.view_Support_Time.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.view_Support_Time.isChecked();
                    report.Support_Time = isChecked;
                    notifyDataSetChanged();
                }
            });

            holder.view_Over_Time_Day_Request.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.view_Over_Time_Day_Request.isChecked();
                    if (isChecked) {
                        report.Over_Time_Day_Request = true;
                        if (DefaultSet != null) {
                            report.Over_Time_Day = DefaultSet.Def_Over_Time_Day;
                        }
                    } else {
                        report.Over_Time_Day_Request = false;
                        report.Over_Time_Day = 0;
                    }
                    report.Over_Time = report.Over_Time_Day + report.Over_Time_Day_Temp + report.Over_Time_Night + report.Over_Time_Night_Temp;
                    notifyDataSetChanged();
                    int selectedCount = 0;
                    for (WorkDailyReport r : workDailyReportList) {
                        if (r.Over_Time_Day_Request) {
                            selectedCount += 1;
                        }
                    }
                    if (selectedCount == workDailyReportList.size()) {
                        chbDayOvertime.setChecked(true);
                    } else {
                        chbDayOvertime.setChecked(false);
                    }
                }
            });
            holder.view_Over_Time_Night_Request.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.view_Over_Time_Night_Request.isChecked();
                    if (isChecked) {
                        report.Over_Time_Night_Request = true;
                        if (DefaultSet != null) {
                            report.Over_Time_Night = DefaultSet.Def_Over_Time_Night;
                        }
                    } else {
                        report.Over_Time_Night_Request = false;
                        report.Over_Time_Night = 0;
                    }
                    report.Over_Time = report.Over_Time_Day + report.Over_Time_Day_Temp + report.Over_Time_Night + report.Over_Time_Night_Temp;
                    notifyDataSetChanged();
                    int selectedCount = 0;
                    for (WorkDailyReport r : workDailyReportList) {
                        if (r.Over_Time_Night_Request) {
                            selectedCount += 1;
                        }
                    }
                    if (selectedCount == workDailyReportList.size()) {
                        chbNightOvertime.setChecked(true);
                    } else {
                        chbNightOvertime.setChecked(false);
                    }
                }
            });
            holder.view_Over_Time_Day_Temp_Request.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.view_Over_Time_Day_Temp_Request.isChecked();
                    if (isChecked) {
                        report.Over_Time_Day_Temp_Request = true;
                        if (DefaultSet != null) {
                            report.Over_Time_Day_Temp = DefaultSet.Def_Over_Time_Day_Temp;
                        }
                    } else {
                        report.Over_Time_Day_Temp_Request = false;
                        report.Over_Time_Day_Temp = 0;
                    }
                    report.Over_Time = report.Over_Time_Day + report.Over_Time_Day_Temp + report.Over_Time_Night + report.Over_Time_Night_Temp;
                    notifyDataSetChanged();
                    int selectedCount = 0;
                    for (WorkDailyReport r : workDailyReportList) {
                        if (r.Over_Time_Day_Temp_Request) {
                            selectedCount += 1;
                        }
                    }
                    if (selectedCount == workDailyReportList.size()) {
                        chbDayTempOT.setChecked(true);
                    } else {
                        chbDayTempOT.setChecked(false);
                    }
                }
            });
            holder.view_Over_Time_Night_Temp_Request.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.view_Over_Time_Night_Temp_Request.isChecked();
                    if (isChecked) {
                        report.Over_Time_Night_Temp_Request = true;
                        if (DefaultSet != null) {
                            report.Over_Time_Night_Temp = DefaultSet.Def_Over_Time_Night_Temp;
                        }
                    } else {
                        report.Over_Time_Night_Temp_Request = false;
                        report.Over_Time_Night_Temp = 0;
                    }
                    report.Over_Time = report.Over_Time_Day + report.Over_Time_Day_Temp + report.Over_Time_Night + report.Over_Time_Night_Temp;
                    notifyDataSetChanged();
                    int selectedCount = 0;
                    for (WorkDailyReport r : workDailyReportList) {
                        if (r.Over_Time_Night_Temp_Request) {
                            selectedCount += 1;
                        }
                    }
                    if (selectedCount == workDailyReportList.size()) {
                        chbNightTempOT.setChecked(true);
                    } else {
                        chbNightTempOT.setChecked(false);
                    }
                }
            });

            holder.view_Meal_Allowance_Day_Req.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.view_Meal_Allowance_Day_Req.isChecked();
                    if (isChecked) {
                        report.Meal_Allowance_Day_Req = true;
                    } else {
                        report.Meal_Allowance_Day_Req = false;
                    }
                    notifyDataSetChanged();
                    int selectedCount = 0;
                    for (WorkDailyReport r : workDailyReportList) {
                        if (r.Meal_Allowance_Day_Req) {
                            selectedCount += 1;
                        }
                    }
                    if (selectedCount == workDailyReportList.size()) {
                        chbDayMealSub.setChecked(true);
                    } else {
                        chbDayMealSub.setChecked(false);
                    }
                }
            });

            holder.view_Meal_Allowance_Night_Req.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.view_Meal_Allowance_Night_Req.isChecked();
                    if (isChecked) {
                        report.Meal_Allowance_Night_Req = true;
                    } else {
                        report.Meal_Allowance_Night_Req = false;
                    }
                    notifyDataSetChanged();
                    int selectedCount = 0;
                    for (WorkDailyReport r : workDailyReportList) {
                        if (r.Meal_Allowance_Night_Req) {
                            selectedCount += 1;
                        }
                    }
                    if (selectedCount == workDailyReportList.size()) {
                        chbNightMealSub.setChecked(true);
                    } else {
                        chbNightMealSub.setChecked(false);
                    }
                }
            });

            holder.view_Allowance_Day_Req.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.view_Allowance_Day_Req.isChecked();
                    if (isChecked) {
                        report.Allowance_Day_Req = true;
                    } else {
                        report.Allowance_Day_Req = false;
                    }
                    notifyDataSetChanged();
                    int selectedCount = 0;
                    for (WorkDailyReport r : workDailyReportList) {
                        if (r.Allowance_Day_Req) {
                            selectedCount += 1;
                        }
                    }
                    if (selectedCount == workDailyReportList.size()) {
                        chbDaySub.setChecked(true);
                    } else {
                        chbDaySub.setChecked(false);
                    }
                }
            });

            holder.view_Allowance_Night_Req.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.view_Allowance_Night_Req.isChecked();
                    if (isChecked) {
                        report.Allowance_Night_Req = true;
                    } else {
                        report.Allowance_Night_Req = false;
                    }
                    notifyDataSetChanged();
                    int selectedCount = 0;
                    for (WorkDailyReport r : workDailyReportList) {
                        if (r.Allowance_Night_Req) {
                            selectedCount += 1;
                        }
                    }
                    if (selectedCount == workDailyReportList.size()) {
                        chbNightSub.setChecked(true);
                    } else {
                        chbNightSub.setChecked(false);
                    }
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView view_Routing;
            TextView view_Worker_ID;
            TextView view_Worker_Name;
            EditText view_Working_Time;
            EditText view_Over_Time;
            CheckBox view_Support_Time;
            CheckBox view_Over_Time_Request;
            CheckBox view_Over_Time_Approval;
            EditText view_Over_Time_Day;
            EditText view_Over_Time_Night;
            EditText view_Over_Time_Day_Temp;
            EditText view_Over_Time_Night_Temp;
            CheckBox view_Over_Time_Day_Request;
            CheckBox view_Over_Time_Night_Request;
            CheckBox view_Over_Time_Day_Temp_Request;
            CheckBox view_Over_Time_Night_Temp_Request;
            TextView view_Leave_Category_Code;
            TextView view_Leave_Category_Description;
            EditText view_Leave_Hours;
            CheckBox view_Meal_Allowance_Day_Req;
            CheckBox view_Meal_Allowance_Night_Req;
            CheckBox view_Allowance_Day_Req;
            CheckBox view_Allowance_Night_Req;
            CheckBox chb;
        }
    }

    private class AddNewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return addnewresposne.size();
        }

        @Override
        public Object getItem(int position) {
            return addnewresposne.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final AddNewViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.stuff_add_new_list_item, null);
                holder = new AddNewViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (AddNewViewHolder) convertView.getTag();
            }
            final WorkerGrpMemberSVGetDataResponse response = addnewresposne.get(position);
            holder.chb.setChecked(response.isChecked);
            holder.chb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = holder.chb.isChecked();
                    if (checked) {
                        response.isChecked = true;
                    } else {
                        response.isChecked = false;
                    }
                    checkAddNewIsSelectAll();
                    notifyDataSetChanged();
                }
            });
            holder.group.setText(response.Worker_Group_Name);
            holder.name.setText(response.Worker_Name);
            return convertView;
        }
    }

    private class AddNewViewHolder {
        CheckBox chb;
        TextView name, group;

        public AddNewViewHolder(View view) {
            chb = (CheckBox) view.findViewById(R.id.chb_all);
            name = (TextView) view.findViewById(R.id.name);
            group = (TextView) view.findViewById(R.id.group);
        }
    }

    private class AddNewStaff {
        public String Prod_Line;
        public String Production_Date;
        public int Shift;
        public String Worker_Group_ID;
        public String Worker_Group_Name;
        public String Worker_ID;
        public String Worker_Name;
        public int Routing;
        public String RoutingName;
    }

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
                List<BaseListItem> lstProdLine = new BaseListItemParser().getListFromJson(result, "Prod_Line", "Prod_Line_Name", false);
                ListItemAdapter adapter = new ListItemAdapter(getBaseContext(), lstProdLine);
                sp_prodLine.setAdapter(adapter);
                for (int i = 0; i < lstProdLine.size(); i++) {
                    if (lstProdLine.get(i).getItemId().toString().equals(PROD_LINE)) {
                        sp_prodLine.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    String getPROD_LINE() {
        try {
            return CanEdit ? ((BaseListItem) sp_prodLine.getSelectedItem()).getItemId() : this.PROD_LINE;
        } catch (Exception ex) {
            return this.PROD_LINE;
        }
    }

    String getPROD_DATE() {
        try {
            return CanEdit ? et_prodDate.getText().toString() : this.PROD_DATE;
        } catch (Exception ex) {
            return this.PROD_LINE;
        }
    }

    String getSHIFT() {
        try {
            return CanEdit ? ((BaseListItem) sp_shift.getSelectedItem()).getItemId() : this.SHIFT;
        } catch (Exception ex) {
            return this.PROD_LINE;
        }
    }

    String getGROUP_ID() {
        try {
            return CanEdit ? ((BaseListItem) sp_group.getSelectedItem()).getItemId() : this.GROUP_ID;
        } catch (Exception ex) {
            return this.PROD_LINE;
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

    private void loadGroup(String productLine) {
        new UserTask<String, String, String>() {
            @Override
            protected void onErrorHandle(Context context, Exception error) {

            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                if (!TextUtils.isEmpty(s)) {
                    List<BaseListItem> lstData = new BaseListItemParser().getListFromJson(s, "Worker_Group_ID", "Worker_Group_Name", false);
                    ListItemAdapter lstAdapter = new ListItemAdapter(getBaseContext(), lstData);
                    sp_group.setAdapter(lstAdapter);
                    for (int i = 0; i < lstData.size(); i++) {
                        if (lstData.get(i).ItemId.equals(GROUP_ID)) {
                            sp_group.setSelection(i);
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
}