package com.gki.managerment.bean;

import java.io.Serializable;
import java.util.Date;

public class WorkDailyReportAdd implements Serializable {

    public String Prod_Line;
    public Date Prodcuction_Date;
    public Integer Shift;
    public String Worker_Group_ID;
    public String Worker_Group_Name;
    public String Worker_ID;
    public String Worker_Name;
    public Integer Routing;
    public float Working_Time;
    public float Over_Time;
    public boolean Support_Time;
    public Boolean Over_Time_Request;
    public Boolean Over_Time_Approval;
    public float Over_Time_Day;
    public float Over_Time_Night;
    public float Over_Time_Day_Temp;
    public float Over_Time_Night_Temp;
    public Boolean Over_Time_Day_Request;
    public Boolean Over_Time_Night_Request;
    public Boolean Over_Time_Day_Temp_Request;
    public Boolean Over_Time_Night_Temp_Request;
    public String Leave_Category_Code;
    public String Leave_Category_Description;
    public float Leave_Hours;
    public Boolean Meal_Allowance_Day_Req;
    public Boolean Meal_Allowance_Night_Req;
    public Boolean Allowance_Day_Req;
    public Boolean Allowance_Night_Req;

}
