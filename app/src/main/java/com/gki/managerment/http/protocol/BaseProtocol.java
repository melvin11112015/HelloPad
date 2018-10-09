package com.gki.managerment.http.protocol;

import com.gki.managerment.http.HttpHelper;
import com.gki.managerment.http.HttpHelper.HttpResult;
import com.gki.managerment.util.IOUtils;
import com.gki.managerment.util.StringUtils;
import com.gki.managerment.util.UIUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 网络访问的基类
 * <p/>
 * 获取数据 - 解析数据 - 网络缓存
 * <p/>
 * 需要权限: android.permission.INTERNET
 */
public abstract class BaseProtocol<T> {

    /**
     * @param index 表示服务器从哪个位置返回20条数据(用于分页)
     */
    public T getData(int index) {
        // 先判断是否有缓存,如果有,直接加载缓存
        String result = getCache(index);

        if (StringUtils.isEmpty(result)) {
            // 没有缓存,访问网络
            result = getDataFromServer(index);
        }

        // 解析数据
        if (result != null) {
            return parseJson(result);
        }

        return null;
    }

    /**
     * 访问网络,获取数据
     * <p/>
     * index: 表示服务器从哪个位置返回20条数据(用于分页)
     */
    private String getDataFromServer(int index) {
        // http://www.itcast.cn/home?index=100&name=zhangsan&age=20
        // 链接 = 主域名 + 接口字段 + 参数
        HttpResult httpResult = HttpHelper.get(HttpHelper.URL + getKey()
                + "?index=" + index + getParams());
        if (httpResult != null) {
            String result = httpResult.getString();// 获取服务器返回的json字符串
            if (!StringUtils.isEmpty(result)) {
                // 写缓存
                setCache(result, index);
            }
            return result;
        }

        return null;
    }

    /**
     * 返回具体接口的字段, 必须由子类实现
     *
     * @return
     */
    public abstract String getKey();

    /**
     * 接口参数, 必须由子类实现
     *
     * @return
     */
    public abstract String getParams();

    /**
     * 以url(包含参数)为key(文件名), 以json为value(文件内容), 保存在本地 MD5(url)
     */
    public void setCache(String json, int index) {
        // 获取系统缓存目录
        File cacheDir = UIUtils.getContext().getCacheDir();
        // 创建缓存文件
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index
                + getParams());

        FileWriter writer = null;
        try {
            writer = new FileWriter(cacheFile);
            // 缓存有效期 半个小时
            // 在文件第一行写截止日期
            long deadLine = System.currentTimeMillis() + 30 * 60 * 1000;
            writer.write(deadLine + "\n");// 要加换行符
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
        }
    }

    /**
     * 从本地缓存目录中查找,有没有文件名为url(包含参数)的文件存在,如果存在,说明该链接有缓存,否则,没有缓存
     */
    public String getCache(int index) {
        // 获取系统缓存目录
        File cacheDir = UIUtils.getContext().getCacheDir();
        // 创建缓存文件
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index
                + getParams());

        if (cacheFile.exists()) {
            // 有缓存
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(cacheFile));

                String firstLine = reader.readLine();
                long deadLine = Long.parseLong(firstLine);// 获取截止日期

                if (System.currentTimeMillis() < deadLine) {
                    // 缓存有效
                    StringBuffer sb = new StringBuffer();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    return sb.toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(reader);
            }
        }

        return null;
    }

    /**
     * 解析网络数据
     *
     * @param result
     */
    public abstract T parseJson(String result);

}
