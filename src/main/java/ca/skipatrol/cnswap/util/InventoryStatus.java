package ca.skipatrol.cnswap.util;

public enum InventoryStatus {
	PENDING_CHECKIN("PENDING_CHECKIN"),
	CHECKOUT("CHECKOUT"),
	AVAILABLE("AVAILABLE"),
	SOLD("SOLD");
	 
	private final String value;
	
	private InventoryStatus(final String value) {
		this.value=value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public static boolean isPendingChecking(String status) {
		if (PENDING_CHECKIN.toString().compareToIgnoreCase(status)==0)
			return true;
		return false;
	}

	public static boolean isAvailable(String status) {
		if (AVAILABLE.toString().compareToIgnoreCase(status)==0)
			return true;
		return false;
	}
}
