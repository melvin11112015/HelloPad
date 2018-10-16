package com.gki.v107.adapter;

import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.Polymorph;
import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.ProdConfirmBomItemsInfo;
import com.gki.v107.entity.WebPordOrderCompInfo;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.GenericOdataCallback;

import java.util.ArrayList;
import java.util.List;

public class MyInspection1aAdapter extends BaseQuickAdapter<Polymorph<ProdConfirmBomItemsAddon, WebPordOrderCompInfo>, BaseViewHolder> {

    public MyInspection1aAdapter(@Nullable List<Polymorph<ProdConfirmBomItemsAddon, WebPordOrderCompInfo>> data) {
        super(R.layout.item2_inspection1, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, Polymorph<ProdConfirmBomItemsAddon, WebPordOrderCompInfo> item) {
        helper.setText(R.id.tv2_item_inspection1_no, String.valueOf(helper.getAdapterPosition()));
        helper.setText(R.id.tv2_item_inspection1_code, item.getAddonEntity().getItem_No());
        helper.setText(R.id.tv2_item_inspection1_name, item.getInfoEntity().getDescription());
        helper.setText(R.id.tv2_item_inspection1_count, item.getInfoEntity().getQuantity_Base());

        final ProdConfirmBomItemsAddon addon = item.getAddonEntity();

        helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection1_confirm,new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText( isChecked? "已确认" : "未确认");
                buttonView.setTextColor(isChecked ? 0xFF00FF00 : 0xFFFF0000);
                addon.setConfirmed(isChecked);
            }
        });

        helper.setChecked(R.id.checkbox2_item_inspection1_confirm, item.getAddonEntity().isConfirmed());

        switch (item.getState()) {
            case FAILURE_NEW:
            case FAILURE_EDIT:
                helper.setBackgroundColor(R.id.la2_item_inspection1, 0xFFFFCCCC);
                break;
            case COMMITTED:
                helper.setBackgroundColor(R.id.la2_item_inspection1, 0xFFCCFFCC);
                break;
            //case UNCOMMITTED_EDIT:
                //helper.setBackgroundColor(R.id.la2_item_inspection1, 0xFFCCCCFF);
                //break;
            default:
                helper.setBackgroundColor(R.id.la2_item_inspection1, 0x00FFFFFF);
                break;
        }
    }

    public List<Polymorph<ProdConfirmBomItemsAddon, WebPordOrderCompInfo>> createPolyList(List<WebPordOrderCompInfo> infoList,
                                                                                          int stepCode,
                                                                                          String orderno,
                                                                                          final TextView tvDate,
                                                                                          final TextView tvstarttime,
                                                                                          final TextView tvendtime) {

        final List<Polymorph<ProdConfirmBomItemsAddon, WebPordOrderCompInfo>> polymorphList = new ArrayList<>();
        for (int index = 0; index<infoList.size() ; index++) {

            WebPordOrderCompInfo info = infoList.get(index);
            ProdConfirmBomItemsAddon addon = new ProdConfirmBomItemsAddon();

            addon.setLine_No(info.getLine_No());
            addon.setConfirmed(false);
            addon.setItem_No(info.getItem_No());
            addon.setProd_Order_No(info.getProd_Order_No());
            addon.setStep(stepCode);


            String filter2 = "Prod_Order_No eq '" + orderno + "' and Step eq " + stepCode + "and Item_No eq '" + info.getItem_No() + "'";

            Polymorph<ProdConfirmBomItemsAddon, WebPordOrderCompInfo> poly = new Polymorph<>(addon, info, Polymorph.State.UNCOMMITTED_NEW);
            polymorphList.add(poly);

            final int specIndex = index;

            ApiTool.callProdConfirmBomItemsList(filter2, new GenericOdataCallback<ProdConfirmBomItemsInfo>() {
                @Override
                public void onDataAvailable(List<ProdConfirmBomItemsInfo> datas) {
                    if (!datas.isEmpty()) {
                        ProdConfirmBomItemsInfo info2 = datas.get(0);

                        polymorphList.get(specIndex).getAddonEntity().setConfirmed(info2.getConfirmed());
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