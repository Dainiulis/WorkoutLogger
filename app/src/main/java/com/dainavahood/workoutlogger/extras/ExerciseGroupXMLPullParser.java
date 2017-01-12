package com.dainavahood.workoutlogger.extras;

import android.content.Context;
import android.content.res.Resources;

import com.dainavahood.workoutlogger.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExerciseGroupXMLPullParser {

    private static final String GROUP_NAME = "groupName";

    private String currentGroup = null;
    private String currentTag = null;
    List<String> groups = new ArrayList<>();

    public List<String> parseXML(Context context) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            InputStream stream = context.getResources().openRawResource(R.raw.exercise_groups);
            xpp.setInput(stream, null);

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    handleStartTag(xpp.getName());
                } else if (eventType == XmlPullParser.END_TAG) {
                    currentTag = null;
                } else if (eventType == XmlPullParser.TEXT) {
                    handleText(xpp.getText());
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
        } catch (Resources.NotFoundException e) {
        } catch (IOException e) {
        }

        return groups;

    }

    private void handleStartTag(String name) {
        if (name.equals("exerciseGroup")) {
            currentGroup = "";
        } else {
            currentTag = name;
        }
    }

    private void handleText(String text) {
        if (currentGroup != null && currentTag != null) {
            if (currentTag.equals(GROUP_NAME)) {
                currentGroup = text;
                groups.add(currentGroup);
            }
        }
    }
}
