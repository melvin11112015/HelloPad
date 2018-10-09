package com.gki.managerment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.gki.managerment.bean.LengthCutOffDetailElement;
import com.gki.managerment.bean.LengthCutOffList;
import com.gki.managerment.bean.SubmitRulerTableElement;
import com.gki.managerment.bean.WorkerGroupSV_GetDataByLineResponse;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.DateUtils;
import com.gki.managerment.util.StringUtils;
import com.gki.managerment.util.UserTask;
import com.gki.managerment.view.SettingView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class RulerTable extends BaseActivity implements OnClickListener {
    private static final int CAMERA_REQUEST = 1888;
    private Button btnBack, btnAdd, btnDel, btnSubmit;
    private SettingView svProdLineName, svProductDate, svClassOrder, svClassGroup, svstrOrderNo, svFittingID;
    private String PROD_LINE, PROD_LINE_NAME, PROD_DATE, SHIFT, GROUP_NAME, GROUP_ID, ORDER_NO, fittingID, DOCUMENT_NO;
    private TextView tvUserName, tvDate;
    private ListView lvData;
    private RulerAdapter rulerAdapter;
    private Dialog dialog, takePhotoDialog;
    private View dialogContentView, takePhotoDialogContentView;
    private Button dialogContentViewCancel, dialogContentViewConfirm;
    private CheckBox chbDialogContentViewCheck, chbDetailCheckAll;
    private List<LengthCutOffList.Element> toBeAddedList;
    private List<LengthCutOffDetailElement> detailElementList;
    private List<WorkerGroupSV_GetDataByLineResponse> groupLeader = new ArrayList<WorkerGroupSV_GetDataByLineResponse>();
    private LengthCutOffDetailElement currentPicElement;
    private ImageView cover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ruler_table);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new MyListener());
        svProdLineName = (SettingView) findViewById(R.id.et_prodLineName);
        svProductDate = (SettingView) findViewById(R.id.view_rulerTable_conductDate);
        svClassOrder = (SettingView) findViewById(R.id.view_rulerTable_class);
        svClassGroup = (SettingView) findViewById(R.id.view_rulerTable_classes);
        svstrOrderNo = (SettingView) findViewById(R.id.view_staffInput_conductNum);
        svFittingID = (SettingView) findViewById(R.id.view_ruleTable_elementNum);
        tvUserName = (TextView) findViewById(R.id.tv_username);
        lvData = (ListView) findViewById(R.id.lv_rulerTable_matrialList);
        tvUserName.setText(LoginUser.getUser().User_Name);
        tvDate = (TextView) findViewById(R.id.tv_date);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnDel = (Button) findViewById(R.id.btn_del);
        chbDetailCheckAll = (CheckBox) findViewById(R.id.chb_detail_check_all);
        btnSubmit = (Button) findViewById(R.id.btn_rulerTable_commit);

        btnAdd.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        chbDetailCheckAll.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        findViewById(R.id.btn_inputName).setOnClickListener(this);
        findViewById(R.id.btn_daily).setOnClickListener(this);
        tvDate.setText(LoginUser.user.LoginTime.substring(0,10));

        PROD_LINE = getIntent().getStringExtra("product_line");
        PROD_LINE_NAME = getIntent().getStringExtra("prodLineName");
        PROD_DATE = getIntent().getStringExtra("product_date");
        SHIFT = getIntent().getStringExtra("class_order");
        GROUP_NAME = getIntent().getStringExtra("class_group");
        GROUP_ID = getIntent().getStringExtra("class_group_id");
        ORDER_NO = getIntent().getStringExtra("product_order");
        fittingID = getIntent().getStringExtra("fitting_id");
        DOCUMENT_NO = getIntent().getStringExtra("diary_id");

        svProdLineName.setValue(PROD_LINE_NAME == null ? "" : PROD_LINE_NAME);
        svProductDate.setValue(PROD_DATE == null ? "" : PROD_DATE);
        svClassOrder.setValue(SHIFT == null ? "" : SHIFT);
        svClassGroup.setValue(GROUP_ID == null ? "" : GROUP_ID);
        svstrOrderNo.setValue(ORDER_NO == null ? "" : ORDER_NO);
        svFittingID.setValue(fittingID == null ? "" : fittingID);

        if (PROD_LINE != null) {
            loadGroupLeader(PROD_LINE);
        }
        loadData();
    }

    public <T> void jump(Class<T> MyClass) {
        Intent intent = new Intent();
        intent.setClass(this, MyClass);
        startActivity(intent);
    }

    private void loadData() {
        if (DOCUMENT_NO == null) {
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
                Toast.makeText(RulerTable.this, "加载失败", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                Gson gson = new Gson();
                detailElementList = gson.fromJson(s, new TypeToken<List<LengthCutOffDetailElement>>() {
                }.getType());
                rulerAdapter = new RulerAdapter(RulerTable.this, detailElementList);
                lvData.setAdapter(rulerAdapter);
                rulerAdapter.notifyDataSetChanged();
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.ProdOrderCompSVGetDetail(DOCUMENT_NO);
            }
        }).execute(DOCUMENT_NO);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                showAddDialog();
                break;
            case R.id.btn_del:
                delete();
                break;
            case R.id.btn_daily:
                RulerTable.this.finish();
                break;
            case R.id.btn_inputName:
                setResult(Activity.RESULT_OK);
                RulerTable.this.finish();
                break;
            case R.id.chb_detail_check_all:
                checkAllDetailOrNot();
                break;
            case R.id.btn_rulerTable_commit:
                v.requestFocus();
                submit();
                break;
        }
    }
    //显示新增窗口
    private void showAddDialog() {
        if (ORDER_NO == null) {
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
                Toast.makeText(RulerTable.this, "加载失败", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                Gson gson = new Gson();
                toBeAddedList = gson.fromJson(s, new TypeToken<List<LengthCutOffList.Element>>() {
                }.getType());
                final DialogAdapter adapter = new DialogAdapter(RulerTable.this, toBeAddedList);
                dialog = null;
                dialog = new Dialog(RulerTable.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("添加材料");
                dialogContentView = LayoutInflater.from(getBaseContext()).inflate(R.layout.ruler_add, null);
                dialogContentViewCancel = (Button) dialogContentView.findViewById(R.id.btn_cancel);
                dialogContentViewCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                chbDialogContentViewCheck = (CheckBox) dialogContentView.findViewById(R.id.chb_check_all);
                dialogContentViewConfirm = (Button) dialogContentView.findViewById(R.id.btn_confirm);
                dialogContentViewConfirm.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (detailElementList == null) {
                            detailElementList = new ArrayList<LengthCutOffDetailElement>();
                        }
                        if (toBeAddedList != null) {
                            for (LengthCutOffList.Element element : toBeAddedList) {
                                if (element.isSelected) {
                                    LengthCutOffDetailElement lengthCutOffDetailElement = new LengthCutOffDetailElement();
                                    lengthCutOffDetailElement.RAW_Description = element.Description;
                                    lengthCutOffDetailElement.RAW_No = element.Item_No;
                                    detailElementList.add(lengthCutOffDetailElement);
                                }
                            }
                            rulerAdapter.notifyDataSetChanged();
                        }
                    }
                });
                chbDialogContentViewCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isChecked = chbDialogContentViewCheck.isChecked();
                        if (isChecked) {
                            for (LengthCutOffList.Element element : toBeAddedList) {
                                element.isSelected = true;
                            }
                            chbDialogContentViewCheck.setChecked(true);
                        } else {
                            for (LengthCutOffList.Element element : toBeAddedList) {
                                element.isSelected = false;
                            }
                            chbDialogContentViewCheck.setChecked(false);

                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.show();
                ListView lv = (ListView) dialogContentView.findViewById(R.id.lv_data);
                lv.setAdapter(adapter);
                dialog.setContentView(dialogContentView);

            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.ProdOrderCompSVGetData(ORDER_NO, "10000", "10000");
            }
        }).execute(ORDER_NO);
    }

    private void showTakePhotoDialog(final LengthCutOffDetailElement element) {
        takePhotoDialog = null;
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
        }
        takePhotoDialog = new Dialog(this);
        takePhotoDialog.setTitle("照片");
        takePhotoDialogContentView = LayoutInflater.from(this).inflate(R.layout.take_photo, null);
        NetworkImageView imageView = (NetworkImageView) takePhotoDialogContentView.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.test);
        imageView.setErrorImageResId(R.drawable.test);
        imageView.setDefaultImageResId(R.drawable.test);
        System.out.println("拍照前：" + element.Picture_URL);
        if (element.Picture_URL != null) {
            imageView.setImageURI(Uri.parse(element.Picture_URL));
        }
        cover = imageView;
        takePhotoDialogContentView.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoDialog.dismiss();
            }
        });
        takePhotoDialogContentView.findViewById(R.id.btn_take).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(element);
            }
        });

        takePhotoDialog.show();
        takePhotoDialog.setContentView(takePhotoDialogContentView);
    }

    private void checkAddDialogIsAllChecked(List<LengthCutOffList.Element> list) {
        int selectedCount = 0;
        for (LengthCutOffList.Element element : list) {
            if (element.isSelected) {
                selectedCount += 1;
            }
        }
        if (selectedCount == list.size()) {
            chbDialogContentViewCheck.setChecked(true);
        } else {
            chbDialogContentViewCheck.setChecked(false);
        }
    }

    private void checkAllDetailOrNot() {
        if (detailElementList != null) {
            boolean isChecked = chbDetailCheckAll.isChecked();
            if (isChecked) {
                for (LengthCutOffDetailElement element : detailElementList) {
                    element.isSelected = true;
                }
                chbDetailCheckAll.setChecked(true);
            } else {
                for (LengthCutOffDetailElement element : detailElementList) {
                    element.isSelected = false;
                }
                chbDetailCheckAll.setChecked(false);
            }
            if (rulerAdapter != null) {
                rulerAdapter.notifyDataSetChanged();
            }
        }
    }

    private void makeDetailAllCheckedOrNot() {
        if (detailElementList == null) {
            return;
        }
        int selectedCount = 0;
        for (LengthCutOffDetailElement element : detailElementList) {
            if (element.isSelected) {
                selectedCount += 1;
            }
        }
        if (selectedCount == detailElementList.size()) {
            chbDetailCheckAll.setChecked(true);
        } else {
            chbDetailCheckAll.setChecked(false);
        }
    }

    @SuppressWarnings("all")
    private void submit() {
        if (detailElementList == null || detailElementList.size() == 0) {
            ShowTipMessage("请先选择材料！");
            return;
        }
        String submitStr = null;
        List<SubmitRulerTableElement> list = new ArrayList<SubmitRulerTableElement>();
        for (LengthCutOffDetailElement element : detailElementList) {
            SubmitRulerTableElement submitRulerTableElement = new SubmitRulerTableElement();
            if (!StringUtils.isEmpty(element.Key)) {
                submitRulerTableElement.Key = element.Key;
                submitRulerTableElement.Line_No = element.Line_No;
            } else {
                submitRulerTableElement.Line_No = 0;
            }
            submitRulerTableElement.Document_No = DOCUMENT_NO;
            submitRulerTableElement.Prod_Order_No = ORDER_NO;
            submitRulerTableElement.Production_Date = PROD_DATE;
            submitRulerTableElement.Item_No = null;
            submitRulerTableElement.Item_Description = null;
            submitRulerTableElement.Prod_Line = element.Prod_Line;
            submitRulerTableElement.Shift = SHIFT.equalsIgnoreCase("昼") ? 0 : 1;
            submitRulerTableElement.Worker_Group_ID = groupLeader == null ? "" : groupLeader.get((int) svClassGroup.getSpinner().getSelectedItemId()).Worker_Group_ID;
            submitRulerTableElement.Worker_Group_Name = groupLeader == null ? "" : groupLeader.get((int) svClassGroup.getSpinner().getSelectedItemId()).Worker_Group_Name;
            submitRulerTableElement.RAW_No = element.RAW_No;
            submitRulerTableElement.RAW_Description = element.RAW_Description;
            submitRulerTableElement.Quantity = element.Quantity;
            submitRulerTableElement.Remark = element.Remark;
            //submitRulerTableElement.Lot_No = element.RAW_MATERIAL_ID;
            submitRulerTableElement.Lot_No = element.Lot_No;
            submitRulerTableElement.Picture_URL = element.Picture_URL;
            submitRulerTableElement.Submit_Datetime = DateUtils.getDataString("yyyy-MM-dd HH:mm:ss");
            submitRulerTableElement.Submit_User_ID = LoginUser.getUser().User_ID;
            submitRulerTableElement.Submit_User_Name = LoginUser.getUser().User_Name;
            list.add(submitRulerTableElement);
        }
        Gson objJson = new Gson();
        submitStr = objJson.toJson(list);
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
                    ShowTipMessage("保存成功！");
                    //finish();
                    loadData();
                    return;
                }
                ShowTipMessage("保存成功！", s);
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.ProdOrderCompSVSaveData(params[0]);
            }
        }).execute(submitStr);
    }

    @SuppressWarnings("all")
    private void delete() {
        if (detailElementList == null || detailElementList.size() == 0) {
            ShowTipMessage("请先勾选材料！");
            return;
        }
        int checkedCount = 0;
        StringBuilder submitStr = new StringBuilder();
        for (int i = 0; i < detailElementList.size(); i++) {
            if (detailElementList.get(i).isSelected) {
                checkedCount += 1;
                if (detailElementList.get(i).Key != null) {
                    if (i == detailElementList.size() - 1) {
                        submitStr.append(detailElementList.get(i).Key);
                    } else {
                        submitStr.append(detailElementList.get(i).Key).append(",");
                    }
                }
            }
        }
        if (checkedCount == 0) {
            ShowTipMessage("请先勾选材料！");
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
                    loadData();
                    ShowTipMessage("删除成功！");
                    return;
                }
                ShowTipMessage("删除失败！", s);
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.ProdOrderCompSVDeleteData(params[0]);
            }
        }).execute(submitStr.toString());
    }
    //取得班组信息
    private void loadGroupLeader(String productLine) {
        new UserTask<String, String, String>() {

            @Override
            protected void onErrorHandle(Context context, Exception error) {
            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                if (StringUtils.isNotEmpty(s) && s.indexOf("Error:") != 0) {
                    groupLeader.clear();
                    List<WorkerGroupSV_GetDataByLineResponse> responses = new Gson().fromJson(s, new TypeToken<List<WorkerGroupSV_GetDataByLineResponse>>() {
                    }.getType());
                    if (responses != null) {
                        groupLeader.addAll(responses);
                    }
                    GroupLeaderAdapter adapter = new GroupLeaderAdapter(groupLeader);
                    svClassGroup.getSpinner().setAdapter(adapter);
                    for(int i = 0; i < groupLeader.size(); i++)
                    {
                        if (groupLeader.get(i).Worker_Group_ID.equals(GROUP_ID))
                        {
                            svClassGroup.getSpinner().setSelection(i);
                            break;
                        }
                    }
                }
                else
                {
                    ShowTipMessage("查询失败！", s);
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.WorkerGroupSV_GetDataByLine(params[0]);
            }
        }).execute(productLine);
    }

    //拍照
    private void takePhoto(final LengthCutOffDetailElement element) {
        currentPicElement = element;
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            if (!currentPicElement.equals(null)) {
                currentPicElement.Picture_URL = data.getStringExtra("url");
            }
        }
    }

    public class MyListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.btn_back):
                    RulerTable.this.finish();
                    break;
                default:
                    break;
            }
        }
    }

    private class DialogAdapter extends BaseAdapter {

        private List<LengthCutOffList.Element> list;
        private Context context;

        public DialogAdapter(Context context, List<LengthCutOffList.Element> list) {
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

        @SuppressWarnings("all")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.ruler_dialog_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final LengthCutOffList.Element element = list.get(position);
            holder.tvMaterialID.setText(element.Item_No);
            holder.tvMaterialName.setText(element.Description);
            if (element.isSelected) {
                holder.chbCheck.setChecked(true);
            } else {
                holder.chbCheck.setChecked(false);
            }
            holder.chbCheck.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    element.isSelected = !element.isSelected;
                    notifyDataSetChanged();
                    checkAddDialogIsAllChecked(list);
                }
            });
            return convertView;
        }

        public class ViewHolder {
            TextView tvMaterialID, tvMaterialName;
            CheckBox chbCheck;

            public ViewHolder(View view) {
                tvMaterialID = (TextView) view.findViewById(R.id.tvMaterialID);
                tvMaterialName = (TextView) view.findViewById(R.id.tvMaterialName);
                chbCheck = (CheckBox) view.findViewById(R.id.chb_check);
            }
        }
    }

    @SuppressWarnings("all")
    public class RulerAdapter extends BaseAdapter {

        private List<LengthCutOffDetailElement> list;
        private Context context;

        public RulerAdapter(Context context, List<LengthCutOffDetailElement> list) {
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.ruler_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final LengthCutOffDetailElement element = list.get(position);
            holder.tvMaterialID.setText(element.RAW_No);
            holder.tvMaterialName.setText(element.RAW_Description);
            holder.btnPhoto.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    showTakePhotoDialog(element);
                    currentPicElement = element;
                    Intent intent = new Intent(RulerTable.this, UploadImage.class);
                    intent.putExtra("title", element.RAW_Description);
                    intent.putExtra("diary_id", DOCUMENT_NO);
                    intent.putExtra("url", element.Picture_URL);
                    startActivityForResult(intent, 1000);
                }
            });
            if (element.isSelected) {
                holder.chbSelect.setChecked(true);
            } else {
                holder.chbSelect.setChecked(false);
            }
            final ViewHolder h = holder;
            holder.chbSelect.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = h.chbSelect.isChecked();
                    if (isChecked) {
                        element.isSelected = true;
                    } else {
                        element.isSelected = false;
                    }
                    notifyDataSetChanged();
                    makeDetailAllCheckedOrNot();
                }
            });
            holder.etRawMaterialID.setText(element.Lot_No);
            holder.etQuantity.setText(StringUtils.FormatNumber(element.Quantity));
            holder.etRemark.setText(element.Remark);
            holder.etQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        element.Quantity = StringUtils.isEmpty(h.etQuantity.getText().toString()) ? 0: StringUtils.ToFloat(h.etQuantity.getText().toString());
                    }
                }
            });
            holder.etRemark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        element.Remark = h.etRemark.getText().toString();
                    }
                }
            });
            holder.etRawMaterialID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                         //element.RAW_MATERIAL_ID = h.etRawMaterialID.getText().toString();
                        element.Lot_No = h.etRawMaterialID.getText().toString();
                    }
                }
            });
            return convertView;
        }

        public class ViewHolder {
            TextView tvMaterialID, tvMaterialName, tvPhoto;
            EditText etRawMaterialID, etRemark, etQuantity;
            Button btnPhoto;
            CheckBox chbSelect;

            public ViewHolder(View view) {
                tvMaterialID = (TextView) view.findViewById(R.id.tvMaterialID);
                tvMaterialName = (TextView) view.findViewById(R.id.tvMaterialName);
                etRemark = (EditText) view.findViewById(R.id.etRemark);
                etRawMaterialID = (EditText) view.findViewById(R.id.etRawMaterialID);
                etQuantity = (EditText) view.findViewById(R.id.etQuantity);
                btnPhoto = (Button) view.findViewById(R.id.btnTakePhoto);
                chbSelect = (CheckBox) view.findViewById(R.id.chb_select);
            }
        }
    }

    private class GroupLeaderAdapter extends BaseAdapter {
        private List<WorkerGroupSV_GetDataByLineResponse> data;

        public GroupLeaderAdapter(List<WorkerGroupSV_GetDataByLineResponse> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
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
            holder.textView.setText(data.get(position).Worker_Group_Name);
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
