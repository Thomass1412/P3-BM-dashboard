package com.aau.p3.performancedashboard.dashboard;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class OptionsDateIntervalCalculator {
    
    // Logger
    private static final Logger logger = LogManager.getLogger(OptionsDateIntervalCalculator.class);

    /**
     * Calculates the date range based on the given start and end interval enums, start date, and end date.
     * 
     * @param startIntervalEnum The start interval enum.
     * @param endIntervalEnum The end interval enum.
     * @param startDate The optional start date.
     * @param endDate The optional end date.
     * @return An array of two dates representing the calculated start and end dates.
     * @throws IllegalArgumentException if the end date is before the start date.
     */
    public static Date[] getDateRange(OptionsDateIntervalEnum startIntervalEnum,
            OptionsDateIntervalEnum endIntervalEnum, Optional<Date> startDate, Optional<Date> endDate) {
        
        logger.info("Calculating date range for start interval: " + startIntervalEnum + " and end interval: " + endIntervalEnum);
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        Date calculatedStartDate;
        Date calculatedEndDate;

        // Calculate the start date
        calculatedStartDate = calculateStartDate(startIntervalEnum, startCalendar, startDate);
        logger.debug("Calculated start date: " + calculatedStartDate);

        // Calculate the end date
        calculatedEndDate = calculateEndDate(endIntervalEnum, endCalendar, endDate);
        logger.debug("Calculated end date: " + calculatedEndDate);

        // Ensure that the end date is not before the start date
        if (calculatedEndDate.before(calculatedStartDate)) {
            logger.error("End date is before start date. Throwing IllegalArgumentException");
            throw new IllegalArgumentException("End date must not be before start date");
        }

        logger.info("Calculated date range: " + calculatedStartDate + " - " + calculatedEndDate);
        return new Date[] { calculatedStartDate, calculatedEndDate };
    }

    /**
     * Calculates the start date based on the given interval, calendar, and custom date (if applicable).
     *
     * @param intervalEnum the interval enum representing the date interval
     * @param calendar the calendar object used for date calculations
     * @param customDate the optional custom date provided for the CUSTOM interval
     * @return the calculated start date
     * @throws IllegalArgumentException if the interval is unknown or if a start date is required for the CUSTOM interval but not provided
     */
    private static Date calculateStartDate(OptionsDateIntervalEnum intervalEnum, Calendar calendar,
            Optional<Date> customDate) {
        switch (intervalEnum) {
            case TODAY:
            case YESTERDAY:
            case THIS_WEEK:
            case LAST_WEEK:
            case THIS_MONTH:
            case LAST_MONTH:
            case THIS_YEAR:
            case LAST_YEAR:
                return calculateFixedStartDate(intervalEnum, calendar);
            case CUSTOM:
                return customDate.orElseThrow(
                        () -> new IllegalArgumentException("Start date must be provided for CUSTOM interval"));
            default:
                throw new IllegalArgumentException("Unknown Start Date Interval");
        }
    }

    /**
     * Calculates the end date based on the given interval, calendar, and custom date (if applicable).
     *
     * @param intervalEnum the interval enum representing the date interval
     * @param calendar the calendar object used for date calculations
     * @param customDate the optional custom date, if the interval is CUSTOM
     * @return the calculated end date
     * @throws IllegalArgumentException if the interval is unknown or if the custom date is not provided for CUSTOM interval
     */
    private static Date calculateEndDate(OptionsDateIntervalEnum intervalEnum, Calendar calendar,
            Optional<Date> customDate) {
        switch (intervalEnum) {
            case TODAY:
            case YESTERDAY:
            case THIS_WEEK:
            case LAST_WEEK:
            case THIS_MONTH:
            case LAST_MONTH:
            case THIS_YEAR:
            case LAST_YEAR:
                return calculateFixedEndDate(intervalEnum, calendar);
            case CUSTOM:
                return customDate.orElseThrow(
                        () -> new IllegalArgumentException("End date must be provided for CUSTOM interval"));
            default:
                throw new IllegalArgumentException("Unknown End Date Interval");
        }
    }

    /**
     * Calculates the fixed start date based on the given interval and calendar.
     *
     * @param intervalEnum the interval enum representing the desired date interval
     * @param calendar the calendar object representing the current date
     * @return the calculated fixed start date
     */
    private static Date calculateFixedStartDate(OptionsDateIntervalEnum intervalEnum, Calendar calendar) {
        switch (intervalEnum) {
            case YESTERDAY:
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case THIS_WEEK:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                break;
            case LAST_WEEK:
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                break;
            case THIS_MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case LAST_MONTH:
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case THIS_YEAR:
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                break;
            case LAST_YEAR:
                calendar.add(Calendar.YEAR, -1);
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                break;
            case TODAY:
            default:
                // No adjustment needed for TODAY
                break;
        }
        return getStartOfDay(calendar);
    }

    /**
     * Calculates the fixed end date based on the given interval and calendar.
     *
     * @param intervalEnum the interval enum representing the date interval
     * @param calendar the calendar object representing the current date
     * @return the fixed end date
     */
    private static Date calculateFixedEndDate(OptionsDateIntervalEnum intervalEnum, Calendar calendar) {
        switch (intervalEnum) {
            case YESTERDAY:
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case THIS_WEEK:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case LAST_WEEK:
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                calendar.add(Calendar.DAY_OF_MONTH, 6);
                break;
            case THIS_MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case LAST_MONTH:
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case THIS_YEAR:
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
                break;
            case LAST_YEAR:
                calendar.add(Calendar.YEAR, -1);
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
                break;
            case TODAY:
            default:
                // No adjustment needed for TODAY
                break;
        }
        return getEndOfDay(calendar);
    }

    /**
     * Returns the start of the day for the given calendar.
     *
     * @param calendar the calendar object representing a specific date and time
     * @return the start of the day as a Date object
     */
    private static Date getStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
        * Returns the end of the day for the given calendar.
        *
        * @param calendar the calendar object representing a specific date and time
        * @return the end of the day as a Date object
        */
    private static Date getEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}