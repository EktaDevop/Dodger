package com.esko.Utils.DepamentTimeStrategy;

import java.sql.Time;

public class GRC implements Strategy{

	public String getTimeDuration(Time checkIn, Time checkOut,Time breakPoint) {
		long diff = (Math.abs(checkOut.getTime() - checkIn.getTime()));
		int hour=(int)diff/(3600*1000);
		long timeDiff = breakPoint.getTime()-checkIn.getTime();
		if(hour>=8){
			return "FULL";
		}
		else if((timeDiff>=0)&&(hour>=4)){
			return "FIRST_HALF";
		}
		else if((timeDiff<=0)&&(hour>=4)){
			return "SECOND_HALF";
		}
		return "ABSENT";
	}

}
