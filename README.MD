### GKI PAD 项目

## 目录结构
└─com
      └─gki
          ├─managerment //v1.06版本及以前相关模块
          └─v107 //v1.07版本及以后相关模块
              ├─activity //包括交接班留言和检查报告的activity
              ├─adapter //检查报告中3个模块适配器
              ├─entity //相关API的实体
              ├─fragment //检查报告中3个模块fragment
              ├─myinterface
              ├─net //基于Retrofit网络API请求类
              └─tool //时区调整和时间格式化工具类

## 签名
在app/GKI_Barcode.keystore签名文件，签名信息已在app module下build.gradle配置