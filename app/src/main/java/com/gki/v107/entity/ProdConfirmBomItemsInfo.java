/**
  * Copyright 2018 bejson.com 
  */
package com.gki.v107.entity;;
import java.util.Date;

/**
 * Auto-generated: 2018-10-15 16:18:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ProdConfirmBomItemsInfo {

    private String Prod_Order_No;
    private int Step;
    private int Line_No;
    private String Item_No;
    private String Strat_Time;
    private String End_Time;
    private boolean Confirmed;
    private String Item_Name;
    private String ETag;

    public void setProd_Order_No(String Prod_Order_No) {
         this.Prod_Order_No = Prod_Order_No;
     }
     public String getProd_Order_No() {
         return Prod_Order_No;
     }

    public void setStep(int Step) {
         this.Step = Step;
     }
     public int getStep() {
         return Step;
     }

    public void setLine_No(int Line_No) {
         this.Line_No = Line_No;
     }
     public int getLine_No() {
         return Line_No;
     }

    public void setItem_No(String Item_No) {
         this.Item_No = Item_No;
     }
     public String getItem_No() {
         return Item_No;
     }

    public void setStrat_Time(String Strat_Time) {
         this.Strat_Time = Strat_Time;
     }
     public String getStrat_Time() {
         return Strat_Time;
     }

    public void setEnd_Time(String End_Time) {
         this.End_Time = End_Time;
     }
     public String getEnd_Time() {
         return End_Time;
     }

    public void setConfirmed(boolean Confirmed) {
         this.Confirmed = Confirmed;
     }
     public boolean getConfirmed() {
         return Confirmed;
     }

    public void setItem_Name(String Item_Name) {
         this.Item_Name = Item_Name;
     }
     public String getItem_Name() {
         return Item_Name;
     }

    public void setETag(String ETag) {
         this.ETag = ETag;
     }
     public String getETag() {
         return ETag;
     }

}