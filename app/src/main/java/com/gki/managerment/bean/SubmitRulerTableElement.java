package com.gki.managerment.bean;

public class SubmitRulerTableElement {
    //
    // Document_No：作业日报编号（主键）
    // Line_No：行号，流水号（主键）
    // Prod_Order_No：生产单号
    // Production_Date：生产日期
    // Item_No：零件编号，生产零件编号
    // Item_Description：生产零件名称
    // Prod_Line：生产线
    // Shift：班次
    // Worker_Group_ID：班组ID
    // Worker_Group_Name：班组名称
    // RAW_No：原料编号
    // RAW_Description：原料名称
    // Remark：备注
    // Lot_No：原料番号（平板输入数据）
    // Picture_URL：图片路径（平板拍照后上传的目录）
    // Submit_Datetime：提交时间
    // Submit_User_ID：提交人ID
    // Submit_User_Name：提交人名称

    public String Key;
    public String Document_No;
    public int Line_No;
    public String Prod_Order_No;
    public String Production_Date;
    public String Item_No;
    public String Item_Description;
    public String Prod_Line;
    public int Shift;
    public String Worker_Group_ID;
    public String Worker_Group_Name;
    public String RAW_No;
    public String RAW_Description;
    public String Remark;
    public String Lot_No;
    public float Quantity;
    public String Picture_URL;
    public String Submit_Datetime;
    public String Submit_User_ID;
    public String Submit_User_Name;
}
