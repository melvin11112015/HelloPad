package com.gki.managerment.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateSpecialNo {
    public static String getNum() {
        String num;
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        int math = (int) (Math.random() * 9000 + 1000);
        num = myFmt.format(now) + math;
        return num;
    }


}
