package com.gki.v107.adapter;

import android.support.annotation.Nullable;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.Polymorph;
import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.ProdConfirmBomItemsInfo;
import com.gki.v107.entity.ProdConfirmDetailsAddon;
import com.gki.v107.entity.ProdConfirmDetailsInfo;
import com.gki.v107.entity.ProdConfirmItemsInfo;
import com.gki.v107.entity.WebPordOrderCompInfo;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.GenericOdataCallback;

import java.util.ArrayList;
import java.util.List;

public class MyInspection2Adapter extends BaseQuickAdapter<Polymorph<ProdConfirmDetailsAddon, ProdConfirmItemsInfo>, BaseViewHolder> {

    public MyInspection2Adapter(@Nullable List<Polymorph<ProdConfirmDetailsAddon, ProdConfirmItemsInfo>> data) {
        super(R.layout.item2_inspection2, data);
    }

    @Override
    protected void convert(BaseViewHolder helper,Polymorph<ProdConfirmDetailsAddon, ProdConfirmItemsInfo> item) {
        helper.setText(R.id.tv2_item_inspection2_no, String.valueOf(helper.getAdapterPosition()));
        helper.setText(R.id.tv2_item_inspection2_project,item.getInfoEntity().getItem_Name());
        helper.setText(R.id.tv2_item_inspection2_material,item.getInfoEntity().getRef_Description());
        helper.setText(R.id.tv2_item_inspection2_method,item.getInfoEntity().getMethod());

        final ProdConfirmDetailsAddon addon = item.getAddonEntity();

        helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection2_confirm,new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText( isChecked? "已确认" : "未确认");
                buttonView.setTextColor(isChecked ? 0xFF00FF00 : 0xFFFF0000);
                addon.setValue(String.valueOf(isChecked));
            }
        });

        helper.setChecked(R.id.checkbox2_item_inspection2_confirm, item.getAddonEntity().getValue().equals(String.valueOf(true)));

        switch (item.getState()) {
            case FAILURE_NEW:
            case FAILURE_EDIT:
                helper.setBackgroundColor(R.id.la2_item_inspection2, 0xFFFFCCCC);
                break;
            case COMMITTED:
                helper.setBackgroundColor(R.id.la2_item_inspection2, 0xFFCCFFCC);
                break;
            //case UNCOMMITTED_EDIT:
                //helper.setBackgroundColor(R.id.la2_item_inspection2, 0xFFCCCCFF);
                //break;
            default:
                helper.setBackgroundColor(R.id.la2_item_inspection2, 0x00FFFFFF);
                break;
        }
    }

    public List<Polymorph<ProdConfirmDetailsAddon, ProdConfirmItemsInfo>> createPolyList(List<ProdConfirmItemsInfo> infoList,
                                                                                          int stepCode,
                                                                                          String orderno,
                                                                                          final TextView tvDate,
                                                                                          final TextView tvstarttime,
                                                                                          final TextView tvendtime) {

        final List<Polymorph<ProdConfirmDetailsAddon, ProdConfirmItemsInfo>> polymorphList = new ArrayList<>();
        for (int index = 0; index<infoList.size() ; index++) {

            ProdConfirmItemsInfo info = infoList.get(index);
            ProdConfirmDetailsAddon addon = new ProdConfirmDetailsAddon();

            addon.setLine_No(info.getLine_No());
            addon.setValue(String.valueOf(false));
            addon.setMethod(info.getMethod());
            addon.setProd_Order_No(orderno);
            addon.setStep(stepCode);
            addon.setItem_Name(info.getItem_Name());

            String filter2 = "Prod_Order_No eq '" + orderno + "' and Step eq " + stepCode + "and Line_No eq " + addon.getLine_No();

            Polymorph<ProdConfirmDetailsAddon, ProdConfirmItemsInfo> poly = new Polymorph<>(addon, info, Polymorph.State.UNCOMMITTED_NEW);
            polymorphList.add(poly);

            final int specIndex = index;

            ApiTool.callProdConfirmDetailsList(filter2, new GenericOdataCallback<ProdConfirmDetailsInfo>() {
                @Override
                public void onDataAvailable(List<ProdConfirmDetailsInfo> datas) {
                    if (!datas.isEmpty()) {
                        ProdConfirmDetailsInfo info2 = datas.get(0);

                        polymorphList.get(specIndex).getAddonEntity().setValue(info2.getValue());
                        polymorphList.get(specIndex).setState(Polymorph.State.UNCOMMITTED_EDIT);

                        notifyDataSetChanged();

                        String startdatetime = info2.getStrat_Time().replace(":00Z", "");
                        String enddatetime = info2.getEnd_Time().replace(":00Z", "");
                        if (startdatetime.contains("T") && enddatetime.contains("T")) {
                            String[] datetime = startdatetime.split("T");
                            tvDate.setText(datetime[0]);
                            tvstarttime.setText(datetime[1]);
                            tvendtime.setText(enddatetime.split("T")[1]);
                        }
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