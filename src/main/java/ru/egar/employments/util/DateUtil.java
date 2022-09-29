package ru.egar.employments.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateUtil {
    public static LocalDate unixToLocalDate(Long date){
        return LocalDate.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("UTC"));
    }
}
