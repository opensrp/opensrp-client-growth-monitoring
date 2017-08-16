package org.smartregister.growthmonitoring.util;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.repository.EventClientRepository;

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


