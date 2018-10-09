package com.gki.managerment.bean;

import java.io.Serializable;

public class WorkerGrpMember implements Serializable {
    public String Key;
    public String Worker_Group_ID;
    public String Worker_Group_Name;
    public String Worker_ID;
    public String Worker_Name;
    public String Prod_Line;
    public Integer Shift;
    public Boolean ShiftSpecified;
    public Integer Routing;
    public Boolean RoutingSpecified;
    public float Default_Working_Time;
    public Boolean Default_Working_TimeSpecified;
    public float Default_Over_Time;
    public Boolean Default_Over_TimeSpecified;
    public Integer Sorting;
    public Boolean SortingSpecified;
    public float Def_Over_Time_Day;
    public Boolean Def_Over_Time_DaySpecified;
    public float Def_Over_Time_Night;
    public Boolean Def_Over_Time_NightSpecified;
    public float Def_Over_Time_Day_Temp;
    public Boolean Def_Over_Time_Day_TempSpecified;
    public float Def_Over_Time_Night_Temp;
    public Boolean Def_Over_Time_Night_TempSpecified;
}
