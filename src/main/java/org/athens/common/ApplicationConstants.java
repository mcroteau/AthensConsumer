package org.athens.common;

public class ApplicationConstants {

    public static final String STARTED_STATUS      = "Started";
    public static final String RUNNING_STATUS      = "Running";
    public static final String COMPLETE_STATUS     = "Complete";
    public static final String ERROR_STATUS        = "Something went wrong";
    public static final String INTERRUPTED_STATUS  = "Interrupted";

    public static final String EMPTY_AUDIT     = "{}";
    public static final String CSV_DELIMETER   = ",";

    public static final int KRONOS_PUNCH_DATE_COLUMN       = 1;
    public static final int KRONOS_EMPLOYEE_ID_COLUMN      = 0;
    public static final int KRONOS_BADGE_ID_COLUMN         = 4;
    public static final int KRONOS_EMPLOYEE_STATUS_COLUMN  = 2;
    public static final int KRONOS_TERMINAL_COLUMN         = 3;

    public static final String KRONOS_TOKEN_LOOKUP          = "token";
    public static final String KRONOS_DAILY_REPORT          = "70184283";
    public static final String KRONOS_WEEKLY_REPORT         = "70165985";
    public static final String KRONOS_LOGIN_URI             = "https://secure4.saashr.com/ta/rest/v1/login";
    public static final String KRONOS_BASE_REPORT_URI       = "https://secure4.saashr.com/ta/rest/v1/report/saved/";

    public static final String KRNWH_DAO_LOOKUP             = "kronosWorkHourDao";
    public static final String KRNWH_LOG_DAO_LOOKUP         = "kronosIngestLogDao";
    public static final String KRNWH_JOB_SETTINGS_LOOKUP    = "kronosWorkHourJobSettings";
    public static final String QUARTZ_JOB_STATS_LOOKUP      = "quartzJobStats";

    public static final String ATHENS_GROUP                 = "Athens";

    public static final String ATHENS_QUARTZ_JOB_DESCRIPTION_LOOKUP = "description";
    public static final String ATHENS_DAILY_QUARTZ_JOB_DESCRIPTION  = "Daily";
    public static final String ATHENS_WEEKLY_QUARTZ_JOB_DESCRIPTION = "Weekly";

    public static final String ATHENS_DAILY_QUARTZ_JOB      = "AthensQuartzDaily";
    public static final String ATHENS_WEEKLY_QUARTZ_JOB     = "AthensQuartzWeekly";
    public static final String ATHENS_QUARTZ_DAILY_TRIGGER  = "AthensQuartzDailyTrigger";
    public static final String ATHENS_QUARTZ_WEEKLY_TRIGGER = "AthensQuartzWeeklyTrigger";

    public static final String DAILY_JOB_QUARTZ_EXPRESSION  = "1 */3 * * * ?";
    //public static final String DAILY_JOB_QUARTZ_EXPRESSION   = "0 0 0 1 1 ? 2200";
    public static final String WEEKLY_JOB_QUARTZ_EXPRESSION = "1 */1 * * * ?";
    //public static final String WEEKLY_JOB_QUARTZ_EXPRESSION  = "0 0 0 1 1 ? 2200";

}
