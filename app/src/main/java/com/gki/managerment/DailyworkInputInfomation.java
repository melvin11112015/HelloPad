package com.gki.managerment;


public class DailyworkInputInfomation {

    static DailyworkInputInfomation dailyworkInput;
    public String Prod_Line;//生产线
    public String Prod_Date;//生产日期
    public String Shift;//班次
    public String Work_Group_Name;//班组

    public static void setDailyworkInput(DailyworkInputInfomation myDailyworkInput) {
        DailyworkInputInfomation.dailyworkInput = myDailyworkInput;
    }

    public static DailyworkInputInfomation DailyworkInput() {
        if (dailyworkInput == null) {
            dailyworkInput = new DailyworkInputInfomation();
        }
        return dailyworkInput;
    }
}
