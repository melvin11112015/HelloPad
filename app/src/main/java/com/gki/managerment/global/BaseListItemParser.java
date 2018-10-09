package com.gki.managerment.global;

import com.gki.managerment.global.BaseListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BaseListItemParser {
    public List<BaseListItem> getListFromJson(String strListContent, String strIdField, String strNameField, boolean addEmpty) {
        BaseListItem aItem;
        List<BaseListItem> list = new ArrayList<BaseListItem>();
        try {
            JSONArray jsonArray = new JSONArray(strListContent);
            if (addEmpty)
            {
                BaseListItem aEmptyItem = new BaseListItem();
                aEmptyItem.setItemId("");
                aEmptyItem.setItemName("");
                list.add(aEmptyItem);
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                aItem = new BaseListItem();
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                String strKey = jsonObject2.getString(strIdField);
                aItem.setItemId(strKey);
                String Type = jsonObject2.getString(strNameField);
                aItem.setItemName(Type);
                list.add(aItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}