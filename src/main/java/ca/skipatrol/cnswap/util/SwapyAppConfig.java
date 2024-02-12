package ca.skipatrol.cnswap.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

public class SwapyAppConfig {

	private static SwapyAppConfig _instance = null;
	
	private HashMap<String, String> properties;
	
	public static String SWAPY_CONFIG = "SWAPY_CONFIG";
	
	public static String PROPERTY_ORDER_PRINT_COPIES = "PROPERTY_ORDER_PRINT_COPIES"; 
	private static String ORDER_PRINT_COPIES_DEFAULT = "2"; 
			
	private SwapyAppConfig() {
		 properties = new HashMap<String, String>();
		 properties.put(PROPERTY_ORDER_PRINT_COPIES, ORDER_PRINT_COPIES_DEFAULT);
	}
	
	public static SwapyAppConfig getInstance() {
		if (_instance == null) {
			_instance = new SwapyAppConfig();
		}
		return _instance;
	}
	
	public static String getProperty(String propertyName) {
		return getInstance().properties.get(propertyName);
	}
	
}
