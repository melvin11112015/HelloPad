package com.gki.v107.entity;

public class ProdConfirmBomItemsAddon {
    private String Prod_Order_No,Item_No, Strat_Time,End_Time/*,Item_Name*/;
    private boolean Confirmed;
    private int Step,Line_No;

    public ProdConfirmBomItemsAddon(String prod_Order_No, String item_No, String strat_Time, String end_Time/*, String item_Name*/, boolean confirmed,int step,int line_No) {
        Prod_Order_No = prod_Order_No;
        Item_No = item_No;
        Strat_Time = strat_Time;
        End_Time = end_Time;
        //Item_Name = item_Name;
        Confirmed = confirmed;
        Step = step;
        Line_No = line_No;
    }

    public ProdConfirmBomItemsAddon(){

    }

    public int getStep() {
        return Step;
    }

    public void setStep(int step) {
        Step = step;
    }

    public int getLine_No() {
        return Line_No;
    }

    public void setLine_No(int line_No) {
        Line_No = line_No;
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

    public String getStrat_Time() {
        return Strat_Time;
    }

    public void setStrat_Time(String strat_Time) {
        Strat_Time = strat_Time;
    }

    public String getEnd_Time() {
        return End_Time;
    }

    public void setEnd_Time(String end_Time) {
        End_Time = end_Time;
    }
/*
    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }
    */

    public boolean isConfirmed() {
        return Confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        Confirmed = confirmed;
    }
}
