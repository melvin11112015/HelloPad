package com.gki.managerment.bean;

import java.io.Serializable;

/**
 * Created by summit on 2/18/16.
 */
public class WorkerGroupSV_GetDataByLineResponse implements Serializable {


    public String Key;
    public String Worker_Group_ID;
    public String Worker_Group_Name;
    public String Prod_Line;
    public int Shift;
    public boolean ShiftSpecified;
    public int Members;
    public boolean MembersSpecified;

}
