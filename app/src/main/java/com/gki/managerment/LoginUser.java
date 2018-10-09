package com.gki.managerment;


public class LoginUser {

    static LoginUser user;
    public String Key;
    public String User_Name = "";        //用户名称
    public String User_ID;                //用户ID
    public String Pswd;                   //用户密码
    public String Prod_Line;             //生产线编号
    public String ProdLineName;         //生产线名称
    public String All_Prod_Line;        //生产线编号
    public String All_ProdLineName;    //生产线名称
    public String LoginTime;            //登录时间
    public String GroupID;             //班组ID
    public String Shift;                 //班次ID
    public int Type;
    public boolean TypeSpecified;

    public static LoginUser getUser() {
        if (user == null) {
            user = new LoginUser();
        }
        return user;
    }

    public String getUserId() {
        return User_ID;
    }

    public String getPswd() {
        return Pswd;
    }

    public String getLoginTime() {
        return User_Name;
    }

    public void setUserId(String strUserId) {
        User_ID = strUserId;
    }

    public void setPswd(String strPswd) {
        Pswd = strPswd;
    }

    public void setLoginTime(String strLoginTime) {
        LoginTime = strLoginTime;
    }

    public static void setUser(LoginUser user) {
        LoginUser.user = user;
    }
}
