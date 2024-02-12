package ca.skipatrol.cnswap.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

import ca.skipatrol.cnswap.jpa.entity.Order;

public class CNSwapUtil {
	
	private static final SimpleDateFormat dateTimeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	

	public static String bytesToMeg(long size) {
		 String hrSize = null;

		    double b = size;
		    double k = size/1024.0;
		    double m = ((size/1024.0)/1024.0);
		    double g = (((size/1024.0)/1024.0)/1024.0);
		    double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

		    DecimalFormat dec = new DecimalFormat("0.00");

		    if ( t>1 ) {
		        hrSize = dec.format(t).concat(" TB");
		    } else if ( g>1 ) {
		        hrSize = dec.format(g).concat(" GB");
		    } else if ( m>1 ) {
		        hrSize = dec.format(m).concat(" MB");
		    } else if ( k>1 ) {
		        hrSize = dec.format(k).concat(" KB");
		    } else {
		        hrSize = dec.format(b).concat(" Bytes");
		    }

		    return hrSize;
	}
	
		
	public static final String java8_hash() {
		    int leftLimit = 97; // letter 'a'
		    int rightLimit = 122; // letter 'z'
		    int targetStringLength = 6;
		    Random random = new Random();

		    String generatedString = random.ints(leftLimit, rightLimit + 1)
		      .limit(targetStringLength)
		      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		      .toString();

		    return(generatedString);
		}
		
		
		public static final Timestamp getCurrentTimestamp() { 
			return (new Timestamp(System.currentTimeMillis()));
		}

		public static boolean isOrderStatus(OrderStatus orderStatus,Order order) {
	    	if( order.getStatus()!= null && orderStatus.compareTo(OrderStatus.valueOf(order.getStatus()))==0) {
	    		return true;
	    	}
	    	return false;
		}
		
		public static boolean isOrderCompleted(Order order) {
			// VALIDATE that the order is not COMPLETED already
	    	if( order.getStatus()!= null && OrderStatus.COMPLETED.compareTo(OrderStatus.valueOf(order.getStatus()))==0) {
	    		return true;
	    	}
	    	return false;
		}
		
		public static boolean isParsableInteger(String aString) {
			try {
				Integer.parseInt(aString);
				return true;
			}
			catch (NumberFormatException nfe) {
				return false;
			}
		}
	
		  
		public static String getCopiesToPrintInHTMLAttribute() {
			Integer printCopies = Integer.parseInt(SwapyAppConfig.getProperty(SwapyAppConfig.PROPERTY_ORDER_PRINT_COPIES));
			StringBuffer rv = new StringBuffer(5); 
			for ( ; printCopies > 0;printCopies--) {
				rv.append("window.print();");
			}
			return rv.toString();
		}
		
		
}
