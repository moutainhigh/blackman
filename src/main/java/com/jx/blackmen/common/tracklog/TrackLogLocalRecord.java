package com.jx.blackmen.common.tracklog;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TrackLogLocalRecord {
	
	public static Logger log =null;// LogManager.getLogger("D");
	
	public TrackLogLocalRecord(){}
	public TrackLogLocalRecord(String fileName){
		log = LogManager.getLogger(fileName);
	}
	
	public static void log(String logStr){
		log.info(logStr);
	}
	
	public static void main(String[] args) {
//		PropertyConfigurator.configure("log4j.properties");
//		TrackLogLocalRecord.log("11111");
	}
}
