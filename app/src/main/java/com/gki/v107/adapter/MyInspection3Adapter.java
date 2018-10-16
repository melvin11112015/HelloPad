package com.gki.v107.adapter;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

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


        if(isBoolType) {
            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection3_confirm1, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setText(isChecked ? "OK" : "NotOK");
                    buttonView.setTextColor(isChecked ? 0xFF00FF00 : 0xFFFF0000);
                    addon.setValue1(String.valueOf(isChecked));
                }
            });

            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection3_confirm2, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setText(isChecked ? "OK" : "NotOK");
                    buttonView.setTextColor(isChecked ? 0xFF00FF00 : 0xFFFF0000);
                    addon.setValue2(String.valueOf(isChecked));
                }
            });
            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection3_confirm3, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setText(isChecked ? "OK" : "NotOK");
                    buttonView.setTextColor(isChecked ? 0xFF00FF00 : 0xFFFF0000);
                    addon.setValue3(String.valueOf(isChecked));
                }
            });


            helper.setChecked(R.id.checkbox2_item_inspection3_confirm1, addon.getValue1().equals(String.valueOf(true)));
            helper.setChecked(R.id.checkbox2_item_inspection3_confirm2, addon.getValue2().equals(String.valueOf(true)));
            helper.setChecked(R.id.checkbox2_item_inspection3_confirm3, addon.getValue3().equals(String.valueOf(true)));


        }else {

            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection3_confirm1, null);
            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection3_confirm2, null);
            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection3_confirm3, null);

            helper.setChecked(R.id.checkbox2_item_inspection3_confirm1, false);
            helper.setChecked(R.id.checkbox2_item_inspection3_confirm2, false);
            helper.setChecked(R.id.checkbox2_item_inspection3_confirm3, false);


        }

        helper.setGone(R.id.checkbox2_item_inspection3_confirm1, isBoolType);
        helper.setGone(R.id.checkbox2_item_inspection3_confirm2, isBoolType);
        helper.setGone(R.id.checkbox2_item_inspection3_confirm3, isBoolType);

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