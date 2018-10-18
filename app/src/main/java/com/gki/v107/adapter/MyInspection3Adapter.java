package com.gki.v107.adapter;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.ItemVsSpecItemInfo;
import com.gki.v107.entity.Polymorph;
import com.gki.v107.entity.ProdSpecDetailsAddon;
import com.gki.v107.entity.ProdSpecDetailsInfo;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.GenericOdataCallback;

import java.util.ArrayList;
import java.util.List;

public class MyInspection3Adapter extends BaseQuickAdapter<Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo>, BaseViewHolder> {

    public MyInspection3Adapter(@Nullable List<Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo>> data) {
        super(R.layout.item2_inspection3, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo> item) {
        helper.setText(R.id.tv2_item_inspection3_no, String.valueOf(helper.getAdapterPosition()));
        helper.setText(R.id.tv2_item_inspection3_property, item.getInfoEntity().getSpec_Name());
        helper.setText(R.id.tv2_item_inspection3_specification, item.getInfoEntity().getSpec_Description());

        boolean isBoolType = item.getInfoEntity().getValue_Type().equals("Boolean");
        boolean isDecimalType = item.getInfoEntity().getValue_Type().equals("Decimal");

        final ProdSpecDetailsAddon addon = item.getAddonEntity();

        EditText editText1 = ((EditText) helper.getView(R.id.et2_item_inspection3_value1));
        EditText editText2 = ((EditText) helper.getView(R.id.et2_item_inspection3_value2));
        EditText editText3 = ((EditText) helper.getView(R.id.et2_item_inspection3_value3));

        helper.setText(R.id.et2_item_inspection3_value1, addon.getValue1());
        helper.setText(R.id.et2_item_inspection3_value2, addon.getValue2());
        helper.setText(R.id.et2_item_inspection3_value3, addon.getValue3());

        editText1.setInputType(isDecimalType?8194:InputType.TYPE_CLASS_TEXT);
        editText2.setInputType(isDecimalType?8194:InputType.TYPE_CLASS_TEXT);
        editText3.setInputType(isDecimalType?8194:InputType.TYPE_CLASS_TEXT);

        editText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)addon.setValue1(((EditText)v).getText().toString());
            }
        });

        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)addon.setValue2(((EditText)v).getText().toString());
            }
        });

        editText3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)addon.setValue3(((EditText)v).getText().toString());
            }
        });


        Spinner s1 = (Spinner)helper.getView(R.id.spinner2_item_inspection3_confirm1);
        switch (addon.getValue1()){
            default:
                s1.setSelection(0);
                break;
            case "true":
                s1.setSelection(1);
                break;
            case "false":
                s1.setSelection(2);
                break;
        }
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    default:
                    case 0:
                        addon.setValue1("NA");
                        break;
                    case 1:
                        addon.setValue1("true");
                        break;
                    case 2:
                        addon.setValue1("false");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner s2 = (Spinner)helper.getView(R.id.spinner2_item_inspection3_confirm2);
        switch (addon.getValue2()){
            default:
                s2.setSelection(0);
                break;
            case "true":
                s2.setSelection(1);
                break;
            case "false":
                s2.setSelection(2);
                break;
        }
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    default:
                    case 0:
                        addon.setValue2("NA");
                        break;
                    case 1:
                        addon.setValue2("true");
                        break;
                    case 2:
                        addon.setValue2("false");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner s3 = (Spinner)helper.getView(R.id.spinner2_item_inspection3_confirm3);
        switch (addon.getValue3()){
            default:
                s3.setSelection(0);
                break;
            case "true":
                s3.setSelection(1);
                break;
            case "false":
                s3.setSelection(2);
                break;
        }
        s3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    default:
                    case 0:
                        addon.setValue3("NA");
                        break;
                    case 1:
                        addon.setValue3("true");
                        break;
                    case 2:
                        addon.setValue3("false");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        helper.setGone(R.id.spinner2_item_inspection3_confirm1, isBoolType);
        helper.setGone(R.id.spinner2_item_inspection3_confirm2, isBoolType);
        helper.setGone(R.id.spinner2_item_inspection3_confirm3, isBoolType);

        helper.setGone(R.id.et2_item_inspection3_value1, !isBoolType);
        helper.setGone(R.id.et2_item_inspection3_value2, !isBoolType);
        helper.setGone(R.id.et2_item_inspection3_value3, !isBoolType);


        switch (item.getState()) {
            case FAILURE_NEW:
            case FAILURE_EDIT:
                helper.setBackgroundColor(R.id.la2_item_inspection3, 0xFFFFCCCC);
                break;
            case COMMITTED:
                helper.setBackgroundColor(R.id.la2_item_inspection3, 0xFFCCFFCC);
                break;
            //case UNCOMMITTED_EDIT:
            //helper.setBackgroundColor(R.id.la2_item_inspection3, 0xFFCCCCFF);
            //break;
            default:
                helper.setBackgroundColor(R.id.la2_item_inspection3, 0x00FFFFFF);
                break;
        }
    }

    public List<Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo>> createPolyList(List<ItemVsSpecItemInfo> infoList,
                                                                                    String orderno) {

        final List<Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo>> polymorphList = new ArrayList<>();
        for (int index = 0; index < infoList.size(); index++) {

            ItemVsSpecItemInfo info = infoList.get(index);
            ProdSpecDetailsAddon addon = new ProdSpecDetailsAddon();

            addon.setLine_No(info.getLine_No());
            addon.setValue1("");
            addon.setValue2("");
            addon.setValue3("");
            addon.setSpec_Name(info.getSpec_Name());
            addon.setProd_Order_No(orderno);
            addon.setSpec_Description(info.getSpec_Description());

            String filter2 = "Prod_Order_No eq '" + orderno + "' and Line_No eq " + addon.getLine_No();

            Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo> poly = new Polymorph<>(addon, info, Polymorph.State.UNCOMMITTED_NEW);
            polymorphList.add(poly);

            final int specIndex = index;

            ApiTool.callProdSpecDetailsList(filter2, new GenericOdataCallback<ProdSpecDetailsInfo>() {
                @Override
                public void onDataAvailable(List<ProdSpecDetailsInfo> datas) {
                    if (!datas.isEmpty()) {
                        ProdSpecDetailsInfo info2 = datas.get(0);

                        polymorphList.get(specIndex).getAddonEntity().setValue1(info2.getValue1());
                        polymorphList.get(specIndex).getAddonEntity().setValue2(info2.getValue2());
                        polymorphList.get(specIndex).getAddonEntity().setValue3(info2.getValue3());
                        polymorphList.get(specIndex).setState(Polymorph.State.UNCOMMITTED_EDIT);

                        notifyDataSetChanged();

                    }
                }

                @Override
                public void onDataUnAvailable(String msg, int errorCode) {
                    ToastUtil.show(mContext, msg);
                }
            });
        }
        return polymorphList;
    }

}