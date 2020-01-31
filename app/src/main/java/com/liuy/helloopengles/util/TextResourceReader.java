package com.liuy.helloopengles.util;

import android.content.Context;
import android.view.TextureView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * description:
 * author: freed on 2020/1/29
 * email: 674919909@qq.com
 * version: 1.0
 */
public class TextResourceReader {
    public static String readTextFileFromResource(Context context,int resourceId){

        StringBuilder body=new StringBuilder();
        try {
            InputStream inputStream=context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine=bufferedReader.readLine())!=null){
                body.append(nextLine);
                body.append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body.toString();
    }
}
