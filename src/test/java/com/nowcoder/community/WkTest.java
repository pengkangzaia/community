package com.nowcoder.community;

import java.io.IOException;

public class WkTest {

    public static void main(String[] args) {
        String cmd = "C:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltoimage --quality 75 https://www.tencent.com C:/collegefile/JavaProject/work/data/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("Done!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
