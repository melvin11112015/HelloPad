package com.gki.managerment.bean;

import java.io.Serializable;

/**
 * 班组信息
 *
 * @author Lawson
 */
public class WorkerGroup implements Serializable {

    public String key;
    public String Worker_Group_ID;
    public String Worker_Group_Name;
    public String Prod_Line;
    public Integer Shift;
    public Boolean ShiftSpecified;
    public Integer Members;
    public Boolean MembersSpecified;
}
