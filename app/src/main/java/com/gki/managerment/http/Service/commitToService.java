package com.gki.managerment.http.Service;

import com.gki.managerment.http.WebServiceParameter;

import org.ksoap2.serialization.PropertyInfo;

public class commitToService {
    public static String SendProdMandayListService(String strProdMandayList) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strProdMandayList");
        propertyInfos[0].setValue(strProdMandayList);
        String result = WebServiceParameter.getObject("ProdMandayListSV.asmx",
                "SaveData", propertyInfos);
        return result;
    }

    public static String SendWorkDailyReportService(String strWorkDailyReportList) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strWorkDailyReportList");
        propertyInfos[0].setValue(strWorkDailyReportList);
        String result = WebServiceParameter.getObject("WorkDailyReportSV.asmx",
                "SaveData", propertyInfos);
        return result;
    }

    public static String SendWorkDailyReportUpdateService(String strWorkDailyReportList) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strWorkDailyReportList");
        propertyInfos[0].setValue(strWorkDailyReportList);
        String result = WebServiceParameter.getObject("WorkDailyReportSV.asmx",
                "UpdateData", propertyInfos);
        return result;
    }

    public static String SendProdLineStateService(String strProdLineStopReportList) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strData");
        propertyInfos[0].setValue(strProdLineStopReportList);
        String result = WebServiceParameter.getObject("ProdLineStopReportSV.asmx",
                "SaveData", propertyInfos);
        return result.toString();
    }

    public static String SendWorkDailyReportNewService(String strUserId) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strUserId");
        propertyInfos[0].setValue(strUserId);
        String result = WebServiceParameter.getObject("ProdMandayListSV.asmx",
                "NewDaily", propertyInfos);
        return result;
    }

    public static String GetWorkDailyReportService(String strDocumentNo) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strDocumentNo");
        propertyInfos[0].setValue(strDocumentNo);
        String result = WebServiceParameter.getObject("ProdMandayListSV.asmx", "GetDataByDocumentNo", propertyInfos);
        return result;
    }

    public static String SaveDailyDetail(String strDetailList) {
        PropertyInfo[] propertyInfos = new PropertyInfo[1];
        propertyInfos[0] = new PropertyInfo();
        propertyInfos[0].setName("strData");
        propertyInfos[0].setValue(strDetailList);
        String result = WebServiceParameter.getObject("ProdMandayDetailSV.asmx",
                "SaveData", propertyInfos);
        return result;
    }
}
