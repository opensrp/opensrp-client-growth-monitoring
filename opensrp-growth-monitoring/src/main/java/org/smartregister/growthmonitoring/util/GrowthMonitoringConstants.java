package org.smartregister.growthmonitoring.util;


/**
 * Created by keyman on 26/07/2017.
 */
public class GrowthMonitoringConstants {

    public static final String ZSCORE_MALE_URL = "http://www.who.int/childgrowth/standards/wfa_boys_0_5_zscores.txt";
    public static final String ZSCORE_FEMALE_URL = "http://www.who.int/childgrowth/standards/wfa_girls_0_5_zscores.txt";

    public static final String CHILD_TABLE_NAME = "ec_client";

    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String FEMALE = "female";
    public static final String MALE = "male";
    public static final int GRAPH_MONTHS_TIMELINE = 12;
    public static final String ZEIR_ID = "zeir_id";
    public static final String GENDER = "gender";
    public static final String DOB = "dob";
    public static final String PMTCT_STATUS = "pmtct_status";

    public static final class JsonForm {
        public static final String OPENMRS_ENTITY = "openmrs_entity";
        public static final String OPENMRS_ENTITY_ID = "openmrs_entity_id";
        public static final String OPENMRS_ENTITY_PARENT = "openmrs_entity_parent";
        public static final String OPENMRS_DATA_TYPE = "openmrs_data_type";
        public static final String VALUE = "value";
        public static final String KEY = "key";
    }

    public static final class ColumnHeaders {
        public static final String COLUMN_SEX = "sex";
        public static final String COLUMN_MONTH = "month";
        public static final String HEIGHT = "height";
        public static final String COLUMN_L = "l";
        public static final String COLUMN_M = "m";
        public static final String COLUMN_S = "s";
        public static final String COLUMN_SD3NEG = "sd3neg";
        public static final String COLUMN_SD2NEG = "sd2neg";
        public static final String COLUMN_SD1NEG = "sd1neg";
        public static final String COLUMN_SD0 = "sd0";
        public static final String COLUMN_SD1 = "sd1";
        public static final String COLUMN_SD2 = "sd2";
        public static final String COLUMN_SD3 = "sd3";
        public static final String COLUMN_SD = "sd";
    }


}
