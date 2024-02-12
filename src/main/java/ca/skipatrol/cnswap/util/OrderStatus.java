package ca.skipatrol.cnswap.util;

public enum OrderStatus {
	 COMPLETED("COMP"),
	 PENDING("PEND"),
	 REFUNDED("REFU");
	 
	private String label;
	
	private OrderStatus(String label) {
		this.label=label;
	}
	
	public static boolean isCompleted(String status) {
		if (COMPLETED.toString().compareToIgnoreCase(status)==0)
			return true;
		return false;
	}
	
	public static boolean isPending(String status) {
		if (PENDING.toString().compareToIgnoreCase(status)==0)
			return true;
		return false;
	}
	
	public static boolean isRefunded(String status) {
		if (REFUNDED.toString().compareToIgnoreCase(status)==0)
			return true;
		return false;
	}
	
}
