package com.gki.v107.entity;

public class WebPordOrderCompInfo {
    private String Status,Prod_Order_No,Item_No,Description,Unit_of_Measure_Code,Quantity,Quantity_Base,ETag;
    private int Prod_Order_Line_No,Line_No;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getProd_Order_No() {
        return Prod_Order_No;
    }

    public void setProd_Order_No(String prod_Order_No) {
        Prod_Order_No = prod_Order_No;
    }

    public String getItem_No() {
        return Item_No;
    }

    public void setItem_No(String item_No) {
        Item_No = item_No;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUnit_of_Measure_Code() {
        return Unit_of_Measure_Code;
    }

    public void setUnit_of_Measure_Code(String unit_of_Measure_Code) {
        Unit_of_Measure_Code = unit_of_Measure_Code;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getQuantity_Base() {
        return Quantity_Base;
    }

    public void setQuantity_Base(String quantity_Base) {
        Quantity_Base = quantity_Base;
    }

    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }

    public int getProd_Order_Line_No() {
        return Prod_Order_Line_No;
    }

    public void setProd_Order_Line_No(int prod_Order_Line_No) {
        Prod_Order_Line_No = prod_Order_Line_No;
    }

    public int getLine_No() {
        return Line_No;
    }

    public void setLine_No(int line_No) {
        Line_No = line_No;
    }
}
