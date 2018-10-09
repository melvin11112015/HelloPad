package com.gki.managerment.bean;

import com.gki.managerment.util.DateUtils;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * 生产线号状态
 *
 * @author Lawson
 */
public class ProdLineStatus implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4435602396024183366L;
    private String Key;
    private String Prod_Line;
    private String Production_Date;
    private Boolean Production_DateSpecified;
    private int Stop_Type;
    private Boolean Stop_TypeSpecified;
    private String Starting_Time;
    private Boolean Starting_TimeSpecified;
    private String Ending_Time;
    private Boolean Ending_TimeSpecified;
    private float Minutes;
    private Boolean MinutesSpecified;
    private String Remark;
    private String Submit_Datetime;
    private Boolean Submit_DatetimeSpecified;
    private String Submit_User_ID;
    private String Submit_User_Name;
    private String StartTime;
    private String EndTime;
    private String Fault_Position_Code;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getProd_Line() {
        return Prod_Line;
    }

    public void setProd_Line(String prod_Line) {
    	Prod_Line = prod_Line;
    }
    public String getProduction_Date() {
        return Production_Date;
    }

    public void setProduction_Date(String production_Date) {
        Production_Date = production_Date;
    }

    public Boolean getProduction_DateSpecified() {
        return Production_DateSpecified;
    }

    public void setProduction_DateSpecified(Boolean production_DateSpecified) {
        Production_DateSpecified = production_DateSpecified;
    }
   /* public int getStopType() {
        return StopType;
    }

    public void setStopType(int stopType) {
    	StopType = stopType;
    }*/
    public int getStop_Type() {
        return Stop_Type;
    }

    public void setStop_Type(int stop_Type) {
    	Stop_Type = stop_Type;
    }

    public Boolean getStop_TypeSpecified() {
        return Stop_TypeSpecified;
    }

    public void setStop_TypeSpecified(Boolean stop_TypeSpecified) {
        Stop_TypeSpecified = stop_TypeSpecified;
    }

    public String getStarting_Time() {
        return Starting_Time;
    }

    public void setStarting_Time(String starting_Time) {
        Starting_Time = starting_Time;
    }

    public Boolean getStarting_TimeSpecified() {
        return Starting_TimeSpecified;
    }

    public void setStarting_TimeSpecified(Boolean starting_TimeSpecified) {
        Starting_TimeSpecified = starting_TimeSpecified;
    }

    public String getEnding_Time() {
        return Ending_Time;
    }

    public void setEnding_Time(String ending_Time) {
        Ending_Time = ending_Time;
    }

    public Boolean getEnding_TimeSpecified() {
        return Ending_TimeSpecified;
    }

    public void setEnding_TimeSpecified(Boolean ending_TimeSpecified) {
        Ending_TimeSpecified = ending_TimeSpecified;
    }

    public float getMinutes() {
        if (Starting_Time.equals(null) || Starting_Time.equals("") || Ending_Time.equals(null) || Ending_Time.equals(""))
            return 0;
        else
        {
            long aDiff = DateUtils.parseString2Date(Ending_Time,"yyyy-MM-dd HH:mm:ss").getTime() - DateUtils.parseString2Date(Starting_Time,"yyyy-MM-dd HH:mm:ss").getTime();
            float fTime = (float)(aDiff/60000.00);
            return fTime;
        }
    }

    public void setMinutes(float minutes) {
        Minutes = minutes;
    }

    public Boolean getMinutesSpecified() {
        return MinutesSpecified;
    }

    public void setMinutesSpecified(Boolean minutesSpecified) {
        MinutesSpecified = minutesSpecified;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getSubmit_Datetime() {
        return Submit_Datetime;
    }

    public void setSubmit_Datetime(String submit_Datetime) {
        Submit_Datetime = submit_Datetime;
    }

    public Boolean getSubmit_DatetimeSpecified() {
        return Submit_DatetimeSpecified;
    }

    public void setSubmit_DatetimeSpecified(Boolean submit_DatetimeSpecified) {
        Submit_DatetimeSpecified = submit_DatetimeSpecified;
    }

    public String getSubmit_User_ID() {
        return Submit_User_ID;
    }

    public void setSubmit_User_ID(String submit_User_ID) {
        Submit_User_ID = submit_User_ID;
    }

    public String getSubmit_User_Name() {
        return Submit_User_Name;
    }

    public void setSubmit_User_Name(String submit_User_Name) {
        Submit_User_Name = submit_User_Name;
    }
   
    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
    	StartTime = startTime;
    }
    
    public String getEndTime() {
        return EndTime;
    }

    public void EndTime(String endTime) {
    	EndTime = endTime;
    }

    public String getFault_Position_Code() {
        return Fault_Position_Code;
    }

    public void setFault_Position_Code(String fault_Position_Code) {
        Fault_Position_Code = fault_Position_Code;
    }
}
