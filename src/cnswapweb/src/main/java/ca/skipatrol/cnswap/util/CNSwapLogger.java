package ca.skipatrol.cnswap.util;

import org.slf4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;

public class CNSwapLogger {

	
	private static String getSessionId() {
		return RequestContextHolder.currentRequestAttributes().getSessionId();
	}
	
	public static void logEntry(Logger logger, String methodName, Object...params ) {
		StringBuilder str = new StringBuilder("["+getSessionId()+"]"+"-> "+"["+methodName+"] params=");
		for (Object param:params) {
			if (param != null) {
				str.append("'"+param.toString()+"'");
			}
			else {
				str.append("'null'");
			}
			
		}
		logger.debug(str.toString());
	}
	
	public static void logExit(Logger logger, String methodName, Object rv ) {
		StringBuilder str = new StringBuilder("["+getSessionId()+"]"+"<- ["+methodName+"] return=");
		if (rv != null) {
			str.append("("+rv.toString()+")");
		}
		else {
			str.append("(null)");
		}
		logger.debug(str.toString());
	}

	public static void trace(Logger logger, String string) {
		logger.trace("["+getSessionId()+"]"+string);
	}
	
	

}
