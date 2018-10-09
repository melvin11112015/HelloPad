package com.gki.managerment.bean;

import java.io.Serializable;

/**
 * 生产单号带出来的信息的bean
 *
 * @author Lawson
 */
public class ProdOrderInfo implements Serializable {

    public String Key;
    public int Status;
    public Boolean StatusSpecified;
    public String No;
    public String Description;
    public String Search_Description;
    public String Description_2;
    public String Creation_Date;
    public Boolean Creation_DateSpecified;
    public String Last_Date_Modified;
    public Boolean Last_Date_ModifiedSpecified;
    public int Source_Type;
    public Boolean Source_TypeSpecified;
    public String Source_No;
    public Boolean Comment;
    public Boolean CommentSpecified;
    public String Due_Date;
    public Boolean Due_DateSpecified;
    public String Finished_Date;
    public Boolean Finished_DateSpecified;
    public float Quantity;
    public Boolean QuantitySpecified;
    public String Car_Model;
    public String Production_line;
    public int Shift;
    public Boolean shiftSpecified;
    public float finishedQty;
    public Boolean finishedQtySpecified;
    public float SmallLableQty;
    public Boolean SmallLableQtySpecified;
    public String Acative_Starting_Time;
    public Boolean Acative_Starting_TimeSpecified;
    public String Acative_Starting_Date;
    public Boolean Acative_Starting_DateSpecified;
    public String Parent_Prod_Order_No;
    public String Parent_Item_No;
    public String Parent_Item_Description;
    public int Item_Type;
    public Boolean Item_TypeSpecified;

}
