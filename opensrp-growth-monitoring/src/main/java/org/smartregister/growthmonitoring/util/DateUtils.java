package org.smartregister.growthmonitoring.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by keyman on 26/07/2017.
 */
public class DateUtils extends org.smartregister.util.JsonFormUtils {

    public static DateFormat yyyyMMddTHHmmssSSSZ = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'");

    public static Date getDateFromString(String dateString) {
        Date parsed = null;

        try {
            if(dateString != null && !dateString.equals("null") && dateString.length() > 0) {
                parsed = yyyyMMddTHHmmssSSSZ.parse(dateString.trim());
            }
        } catch (ParseException var3) {
            Log.e("DateUtil", var3.toString(), var3);
        }

        return parsed;
    }
}


