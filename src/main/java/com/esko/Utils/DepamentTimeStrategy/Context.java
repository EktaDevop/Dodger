package com.esko.Utils.DepamentTimeStrategy;

import java.sql.Time;


public class Context {
	 private Strategy strategy;

	   public Context(Strategy strategy){
	      this.strategy = strategy;
	   }

	   public String executeStrategy(Time checkIn,Time checkOut,Time breakPoint){
	      return strategy.getTimeDuration(checkIn, checkOut, breakPoint);
	   }
}
