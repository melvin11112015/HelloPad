package com.gki.managerment.bean;

import java.io.Serializable;
import java.util.Date;

public class WorkDailyReport implements Serializable {
    public String Key;
    public String Prod_Line;
    public String Production_Date;
    public boolean Production_DateSpecified;
    public int Shift;
    public boolean ShiftSpecified;
    public String Worker_Group_ID;
    public String Worker_Group_Name;
    public String Worker_ID;
    public String Worker_Name;
    public int Routing;
    public String RoutingName;
    public float Working_Time;
    public float Over_Time;
    public boolean Support_Time;
    public boolean Over_Time_Request;
    public boolean Over_Time_Approval;
    public float Over_Time_Day;
    public float Over_Time_Night;
    public float Over_Time_Day_Temp;
    public float Over_Time_Night_Temp;
    public boolean Over_Time_Day_Request;
    public boolean Over_Time_Night_Request;
    public boolean Over_Time_Day_Temp_Request;
    public boolean Over_Time_Night_Temp_Request;
    public String Leave_Category_Code;
    public String Leave_Category_Description;
    public float Leave_Hours;
    public boolean Meal_Allowance_Day_Req;
    public boolean Meal_Allowance_Night_Req;
    public boolean Allowance_Day_Req;
    public boolean Allowance_Night_Req;
    public boolean is_selected;

    public float Def_Over_Time_Day;
    public float Def_Over_Time_Day_Temp;
    public float Def_Over_Time_Night;
    public float Def_Over_Time_Night_Temp;
    public float Default_Over_Time;
    public float Default_Working_Time;
    public String Remark;
    public String Worker_Position_Code;
    public String Worker_Position_Description;
    public String Position_Code;
    public String Position_Description;
}