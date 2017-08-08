package com.esko.Utils.DepamentTimeStrategy;

import java.sql.Time;

public interface Strategy {
	public String getTimeDuration (Time checkIn,Time checkOut,Time breakPoint);
}
