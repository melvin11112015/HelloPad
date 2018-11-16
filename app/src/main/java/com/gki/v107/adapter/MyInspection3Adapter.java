package com.gki.v107.adapter;

import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gki.managerment.LoginUser;
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

    public static final String TRUE_STR = "true";
    public static final String FALSE_STR = "false";
    public static final String FALSE_STR2 = "false";

    public MyInspection3Adapter(@Nullable List<Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo>> data) {
        super(R.layout.item2_inspection3, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo> item) {
        helper.setText(R.id.tv2_item_inspection3_no, item.getInfoEntity().getNo());
        helper.setText(R.id.tv2_item_inspection3_property, item.getInfoEntity().getSpec_Name());
        helper.getView(R.id.tv2_item_inspection3_property).setSelected(true);
        helper.setText(R.id.tv2_item_inspection3_specification, item.getInfoEntity().getSpec_Description());
        helper.getView(R.id.tv2_item_inspection3_specification).setSelected(true);
        helper.setText(R.id.tv2_item_inspection3_remark, item.getInfoEntity().getRemark());

        boolean isBoolType = item.getInfoEntity().getValue_Type().equals("Boolean");
        boolean isDecimalType = item.getInfoEntity().getValue_Type().equals("Decimal");

        final ProdSpecDetailsAddon addon = item.getAddonEntity();

        EditText editText1 = helper.getView(R.id.et2_item_inspection3_value1);
        EditText editText2 = helper.getView(R.id.et2_item_inspection3_value2);
        EditText editText3 = helper.getView(R.id.et2_item_inspection3_value3);
        EditText editText4 = helper.getView(R.id.et2_item_inspection3_value4);
        //EditText editText5 = helper.getView(R.id.et2_item_inspection3_value5);

        Spinner s1 = helper.getView(R.id.spinner2_item_inspection3_confirm1);
        Spinner s2 = helper.getView(R.id.spinner2_item_inspection3_confirm2);
        Spinner s3 = helper.getView(R.id.spinner2_item_inspection3_confirm3);
        Spinner s4 = helper.getView(R.id.spinner2_item_inspection3_confirm4);
        //Spinner s5 = helper.getView(R.id.spinner2_item_inspection3_confirm5);

        helper.setText(R.id.et2_item_inspection3_value1, addon.getValue1());
        helper.setText(R.id.et2_item_inspection3_value2, addon.getValue2());
        helper.setText(R.id.et2_item_inspection3_value3, addon.getValue3());
        helper.setText(R.id.et2_item_inspection3_value4, addon.getValue4());
        helper.setText(R.id.et2_item_inspection3_value5, addon.getValue5());

        s1.setSelection(addon.getValue1().equals(TRUE_STR) ? 1 : 0, true);
        s2.setSelection(addon.getValue2().equals(TRUE_STR) ? 1 : 0, true);
        s3.setSelection(addon.getValue3().equals(TRUE_STR) ? 1 : 0, true);
        s4.setSelection(addon.getValue4().equals(TRUE_STR) ? 1 : 0, true);
        //s5.setSelection(addon.getValue5().equals(TRUE_STR) ? 1 : 0, true);

        editText1.setInputType(isDecimalType?
                InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_NUMBER_FLAG_DECIMAL:
                InputType.TYPE_CLASS_TEXT);
        editText2.setInputType(isDecimalType?
                InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_NUMBER_FLAG_DECIMAL:
                InputType.TYPE_CLASS_TEXT);
        editText3.setInputType(isDecimalType?
                InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_NUMBER_FLAG_DECIMAL:
                InputType.TYPE_CLASS_TEXT);
        editText4.setInputType(isDecimalType?
                InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_NUMBER_FLAG_DECIMAL:
                InputType.TYPE_CLASS_TEXT);
        /*
        editText5.setInputType(isDecimalType?
                InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_NUMBER_FLAG_DECIMAL:
                InputType.TYPE_CLASS_TEXT);
                */

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

        editText4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)addon.setValue4(((EditText)v).getText().toString());
            }
        });
/*
        editText5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)addon.setValue5(((EditText)v).getText().toString());
            }
        });
*/
s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            default:
            case 0:
                addon.setValue1(FALSE_STR2);
                break;
            case 1:
                addon.setValue1(TRUE_STR);
                break;
            case 2:
                addon.setValue1(FALSE_STR);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
});


        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    default:
                    case 0:
                        addon.setValue2(FALSE_STR2);
                        break;
                    case 1:
                        addon.setValue2(TRUE_STR);
                        break;
                    case 2:
                        addon.setValue2(FALSE_STR);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        s3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    default:
                    case 0:
                        addon.setValue3(FALSE_STR2);
                        break;
                    case 1:
                        addon.setValue3(TRUE_STR);
                        break;
                    case 2:
                        addon.setValue3(FALSE_STR);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        s4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    default:
                    case 0:
                        addon.setValue4(FALSE_STR2);
                        break;
                    case 1:
                        addon.setValue4(TRUE_STR);
                        break;
                    case 2:
                        addon.setValue4(FALSE_STR);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*
        s5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    default:
                    case 0:
                        addon.setValue5(FALSE_STR2);
                        break;
                    case 1:
                        addon.setValue5(TRUE_STR);
                        break;
                    case 2:
                        addon.setValue5(FALSE_STR);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */

        helper.setGone(R.id.spinner2_item_inspection3_confirm1, isBoolType);
        helper.setGone(R.id.spinner2_item_inspection3_confirm2, isBoolType);
        helper.setGone(R.id.spinner2_item_inspection3_confirm3, isBoolType);
        helper.setGone(R.id.spinner2_item_inspection3_confirm4, isBoolType);
        //helper.setGone(R.id.spinner2_item_inspection3_confirm5, isBoolType);
        helper.setGone(R.id.spinner2_item_inspection3_confirm5, false);

        helper.setGone(R.id.et2_item_inspection3_value1, !isBoolType);
        helper.setGone(R.id.et2_item_inspection3_value2, !isBoolType);
        helper.setGone(R.id.et2_item_inspection3_value3, !isBoolType);
        helper.setGone(R.id.et2_item_inspection3_value4, !isBoolType);
        //helper.setGone(R.id.et2_item_inspection3_value5, !isBoolType);
        helper.setGone(R.id.et2_item_inspection3_value5, false);


        switch (item.getState()) {
            case FAILURE_NEW:
            case FAILURE_EDIT:
                helper.setBackgroundColor(R.id.la2_item_inspection3, 0xFFFFCCCC);

                helper.getView(R.id.spinner2_item_inspection3_confirm1).setEnabled(true);
                helper.getView(R.id.spinner2_item_inspection3_confirm2).setEnabled(true);
                helper.getView(R.id.spinner2_item_inspection3_confirm3).setEnabled(true);
                helper.getView(R.id.spinner2_item_inspection3_confirm4).setEnabled(true);
                helper.getView(R.id.spinner2_item_inspection3_confirm5).setEnabled(true);

                helper.getView(R.id.et2_item_inspection3_value1).setEnabled(true);
                helper.getView(R.id.et2_item_inspection3_value2).setEnabled(true);
                helper.getView(R.id.et2_item_inspection3_value3).setEnabled(true);
                helper.getView(R.id.et2_item_inspection3_value4).setEnabled(true);
                helper.getView(R.id.et2_item_inspection3_value5).setEnabled(true);

                break;
            case COMMITTED:
                helper.setBackgroundColor(R.id.la2_item_inspection3, 0xFFCCFFCC);

                helper.getView(R.id.spinner2_item_inspection3_confirm1).setEnabled(false);
                helper.getView(R.id.spinner2_item_inspection3_confirm2).setEnabled(false);
                helper.getView(R.id.spinner2_item_inspection3_confirm3).setEnabled(false);
                helper.getView(R.id.spinner2_item_inspection3_confirm4).setEnabled(false);
                helper.getView(R.id.spinner2_item_inspection3_confirm5).setEnabled(false);

                helper.getView(R.id.et2_item_inspection3_value1).setEnabled(false);
                helper.getView(R.id.et2_item_inspection3_value2).setEnabled(false);
                helper.getView(R.id.et2_item_inspection3_value3).setEnabled(false);
                helper.getView(R.id.et2_item_inspection3_value4).setEnabled(false);
                helper.getView(R.id.et2_item_inspection3_value5).setEnabled(false);
                break;
            //case UNCOMMITTED_EDIT:
            //helper.setBackgroundColor(R.id.la2_item_inspection3, 0xFFCCCCFF);
            //break;
            default:
                helper.setBackgroundColor(R.id.la2_item_inspection3, 0x00FFFFFF);

                helper.getView(R.id.spinner2_item_inspection3_confirm1).setEnabled(true);
                helper.getView(R.id.spinner2_item_inspection3_confirm2).setEnabled(true);
                helper.getView(R.id.spinner2_item_inspection3_confirm3).setEnabled(true);
                helper.getView(R.id.spinner2_item_inspection3_confirm4).setEnabled(true);
                helper.getView(R.id.spinner2_item_inspection3_confirm5).setEnabled(true);

                helper.getView(R.id.et2_item_inspection3_value1).setEnabled(true);
                helper.getView(R.id.et2_item_inspection3_value2).setEnabled(true);
                helper.getView(R.id.et2_item_inspection3_value3).setEnabled(true);
                helper.getView(R.id.et2_item_inspection3_value4).setEnabled(true);
                helper.getView(R.id.et2_item_inspection3_value5).setEnabled(true);
                break;
        }
    }

    public List<Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo>> createPolyList(List<ItemVsSpecItemInfo> infoList,
                                                                                    String orderno, final EditText etNo1, final EditText etNo2, final EditText etNo3,final EditText etNo4,final EditText etNo5) {

        final List<Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo>> polymorphList = new ArrayList<>();
        for (int index = 0; index < infoList.size(); index++) {

            ItemVsSpecItemInfo info = infoList.get(index);
            ProdSpecDetailsAddon addon = new ProdSpecDetailsAddon();

            addon.setNo(info.getNo());
            addon.setLine_No(info.getLine_No());
            addon.setValue1(info.getValue_Type().equals("Boolean") ? "true" : "");
            addon.setValue2(info.getValue_Type().equals("Boolean") ? "true" : "");
            addon.setValue3(info.getValue_Type().equals("Boolean") ? "true" : "");
            addon.setValue4(info.getValue_Type().equals("Boolean") ? "true" : "");
            addon.setValue5(info.getValue_Type().equals("Boolean") ? "true" : "");
            addon.setSpec_Name(info.getSpec_Name());
            addon.setProd_Order_No(orderno);
            addon.setSpec_Description(info.getSpec_Description());
            addon.setRemark(info.getRemark());
            addon.setValue_Type(info.getValue_Type());
            addon.setDiff_Value(info.getDiff_Value());
            addon.setQty1("0");
            addon.setQty2("0");
            addon.setQty3("0");
            addon.setQty4("0");
            addon.setQty5("0");
            addon.setCreate_User(LoginUser.getUser().getUserId());

            String filter2 = "Prod_Order_No eq '" + orderno + "' and Line_No eq " + addon.getLine_No() + " and No eq '" + addon.getNo() + "'";

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
                        polymorphList.get(specIndex).getAddonEntity().setValue4(info2.getValue4());
                        polymorphList.get(specIndex).getAddonEntity().setValue5(info2.getValue5());
                        polymorphList.get(specIndex).getAddonEntity().setQty1(info2.getQty1());
                        polymorphList.get(specIndex).getAddonEntity().setQty2(info2.getQty2());
                        polymorphList.get(specIndex).getAddonEntity().setQty3(info2.getQty3());
                        polymorphList.get(specIndex).getAddonEntity().setQty4(info2.getQty4());
                        polymorphList.get(specIndex).getAddonEntity().setQty5(info2.getQty5());
                        etNo1.setText(info2.getQty1());
                        etNo2.setText(info2.getQty2());
                        etNo3.setText(info2.getQty3());
                        etNo4.setText(info2.getQty4());
                        etNo5.setText(info2.getQty5());
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