package com.aau.p3.performancedashboard.dashboard;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class OptionsDateIntervalCalculator {
    public static Date[] getDateRange(OptionsDateIntervalEnum startIntervalEnum,
            OptionsDateIntervalEnum endIntervalEnum, Optional<Date> startDate, Optional<Date> endDate) {
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        Date calculatedStartDate;
        Date calculatedEndDate;

        // Calculate the start date
        calculatedStartDate = calculateStartDate(startIntervalEnum, startCalendar, startDate);

        // Calculate the end date
        calculatedEndDate = calculateEndDate(endIntervalEnum, endCalendar, endDate);

        // Ensure that the end date is not before the start date
        if (calculatedEndDate.before(calculatedStartDate)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }

        return new Date[] { calculatedStartDate, calculatedEndDate };
    }

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

    private static Date getStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private static Date getEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}