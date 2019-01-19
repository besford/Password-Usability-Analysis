package com.comp_3008;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
 
//takes in two time stamps and returns the number of seconds difference between the two
public class TimeDifference {

	public Timestamp convertStringToTimestamp(String str_date) {
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date = formatter.parse(str_date);
			java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
			return timeStampDate;
		}catch (ParseException e) { return null; }
	}
	
	public int getTimeDifference(String time1, String time2) {
		Timestamp timestamp1 = convertStringToTimestamp(time1);
		Timestamp timestamp2 = convertStringToTimestamp(time2);
		int seconds = (int) (timestamp2.getTime() - timestamp1.getTime()) / 1000;
		return seconds;
	}
}