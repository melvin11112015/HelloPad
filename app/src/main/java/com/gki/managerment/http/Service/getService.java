package com.gki.managerment.http.Service;

import com.gki.managerment.http.WebServiceParameter;

import org.ksoap2.serialization.PropertyInfo;

/**
 * 获取服务接口的工具
 *
 * @author Lawson
 */
@SuppressWarnings("all")
public class getService {
    //取得最新版本的地址，如不需要更新则返回""
    public static String GetNewVersion(String strOldVersion) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strOldVersion");
        propertyInfos[0].setValue(strOldVersion);
        String result = WebServiceParameter.getObject("SystemInfo.asmx", "GetPadVersion", propertyInfos);
        return result;
    }

    // 根据生产单获取信息
    public static String GetProdOrderService(String strCode) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strCode");
        propertyInfos[0].setValue(strCode);
        String result = WebServiceParameter.getObject("ProdOrderSV.asmx",
                "GetData", propertyInfos);
        return result;
    }

    // 获取班组
    public static String GetWorkerGroupService(String strCode) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strCode");
        propertyInfos[0].setValue(strCode);
        String result = WebServiceParameter.getObject("WorkerGroupSV.asmx",
                "GetData", propertyInfos);
        return result;
    }

    // 获取报废情况类型
    public static String GetProdMandayParavService(String strCode,
                                                   String strType) {
        PropertyInfo[] propertyInfos = new PropertyInfo[2];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strCode");
        propertyInfos[0].setValue(strCode);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strType");
        propertyInfos[1].setValue(strType);
        String result = WebServiceParameter.getObject("ProdMandayDetailSV.asmx",
                "GetData", propertyInfos);
        return result;
    }

    //新增配员接口
    public static String GetData_Staff(String strProdLine, String strProductionDate, String strShift, String strGroupId) {
        PropertyInfo[] propertyInfos = new PropertyInfo[4];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strProdLine");
        propertyInfos[0].setValue(strProdLine);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strProductionDate");
        propertyInfos[1].setValue(strProductionDate);
        propertyInfos[2] = new PropertyInfo();
        propertyInfos[2].setName("strShift");
        propertyInfos[2].setValue(strShift);
        propertyInfos[3] = new PropertyInfo();
        propertyInfos[3].setName("strGroupId");
        propertyInfos[3].setValue(strGroupId);
        System.out.println("GetWorkDailyReportService begin:");
        String result = WebServiceParameter.getObject("WorkDailyReportSV.asmx",
                "GetData", propertyInfos);
        return result;
    }

    // 初始配员
    public static String InitBYService(String strProdLine, String strProdDate,String strShift,String strGroupId) {
        PropertyInfo[] propertyInfos = new PropertyInfo[4];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strProdLine");
        propertyInfos[0].setValue(strProdLine);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strProdDate");
        propertyInfos[1].setValue(strProdDate);
        propertyInfos[2] = new PropertyInfo();
        propertyInfos[2].setName("strShift");
        propertyInfos[2].setValue(strShift);
        propertyInfos[3] = new PropertyInfo();
        propertyInfos[3].setName("strGroupId");
        propertyInfos[3].setValue(strGroupId);
        String result = WebServiceParameter.getObject("WorkerGrpMemberSV.asmx",
                "InitData", propertyInfos);
        return result;
    }

    // 获取班组成员
    public static String GetWorkerGrpMemberService(String strCode) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strCode");
        propertyInfos[0].setValue(strCode);
        String result = WebServiceParameter.getObject("WorkerGrpMemberSV.asmx",
                "GetData", propertyInfos);
        return result;
    }

    //获取生产线状态
    public static String GetProdLineStateService(String strProdLine, int iStopType) {
        PropertyInfo[] propertyInfos = new PropertyInfo[2];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strProdLine");
        propertyInfos[0].setValue(strProdLine);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("iStopType");
        propertyInfos[1].setValue(iStopType);
        String result = WebServiceParameter.getObject("ProdLineStopReportSV.asmx",
                "GetStatus", propertyInfos);
        return result;
    }

    //获取用户生产线信息
    public static String GetUserProdLineService(String strUserId) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strUserId");
        propertyInfos[0].setValue(strUserId);
        String result = WebServiceParameter.getObject("UserSV.asmx",
                "GetProdLine", propertyInfos);
        return result;
    }

    //获取请假类型信息
    public static String GetLeaveCategoryService(String strCode) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strCode");
        propertyInfos[0].setValue(strCode);

        String result = WebServiceParameter.getObject("LeaveCategorySV.asmx",
                "GetData", propertyInfos);
        return result;
    }

    public static String ProdOrderCompSVGetData(String productOrderNo, String orderLineNO, String lineNO) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strOrderNo");
        propertyInfos[0].setValue(productOrderNo);
//        propertyInfos[1] = new PropertyInfo();
//        propertyInfos[1].setName("strOrderLineNo");
//        propertyInfos[1].setValue(orderLineNO);
//        propertyInfos[2] = new PropertyInfo();
//        propertyInfos[2].setName("strLineNo");
//        propertyInfos[2].setValue(lineNO);
        return WebServiceParameter.getObject("ProdOrderCompSV.asmx", "GetData", propertyInfos);
    }

    public static String ProdOrderCompSVGetDetail(String strDocumentNo) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strDocumentNo");
        propertyInfos[0].setValue(strDocumentNo);
        String strResult = WebServiceParameter.getObject("ProdOrderCompSV.asmx", "GetDetail", propertyInfos);
        return strResult;
    }

    //加工确认
    //查询数据
    public static String GetData_Confirm(String strProdLine,String strProdDate) {
        PropertyInfo[] propertyInfos = new PropertyInfo[2];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strProdLine");
        propertyInfos[0].setValue(strProdLine);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strProdDate");
        propertyInfos[1].setValue(strProdDate);
        String strResult = WebServiceParameter.getObject("ProcCondiItemSV.asmx", "GetData", propertyInfos);
        return strResult;
    }
    //加工确认
    //删除数据
    public static String DeleteData_Confirm(String strProdLine,String strProdDate) {
        PropertyInfo[] propertyInfos = new PropertyInfo[2];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strProdLine");
        propertyInfos[0].setValue(strProdLine);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strProdDate");
        propertyInfos[1].setValue(strProdDate);
        String strResult = WebServiceParameter.getObject("ProcCondiItemSV.asmx", "DeleteData", propertyInfos);
        return strResult;
    }
    //加工确认
    //初始化数据
    public static String InitData_Confirm(String strProdNo,String strProdLine,String strProdDate,String strUserId) {
        PropertyInfo[] propertyInfos = new PropertyInfo[4];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strProdNo");
        propertyInfos[0].setValue(strProdNo);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strProdLine");
        propertyInfos[1].setValue(strProdLine);
        propertyInfos[2] = new PropertyInfo();
        propertyInfos[2].setName("strProdDate");
        propertyInfos[2].setValue(strProdDate);
        propertyInfos[3] = new PropertyInfo();
        propertyInfos[3].setName("strUserId");
        propertyInfos[3].setValue(strUserId);
        String strResult = WebServiceParameter.getObject("ProcCondiItemSV.asmx", "InitData", propertyInfos);
        return strResult;
    }

    //获取加工条件输入时间列表串
    public static String GetInputTimes_Confirm(String strProdLine,String strProdDate) {
        PropertyInfo[] propertyInfos = new PropertyInfo[2];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strProdLine");
        propertyInfos[0].setValue(strProdLine);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strProdDate");
        propertyInfos[1].setValue(strProdDate);
        String strResult = WebServiceParameter.getObject("ProcCondiItemSV.asmx", "GetInputTimes", propertyInfos);
        return strResult;
    }

    //加工确认
    //保存数据
    public static String SaveData_Confirm(String strData) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strData");
        propertyInfos[0].setValue(strData);
        String strResult = WebServiceParameter.getObject("ProcCondiItemSV.asmx", "SaveData", propertyInfos);
        return strResult;
    }

    public static String ProdOrderCompSVSaveData(String strData) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strData");
        propertyInfos[0].setValue(strData);
        String strResult = WebServiceParameter.getObject("ProdOrderCompSV.asmx", "SaveData", propertyInfos);
        return strResult;
    }

    public static String ProdOrderCompSVDeleteData(String strKeys) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strKeys");
        propertyInfos[0].setValue(strKeys);
        return WebServiceParameter.getObject("ProdOrderCompSV.asmx", "DeleteData", propertyInfos);
    }

    //取得故障位置列表
    public static String GetFaultPart(String strProdLine) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strLineNo");
        propertyInfos[0].setValue(strProdLine);
        String result = WebServiceParameter.getObject("FaultPositionSV.asmx",
                "GetList", propertyInfos);
        return result;
    }

    public static String ProdMandayListSV_QueryList(String strProdLine, String dairyID, String orderID, String strDate, String strUserId) {
        PropertyInfo[] propertyInfos = new PropertyInfo[5];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strProdLine");
        propertyInfos[0].setValue(strProdLine);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strDocumentNo");
        propertyInfos[1].setValue(dairyID);
        propertyInfos[2] = new PropertyInfo();
        propertyInfos[2].setName("strOrderNo");
        propertyInfos[2].setValue(orderID);
        propertyInfos[3] = new PropertyInfo();
        propertyInfos[3].setName("strDate");
        propertyInfos[3].setValue(strDate);
        propertyInfos[4] = new PropertyInfo();
        propertyInfos[4].setName("strUserId");
        propertyInfos[4].setValue(strUserId);
        String result = WebServiceParameter.getObject("ProdMandayListSV.asmx",
                "QueryList", propertyInfos);
        return result;
    }

    public static String WorkerGroupSV_GetDataByLine(String productLine) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strLine");
        propertyInfos[0].setValue(productLine);
        String result = WebServiceParameter.getObject("WorkerGroupSV.asmx",
                "GetDataByLine", propertyInfos);
        return result;
    }
    //取配员初始信息
    public static String GetWorkGroupInitInfo(String strWorkGroupId) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strWorkGroupId");
        propertyInfos[0].setValue(strWorkGroupId);
        String result = WebServiceParameter.getObject("WorkerGrpMemberSV.asmx",
                "GetInitInfo", propertyInfos);
        return result;
    }

    //取级别默认设置信息
    public static String GetDefaultSetup_WorkGroup(String strGroupId) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strGroupId");
        propertyInfos[0].setValue(strGroupId);
        String result = WebServiceParameter.getObject("WorkerGroupSV.asmx",
                "GetDefaultSetup", propertyInfos);
        return result;
    }

    public static String WorkDailyReportSV_DeleteData(String value) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strKeys");
        propertyInfos[0].setValue(value);
        String result = WebServiceParameter.getObject("WorkDailyReportSV.asmx",
                "DeleteData", propertyInfos);
        return result;
    }

    public static String WorkDailyReportSV_Update(String strData) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strData");
        propertyInfos[0].setValue(strData);
        String result = WebServiceParameter.getObject("WorkDailyReportSV.asmx",
                "Update", propertyInfos);
        return result;
    }

    public static String ProdOrderCompSV_UploadImage(String diaryID, String phonto, String suffix) {
        PropertyInfo[] propertyInfos = new PropertyInfo[3];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strDocumentNo");
        propertyInfos[0].setValue(diaryID);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strData");
        propertyInfos[1].setValue(phonto);
        propertyInfos[2] = new PropertyInfo();
        propertyInfos[2].setName("strSuffix");
        propertyInfos[2].setValue(suffix);
        String result = WebServiceParameter.getObject("ProdOrderCompSV.asmx",
                "UploadImage", propertyInfos);
        return result;
    }

    public static String WorkerGrpMemberSV_GetData(String strCode) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strCode");
        propertyInfos[0].setValue(strCode);
        String result = WebServiceParameter.getObject("WorkerGrpMemberSV.asmx", "GetData", propertyInfos);
        return result;
    }

    public static String InsertData_Staff(String strData,String strGroupId) {
        PropertyInfo[] propertyInfos = new PropertyInfo[2];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strData");
        propertyInfos[0].setValue(strData);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strGroupId");
        propertyInfos[1].setValue(strGroupId);
        String result = WebServiceParameter.getObject("WorkDailyReportSV.asmx", "InsertData", propertyInfos);
        return result;
    }

    //获取用户名的服务
    public String GetUserInfoService(String UserName) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strUserId");
        propertyInfos[0].setValue(UserName);
        String result = WebServiceParameter.getObject("UserSV.asmx", "GetUserInfo", propertyInfos);
        return result;
    }

    //检测用户名和密码服务
    public String CheckUserPwdService(String UserName, String UserPwd) {
        PropertyInfo[] propertyInfos = new PropertyInfo[2];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strUserId");
        propertyInfos[0].setValue(UserName);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strPswd");
        propertyInfos[1].setValue(UserPwd);
        String result = WebServiceParameter.getObject("UserSV.asmx", "CheckPswdForPAD", propertyInfos);
        return result;
    }
    //检测用户名和密码服务
    public String UpdatePswd_User(String strUserId, String strOldPswd, String strNewPswd) {
        PropertyInfo[] propertyInfos = new PropertyInfo[3];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strUserId");
        propertyInfos[0].setValue(strUserId);
        propertyInfos[1] = new PropertyInfo();
        propertyInfos[1].setName("strOldPswd");
        propertyInfos[1].setValue(strOldPswd);
        propertyInfos[2] = new PropertyInfo();
        propertyInfos[2].setName("strNewPswd");
        propertyInfos[2].setValue(strNewPswd);
        String result = WebServiceParameter.getObject("UserSV.asmx", "UpdatePswd", propertyInfos);
        return result;
    }

    //取得工序列表
    public static String GetWorkerPosition() {
        PropertyInfo[] propertyInfos = new PropertyInfo[0];
        String result = WebServiceParameter.getObject("WorkerPositionSV.asmx", "GetData", propertyInfos);
        return result;
    }
}