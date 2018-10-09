package com.gki.managerment.http;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.gki.managerment.util.StringUtils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class WebServiceParameter {
    private static String NAMESPACE = AppContants.NameSpace;
    //private String methodName = "GetItemSceneList";
    //private static String SOAP_ACTION1=NAMESPACE+"/GetItemSceneList";
    //private static String URL=AppContants.AppUrL+"UserSV.asmx";
    /**
     * @param url
     * @param methodName    ������
     * @param propertyInfos ����
     * @return josn�ַ�
     */
    public static String getObject(String url, String methodName, PropertyInfo[] propertyInfos) {
        try {
            //Object result = null;
            String SOAP_ACTION1 = NAMESPACE + methodName;
            String serviceUrl = AppContants.ServicePath + url;
            System.out.println("url:" + serviceUrl);
            System.out.println("SOAP_ACTION1:" + SOAP_ACTION1);
            SoapObject objSoap = new SoapObject(NAMESPACE, methodName);
            for (int i = 0; i < propertyInfos.length; i++) {
                objSoap.addProperty(propertyInfos[i]);
            }
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = objSoap;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(objSoap);
//		        AndroidHttpTransport httpTranstation = new AndroidHttpTransport(serviceUrl);
            HttpTransportSE httpTranstation = new HttpTransportSE(serviceUrl);
            httpTranstation.debug = true;

            httpTranstation.call(SOAP_ACTION1, envelope);
            System.out.println("GetWebServiceData in:");
            SoapObject result = (SoapObject) envelope.bodyIn;
            //SoapObject result = (SoapObject) envelope.getResponse();
            String detail;
            detail = result.getProperty(methodName + "Result").toString();
            if (StringUtils.isNotEmpty(detail) && detail.equalsIgnoreCase("anytype{}"))
            {
                detail = "";
            }
            System.out.println("GetWebServiceData Result:" + detail);
            return detail;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException");
            return "Error:网络连接异常，请检查您的网络！";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            System.out.println("XmlPullParserException");
            return "Error:数据转换失败，地址："+url+"，方法名："+methodName+"！";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("错误："+e.getMessage()+"，地址："+url+"，方法名："+methodName+"！");
            return "Error:"+e.getMessage()+"，地址："+url+"，方法名："+methodName+"！";
        }
    }

    public static String ISOtoGBK(String s) {
        String str = "";
        try {
            if (s == null || s == "" || s.equals("")) {
                str = s;
            } else {
                str = new String(s.getBytes("ISO8859-1"), "GBK");
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    public static boolean getPropertyValue(Object flag, boolean b) {
        // TODO Auto-generated method stub
        return false;
    }
}