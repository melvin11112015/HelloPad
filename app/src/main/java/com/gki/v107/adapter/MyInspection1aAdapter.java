package com.gki.v107.adapter;

import android.support.annotation.Nullable;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gki.managerment.LoginUser;
import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.ItemVsBomInfo;
import com.gki.v107.entity.Polymorph;
import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.ProdConfirmBomItemsInfo;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.GenericOdataCallback;
import com.gki.v107.tool.DatetimeTool;

import java.util.ArrayList;
import java.util.List;

public class MyInspection1aAdapter extends BaseQuickAdapter<Polymorph<ProdConfirmBomItemsAddon, ItemVsBomInfo>, BaseViewHolder> {

    public MyInspection1aAdapter(@Nullable List<Polymorph<ProdConfirmBomItemsAddon, ItemVsBomInfo>> data) {
        super(R.layout.item2_inspection1, data);

    }



    @Override
    protected void convert(BaseViewHolder helper, Polymorph<ProdConfirmBomItemsAddon, ItemVsBomInfo> item) {
        // remove OnCheckedChangeListener firstly! viewholder可能因为未移除监听器出现数据错乱！
        helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection1_confirm, null);

        helper.setText(R.id.tv2_item_inspection1_no,item.getAddonEntity().getIndex_No());
        helper.setText(R.id.tv2_item_inspection1_code, item.getAddonEntity().getCheck_Item_No());
        helper.setText(R.id.tv2_item_inspection1_name, item.getAddonEntity().getCheck_Item_Name());
        helper.setText(R.id.tv2_item_inspection1_count, item.getAddonEntity().getQuantity());
        helper.setText(R.id.tv2_item_inspection1_remark, item.getAddonEntity().getRemark());

        final ProdConfirmBomItemsAddon addon = item.getAddonEntity();

        // remove OnCheckedChangeListener firstly! viewholder可能因为未移除监听器出现数据错乱！

        helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection1_confirm,new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText( isChecked? "已确认" : "未确认");
                buttonView.setTextColor(isChecked ? 0xFF00FF00 : 0xFFFF0000);
                addon.setConfirmed(isChecked);
            }
        });

        helper.setChecked(R.id.checkbox2_item_inspection1_confirm, item.getAddonEntity().getConfirmed());

        switch (item.getState()) {
            case FAILURE_NEW:
            case FAILURE_EDIT:
                helper.setBackgroundColor(R.id.la2_item_inspection1, 0xFFFFCCCC);
                helper.getView(R.id.checkbox2_item_inspection1_confirm).setEnabled(true);
                break;
            case COMMITTED:
                helper.setBackgroundColor(R.id.la2_item_inspection1, 0xFFCCFFCC);
                helper.getView(R.id.checkbox2_item_inspection1_confirm).setEnabled(false);
                break;
            //case UNCOMMITTED_EDIT:
                //helper.setBackgroundColor(R.id.la2_item_inspection1, 0xFFCCCCFF);
                //break;
            default:
                helper.setBackgroundColor(R.id.la2_item_inspection1, 0x00FFFFFF);
                helper.getView(R.id.checkbox2_item_inspection1_confirm).setEnabled(true);
                break;
        }
    }

    public List<Polymorph<ProdConfirmBomItemsAddon, ItemVsBomInfo>> createPolyList(List<ItemVsBomInfo> infoList,
                                                                                   int stepCode,
                                                                                   String orderno,
                                                                                   final TextView tvstarttime,
                                                                                   final TextView tvendtime
                                                                                         ) {

        final List<Polymorph<ProdConfirmBomItemsAddon, ItemVsBomInfo>> polymorphList = new ArrayList<>();
        for (int index = 0; index<infoList.size() ; index++) {

            ItemVsBomInfo info = infoList.get(index);
            ProdConfirmBomItemsAddon addon = new ProdConfirmBomItemsAddon();

            addon.setLine_No(info.getLine_No());
            addon.setConfirmed(false);
            addon.setCheck_Item_No(info.getCheck_Item_No());
            addon.setCheck_Item_Name(info.getCheck_Item_Name());
            addon.setProd_Order_No(orderno);
            addon.setStep(stepCode);
            addon.setRemark(info.getRemark());
            addon.setIndex_No(info.getIndex_No());
            addon.setQuantity(info.getQuantity());
            addon.setCreate_User(LoginUser.getUser().getUserId());


            String filter2 = "Prod_Order_No eq '" +
                    orderno +
                    "' and Line_No eq " +
                    info.getLine_No();

            Polymorph<ProdConfirmBomItemsAddon, ItemVsBomInfo> poly = new Polymorph<>(addon, info, Polymorph.State.UNCOMMITTED_NEW);
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


                        //tvDate.setText(DatetimeTool.convertOdataTimezone(info2.getStrat_Time(),DatetimeTool.TYPE_DATE,DatetimeTool.DEFAULT_ADJUST_TIMEZONE));
                        tvstarttime.setText(DatetimeTool.convertOdataTimezone(info2.getStrat_Time(), DatetimeTool.TYPE_TIME, info2.getStrat_Time().contains("Z")));
                        tvendtime.setText(DatetimeTool.convertOdataTimezone(info2.getEnd_Time(), DatetimeTool.TYPE_TIME, info2.getStrat_Time().contains("Z")));

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