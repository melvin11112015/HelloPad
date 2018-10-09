package com.gki.managerment.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BlanksParser {
    public List<Blankentity> getJsonParser(String content) {
        Blankentity blankentity;
        List<Blankentity> list = new ArrayList<Blankentity>();
        try {
            JSONArray jsonArray = new JSONArray(content);
            //String total = jsonArray.getString("Key");
            //JSONArray jsonArray = jsonObject.getJSONArray("rows");
            for (int i = 0; i < jsonArray.length(); i++) {
                blankentity = new Blankentity();
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                String strKey = jsonObject2.getString("Key");
                blankentity.setKey(strKey);

                String Type = jsonObject2.getString("Type");
                blankentity.setType(Type);

                String strDocument_No = jsonObject2.getString("Document_No");
                blankentity.setDocument_no(strDocument_No);

                String strQuantity_Time = jsonObject2.getString("Quantity_Time");
                blankentity.setQuantity_Time(strQuantity_Time);

                String TypeSpecified = jsonObject2.getString("TypeSpecified");
                blankentity.setTypeSpecified(TypeSpecified);

                String Code = jsonObject2.getString("Code");
                blankentity.setCode(Code);

                String Description = jsonObject2.getString("Description");
                blankentity.setDescription(Description);

                String Sorting_Key = jsonObject2.getString("Sorting_Key");
                blankentity.setSorting_Key(Sorting_Key);

                String Sorting_KeySpecified = jsonObject2.getString("Sorting_KeySpecified");
                blankentity.setSorting_KeySpecified(Sorting_KeySpecified);

                list.add(blankentity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
