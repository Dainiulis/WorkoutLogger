package com.dainavahood.workoutlogger.extras;

import android.content.Context;
import android.content.res.Resources;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.model.Exercise;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExercisesXMLPullParser {
    private static final String EXERCISE_NAME = "exerciseName";
    private static final String EXERCISE_TYPE = "exerciseType";
    private static final String EXERCISE_GROUP = "exerciseGroup";

    private Exercise currentExercise = null;
    private String currentTag = null;
    List<Exercise> exercises = new ArrayList<>();

    public List<Exercise> parseXML(Context context) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            InputStream stream = context.getResources().openRawResource(R.raw.exercises);
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

        return exercises;

    }

    private void handleStartTag(String name) {
        if (name.equals("exercise")) {
            currentExercise = new Exercise();
            exercises.add(currentExercise);
        } else {
            currentTag = name;
        }
    }

    private void handleText(String text) {
        if (currentExercise != null && currentTag != null) {
            if (currentTag.equals(EXERCISE_NAME)) {
                currentExercise.setName(text);
            } else if (currentTag.equals(EXERCISE_TYPE)) {
                currentExercise.setExerciseType(text);
            } else if (currentTag.equals(EXERCISE_GROUP)) {
                currentExercise.setExerciseGroup(text);
            }
        }
    }
}
