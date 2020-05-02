package com.nowcoder.community;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {


    @Test
    public void testFormat() {
        Date date = new Date();
        System.out.println(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s = sdf.format(date);
        System.out.println(sdf.format(date));
        System.out.println(sdf.format(new Date()));

    }


}
