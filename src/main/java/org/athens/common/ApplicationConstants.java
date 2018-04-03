package org.athens.common;

public class ApplicationConstants {

    public static final String STARTED_STATUS  = "Started";
    public static final String RUNNING_STATUS  = "Running";
    public static final String COMPLETE_STATUS = "Complete";
    public static final String ERROR_STATUS    = "Something went wrong";

    public static final String EMPTY_AUDIT     = "{}";

    public static final String ATHENS_GROUP             = "Athens";
    public static final String ATHENS_QUARTZ_TRIGGER    = "AthensQuartzTrigger";
    public static final String ATHENS_DAILY_KRNWH_JOB   = "AthensKrnwhDaily";
    public static final String ATHENS_WEEKLY_KRNWH_JOB  = "AthensKrnwhWeekly";

    public static final String QUARTZ_DAILY_JOB_EXPRESSION  = "1 1 9,15 * * ?";
    public static final String QUARTZ_WEEKLY_JOB_EXPRESSION = "1 1 1 * * ?";

}
