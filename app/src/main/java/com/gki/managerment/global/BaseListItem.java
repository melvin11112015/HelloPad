package com.gki.managerment.global;

import java.io.Serializable;

/**
 * Created by lee on 2016/2/25.
 * 下拉列表专用类
 */
public class BaseListItem {
    public String ItemId;
    public String ItemName;

    public BaseListItem(){
        this.ItemId = "";
        this.ItemName = "";
    }

    public BaseListItem(String strItemId, String strItemName){
        this.ItemId = strItemId;
        this.ItemName = strItemName;
    }

    public void setItemId(String strItemId) {
        this.ItemId = strItemId;
    }
    public String getItemId() { return this.ItemId; }

    public void setItemName(String strItemName) {
        this.ItemName = strItemName;
    }
    public String getItemName() { return this.ItemName; }
}