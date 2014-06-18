package com.zane001.mobilesafe.engine;

import android.util.Xml;

import com.zane001.mobilesafe.domain.UpdateInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zane001 on 2014/6/11.
 */
public class UpdateInfoParser {
    public static UpdateInfo getUpdateInfo(InputStream is) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");
        UpdateInfo updateInfo = new UpdateInfo();
        int type = parser.getEventType();
        while (type != XmlPullParser.END_DOCUMENT) {
            if(type == XmlPullParser.START_TAG) {
                if("version".equals(parser.getName())) {
                    String version = parser.nextText(); //一个节点
                    updateInfo.setVersion(version);
                } else if("description".equals(parser.getName())){
                    String description = parser.nextText();
                    updateInfo.setDescription(description);
                } else if("apkurl".equals(parser.getName())) {
                    String apkurl = parser.nextText();
                    updateInfo.setApkUrl(apkurl);
                }
            }
            type = parser.next(); //触发下一个事件
        }
        return updateInfo;
    }
}

