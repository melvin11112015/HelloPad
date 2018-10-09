package com.gki.managerment.bean;

import java.util.List;

/**
 * 定尺裁剪返回列表
 * Created by summit on 2/16/16.
 */
public class LengthCutOffList {

    public List<Element> elements;


    public static class Element {

        public String Key;
        public int Status;
        public boolean StatusSpecified;
        public String Prod_Order_No;
        public long Prod_Order_Line_No;
        public boolean Prod_Order_Line_NoSpecified;
        public long Line_No;
        public boolean Line_NoSpecified;
        public String Item_No;
        public String Description;
        public String Unit_of_Measure_Code;
        public float Quantity;
        public boolean QuantitySpecified;
        public float Quantity_Base;
        public boolean Quantity_BaseSpecified;
        public boolean isSelected = false; // 默认不选中


    }
}
