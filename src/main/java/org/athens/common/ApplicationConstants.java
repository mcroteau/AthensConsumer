package org.athens.common;

public class ApplicationConstants {

    public static final String STARTED_STATUS  = "Started";
    public static final String RUNNING_STATUS  = "Running";
    public static final String COMPLETE_STATUS = "Complete";
    public static final String ERROR_STATUS    = "Something went wrong";

    public static final String EMPTY_AUDIT     = "{}";

    public static final String KRONOS_DAILY_REPORT          = "70184453";
    public static final String KRONOS_WEEKLY_REPORT         = "70165985";

    public static final String ATHENS_GROUP                 = "Athens";

    public static final String ATHENS_DAILY_QUARTZ_JOB      = "AthensQuartzDaily";
    public static final String ATHENS_WEEKLY_QUARTZ_JOB     = "AthensQuartzWeekly";
    public static final String ATHENS_QUARTZ_DAILY_TRIGGER  = "AthensQuartzDailyTrigger";
    public static final String ATHENS_QUARTZ_WEEKLY_TRIGGER = "AthensQuartzWeeklyTrigger";

    //public static final String DAILY_JOB_QUARTZ_EXPRESSION  = "1 24 6,21 * * ?";
    public static final String WEEKLY_JOB_QUARTZ_EXPRESSION = "1 27 6,21 * * ?";
    public static final String DAILY_JOB_QUARTZ_EXPRESSION  = "1 */2 * * * ?";

}
