package com.gratus.idp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtil {


    private static final String DATE_PATTERN =
            "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";


    /**
     * Validate date format with regular expression
     * @return true valid date format, false invalid date format
     */
    public DateTimeUtil(){

    }
    public static boolean validate(final String date){
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(DATE_PATTERN);
        matcher = pattern.matcher(date);

        if(matcher.matches()){
            matcher.reset();

            if(matcher.find()){
                String day = matcher.group(1);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));

                if (day.equals("31") &&
                        (month.equals("4") || month .equals("6") || month.equals("9") ||
                                month.equals("11") || month.equals("04") || month .equals("06") ||
                                month.equals("09"))) {
                    return false; // only 1,3,5,7,8,10,12 has 31 days
                }

                else if (month.equals("2") || month.equals("02")) {
                    //leap year
                    if(year % 4==0){
                        if(day.equals("30") || day.equals("31")){
                            return false;
                        }
                        else{
                            return true;
                        }
                    }
                    else{
                        if(day.equals("29")||day.equals("30")||day.equals("31")){
                            return false;
                        }
                        else{
                            return true;
                        }
                    }
                }

                else{
                    return true;
                }
            }

            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    public static String DateConverter(String date){
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date d=dateFormat.parse(date);
           return d.toString();
        }
        catch(Exception e) {
            //java.text.ParseException: Unparseable date: Geting error
            System.out.println("Excep"+e);
        }
        return null;
    }

    public static String currentdate(){
        int Year, Month, Day ;
        Calendar calendar ;
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        String day = Day+"";
        String month = (Month+1)+"";
        if(Day<10){
            day = "0"+Day;
        }
        if(Month+1<10){
            month = "0"+(Month+1);
        }
        return  day + "/" + (month) + "/" + Year;
    }
}
