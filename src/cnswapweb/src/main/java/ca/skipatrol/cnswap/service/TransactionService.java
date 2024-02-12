package ca.skipatrol.cnswap.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.skipatrol.cnswap.exception.CNSWAPException;
import ca.skipatrol.cnswap.jpa.entity.Catentry;
import ca.skipatrol.cnswap.jpa.entity.CatentryRepository;
import ca.skipatrol.cnswap.jpa.entity.DiscountsRepository;
import ca.skipatrol.cnswap.jpa.entity.Inventory;
import ca.skipatrol.cnswap.jpa.entity.InventoryRepository;
import ca.skipatrol.cnswap.jpa.entity.Order;
import ca.skipatrol.cnswap.jpa.entity.OrderItems;
import ca.skipatrol.cnswap.jpa.entity.OrderItemsRepository;
import ca.skipatrol.cnswap.jpa.entity.OrdersRepository;
import ca.skipatrol.cnswap.jpa.entity.PaymentRepository;
import ca.skipatrol.cnswap.jpa.entity.TaxEntry;
import ca.skipatrol.cnswap.jpa.entity.TaxInstruction;
import ca.skipatrol.cnswap.jpa.entity.TaxInstructionsRepository;
import ca.skipatrol.cnswap.jpa.entity.User;
import ca.skipatrol.cnswap.jpa.entity.UsersRepository;
import ca.skipatrol.cnswap.util.CNSwapLogger;
import ca.skipatrol.cnswap.util.CNSwapUtil;
import ca.skipatrol.cnswap.util.InventoryStatus;
import ca.skipatrol.cnswap.util.OrderStatus;
import ca.skipatrol.cnswap.util.SystemMessages;

@Service
public class TransactionService {
	
	Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
			
	@Autowired
	OrdersRepository ordersRepo;
	
	@Autowired
	OrderItemsRepository orderItemsRepo;
	
	@Autowired
	DiscountsRepository discountRepo;
	
	@Autowired
	PaymentRepository paymentRepo;
	
	@Autowired
	CatentryRepository catEntryRepo;
	
	@Autowired
	InventoryRepository invRepo;
	
	@Autowired
	UsersRepository userRepo;
	
	@Autowired
	TaxInstructionsRepository taxInstructionRepo;
	
	
	
	public TransactionService() {

	}
	

	
	public Order newTransaction(User username) {
		Order anOrder = new Order();
		anOrder.setTransTime(CNSwapUtil.getCurrentTimestamp());
		anOrder.setTransUsername(username);
		return anOrder;
	}
	
	
	private boolean isStatusAvailable(Catentry anItem) {
  
		switch(InventoryStatus.valueOf(anItem.getInventory().getStatus())) {
//			case CHECKOUT: case PENDING_CHECKIN: case SOLD:
//				return false;
			case AVAILABLE:
				return true;
		}
		
		return false;
	}
	
	public boolean isItemPurchasable(Catentry anItem) {
		// is item.status = available		
		if (!isStatusAvailable(anItem)) {
			return false;
		};
		
		// is there inventory
		if (anItem.getInventory().getQuantity() >=1) {
			return true;
		}
			
		System.out.println(this.getClass().getCanonicalName() + SystemMessages.CNSWAP_ERR005_MSG + "Item="+anItem);
		return false;
	}

	
	
	public boolean addOrderItems(Integer orderId, String barcode) {
		boolean rv =false;
		Catentry anItem = getCatentry(barcode);
		
		
		// Did we find an Item with that Barcode?
		if (anItem == null) {
			rv =false;
			return rv;
		}

		// is item purchasable?
		if (!isItemPurchasable(anItem)) {
			rv =false;
			return rv;	
		}
			
		// if there is only Item.quantity =1 and is already in the order.. do not add another 
		if (!isOrderEmpty(orderId) && !canOrderAcceptItemQuantity(orderId,anItem)) {
			return false;
		}
			
		rv = calculateAndStore(orderId,anItem);
		
		return rv;
	}
	
	
	private boolean isOrderEmpty(Integer orderId) {
		Order order = getOrder(orderId);
		List<OrderItems> orderItems = order.getOrderItems();
		if (orderItems== null || orderItems.isEmpty()) {
			return true;
		}
		return false;
	}

	private boolean isOrderEmpty(Order order) {
		List<OrderItems> orderItems = order.getOrderItems();
		if (orderItems== null || orderItems.isEmpty()) {
			return true;
		}
		return false;
	}

	
	
	public boolean canOrderAcceptItemQuantity(Integer orderId,Catentry item) throws CNSWAPException{
		Order order = getOrder(orderId);
		List<OrderItems> orderItems = order.getOrderItems();
		
		Iterator<OrderItems> oItemsIter = orderItems.iterator(); 
		int orderQuantity=0;
		while (oItemsIter.hasNext()) {			
			if (item.getId().equalsIgnoreCase(oItemsIter.next().getCatentry_id())) {
				orderQuantity++;
			}
		}
		if (orderQuantity >= item.getInventory().getQuantity()) {
			return false;
		}

		return true;
	}
	
	
	public boolean calculateAndStore(Integer orderId,Catentry item) throws CNSWAPException{
		

		Order order = ordersRepo.findOneById(orderId);
		
		OrderItems aCartItem = new OrderItems();
		aCartItem.setOrders_id(order);
		aCartItem.setCatentry_id(item.getId());
		aCartItem.setName(item.getName());
		aCartItem.setDescription(item.getDescription());
		aCartItem.setSize(item.getSize());
		aCartItem.setType(item.getType().getCatentryTypeName());
		aCartItem.setUnitPrice(new BigDecimal(item.getPrice()));
		

//		List<OrderItems> dbOrderItems =order.getOrderItems();
//		if (dbOrderItems != null && dbOrderItems.isEmpty()) {
//			dbOrderItems = new ArrayList();
//			
//		}
		
		order.getOrderItems().add(aCartItem);
				
//		// Save the cartItem
//		aCartItem = orderItemsRepo.save(aCartItem);
		
		// Recalculate the total
		calculate(order);

		return true;
	}
	
	
	public Order calculate(Integer orderId) {
		return calculate(ordersRepo.findOneById(orderId));
	}
	
	public Order calculate(String orderId) {
		return calculate(ordersRepo.findOneById(Integer.getInteger(orderId)));
		
	}
	
	public Order calculate(Order order) {
		final String METHODNAME = "calculate";
		CNSwapLogger.logEntry(LOGGER, METHODNAME, order);
		
		BigDecimal subOrderTotal= new BigDecimal(0);
		
		if (order.getOrderItems() == null){
			CNSwapLogger.trace(LOGGER,"No Order Items");
			return order;
		}
		
		CNSwapLogger.trace(LOGGER,"Add all Order Items");
		// Add all the items first
		for (OrderItems inCartItem : order.getOrderItems()) {
			subOrderTotal = subOrderTotal.add(inCartItem.getUnitPrice());
		}
		order.setSubTotal(subOrderTotal);
		
		
		CNSwapLogger.trace(LOGGER,"Saving order="+order);
		order = ordersRepo.save(order);
		
		addTax(order);		
		
		CNSwapLogger.trace(LOGGER,"Saving order="+order);
		order = ordersRepo.save(order);
		
		CNSwapLogger.logExit(LOGGER, METHODNAME, order);
		return order;
		
	}
	
	
	public BigDecimal addTax(Order order) {
		final String METHODNAME = "addTax";
		CNSwapLogger.logEntry(LOGGER, METHODNAME, order);
		
		clearAllTax(order);
		
		
		BigDecimal subOrderTotal = order.getSubTotal();
		BigDecimal orderTotalTax = new BigDecimal(0);
		// Calculate the tax and add it to the orderTotal
		CNSwapLogger.trace(LOGGER,"Calculate tax");
		TaxEntry[] taxEntryArray = getAllVendorsTaxEntries(order);
		if (taxEntryArray.length > 0) {
			CNSwapLogger.trace(LOGGER,"taxEntryArray.length="+taxEntryArray.length);
			
			if (subOrderTotal.compareTo(BigDecimal.ZERO) > 0) {
				// this handles the NEW TRANSACTION calculations
				CNSwapLogger.trace(LOGGER,"subOrderTotal is possitve");

				for (TaxEntry taxEntry :taxEntryArray) {
		
					BigDecimal taxEntryPercentage = taxEntry.getPercentage();
					BigDecimal totalTax = subOrderTotal.multiply(taxEntryPercentage.setScale(2, RoundingMode.HALF_UP));
					TaxInstruction ti = new TaxInstruction();
					ti.setName(taxEntry.getName());
					ti.setOrdersId(order);
					ti.setTotal(totalTax);
					taxInstructionRepo.save(ti);
					
					addToOrder(ti,order);
					
					orderTotalTax = orderTotalTax.add(totalTax);
				}
			}
			else { 
				// this handles the RETURN calculations
				// We need to negate things here as this is a return
				CNSwapLogger.trace(LOGGER,"subOrderTotal is negative");
				BigDecimal returnedSubOrderTotal = subOrderTotal.negate();
				CNSwapLogger.trace(LOGGER,"returnedSubOrderTotal="+returnedSubOrderTotal);
				for (TaxEntry taxEntry :taxEntryArray) {
					
					BigDecimal taxEntryPercentage = taxEntry.getPercentage();
					BigDecimal totalTax = returnedSubOrderTotal.multiply(taxEntryPercentage.setScale(2, RoundingMode.HALF_UP));
					CNSwapLogger.trace(LOGGER,"totalTax="+totalTax);
					// We need to negate things here as this is a returned
					totalTax = totalTax.negate();
					CNSwapLogger.trace(LOGGER,"Negated totalTax="+totalTax);
					TaxInstruction ti = new TaxInstruction();
					ti.setName(taxEntry.getName());
					ti.setOrdersId(order);
					ti.setTotal(totalTax);
					taxInstructionRepo.save(ti);
					
					addToOrder(ti,order);
					
					orderTotalTax = orderTotalTax.add(totalTax);
				}

			}
			
		}
		else {
			CNSwapLogger.trace(LOGGER,"No tax found for any .Catentry of the .Vendors of this .VendorType. taxEntryArray.length="+taxEntryArray.length);
		}
		
		
		subOrderTotal = subOrderTotal.add(orderTotalTax);
		subOrderTotal = subOrderTotal.setScale(2, RoundingMode.HALF_UP);
		order.setTotal(subOrderTotal);
		
		CNSwapLogger.trace(LOGGER,"Saving order="+order);
		order = ordersRepo.save(order);
		
		CNSwapLogger.logExit(LOGGER, METHODNAME, "orderTotalTax="+orderTotalTax);
		return orderTotalTax;
	}

	
	private void addToOrder(TaxInstruction ti, Order order) {
		List<TaxInstruction> tiList = order.getTaxInstructions();
		// ArrayList<TaxInstruction> tiInOrder = new ArrayList<TaxInstruction>(0);
		if (tiList == null) {
			tiList = new ArrayList<TaxInstruction>(0);
		}
		tiList.add(ti);
		order.setTaxInstructions(tiList);
		ordersRepo.save(order);
	}



	private void clearAllTax(Order order) {
		final String METHODNAME = "clearAllTax";
		CNSwapLogger.logEntry(LOGGER, METHODNAME, "order="+order);
		
		List<TaxInstruction> deletedTax = taxInstructionRepo.deleteAllByOrdersId(order);
		CNSwapLogger.trace(LOGGER,"List of taxInstructions deleted="+deletedTax);
		
		CNSwapLogger.logExit(LOGGER, METHODNAME, "");
	}



	private TaxEntry[] getAllVendorsTaxEntries(Order order) {
		final String METHODNAME = "getAllVendorsTaxEntries";
		CNSwapLogger.logEntry(LOGGER, METHODNAME, order);
		
		Set<TaxEntry> taxEntryList = new HashSet<TaxEntry>();
		Iterator <OrderItems> orderItemList = order.getOrderItems().iterator();
		while (orderItemList.hasNext()) {
			OrderItems oi = orderItemList.next();
			  Optional<Catentry> cEntry = catEntryRepo.findById(oi.getCatentry_id());
			  if (cEntry.isPresent()) {
				  List<TaxEntry> taxEntryListForOrderItem = cEntry.get().getVendor().getVendortype().getTaxEntry();
				  for (TaxEntry oiTaxEntry : taxEntryListForOrderItem ) {
					  taxEntryList.add(oiTaxEntry);
				  }  
			  }
		}
		
		
		return taxEntryList.toArray(new TaxEntry[0]);
	}



	public Order save(Order order) {
		return ordersRepo.save(order);
	}
	
	
	/**
	 * Given an order refres the object from the DB
	 * @param order
	 * @return
	 */
	public Order refresh(Order order) {
		return ordersRepo.findOneById(order.getId());
	}
	
	
	public Order getOrder(String orderId) {
		return getOrder(Integer.parseInt(orderId));
	}
	
	public Order getOrder(Integer orderId) {
		final String METHODNAME = "getOrder";
		CNSwapLogger.logEntry(LOGGER,METHODNAME,orderId);
		
		Order rv = ordersRepo.findOneById(orderId);
		
		CNSwapLogger.logExit(LOGGER,METHODNAME,rv);
		return rv;
	}

	public boolean removeOrderItem(Integer orderId, String orderItemId) {
		boolean rv = false;
		Optional<OrderItems> oiOptional = orderItemsRepo.findById(Integer.valueOf(orderItemId));
		OrderItems orderItemToRemove;
		if (oiOptional.isPresent()) {
			orderItemToRemove = oiOptional.get();
			orderItemsRepo.delete(orderItemToRemove);
			rv = true;
		}

		
		calculate(orderId);
		return rv;
		
	}

	public boolean cancelOrder(Integer orderId) {
		boolean rv = false;
		Order order = getOrder(orderId);
		List<OrderItems> orderItems = order.getOrderItems();
		for (OrderItems anItem :orderItems) {
			orderItemsRepo.delete(anItem);
		}
		ordersRepo.deleteById(orderId);
		rv = true;
		
		return rv;
		
	}


	public Catentry getCatentry(String barcode) {
		Optional<Catentry> itemOptional = catEntryRepo.findById(barcode);
		if (!itemOptional.isEmpty()) {
			return catEntryRepo.findById(barcode).get();
		}
		return null;
	}



	public String getBarcodeStatus(String barcode) {
		Catentry anItem = getCatentry(barcode);
		if (anItem == null) {
			return "NOT_FOUND";
		}

		return anItem.getInventory().getStatus();
	}



	public void processInventory(Order order) {
		List<OrderItems> orderItems = order.getOrderItems();
		Iterator<OrderItems> iterOrderItems = orderItems.iterator();
		while(iterOrderItems.hasNext()) {
			invRepo.findOneByCatentry_id(iterOrderItems.next().getCatentry_id());
		}
		
	}


	public Integer getBarcodeQuantity(String barcode) {
		Catentry anItem = getCatentry(barcode);
		if (anItem == null) {
			return 0;
		}

		return anItem.getInventory().getQuantity();
	}

	private boolean isEmptyOrder(Order order) {
		if (order == null)
			return true;
		
		if (order.getId() == null)
			return true;
		return false;
	}

	private List<Catentry> getCatentryList(Order order){
		List<OrderItems> orderItemsList = order.getOrderItems();
		List<String> orderItemIdList = new ArrayList<String>();
		for (OrderItems anItem: orderItemsList) {
			orderItemIdList.add(anItem.getCatentry_id());
		}
		Iterable<Catentry> catentryIterable = catEntryRepo.findAllById(orderItemIdList);
		List<Catentry> catentryList = new ArrayList();
		for (Catentry aCatentry:catentryIterable) {
			catentryList.add(aCatentry);
		}
		return catentryList;
	}
	
	private List<Inventory> getInventoryList(Order order){
		List<OrderItems> orderItemsList = order.getOrderItems();
		List<String> orderItemIdList = new ArrayList<String>();
		for (OrderItems anItem: orderItemsList) {
			orderItemIdList.add(anItem.getCatentry_id());
		}
		List<Inventory> invList = invRepo.findAllByCatentry_idIn(orderItemIdList);
		return invList;
	}
	
	
	public void completeRefund(Order order,String originalOrderId, String authToken, String comment) {
		final String METHODNAME="completeRefund";
		CNSwapLogger.logEntry(LOGGER, METHODNAME, order,originalOrderId,authToken,comment);
		
		CNSwapLogger.trace(LOGGER,"isOrderEmpty?");
		if (isEmptyOrder(order)) {
			return; 
		}
		
		CNSwapLogger.trace(LOGGER,"Get inventory from order="+order);
		List<Inventory> orderItemInvList = getInventoryList(order); 
		
		
		for (Inventory anItemInv :orderItemInvList) {
			CNSwapLogger.trace(LOGGER,"anItemInv="+anItemInv);
			anItemInv.setQuantity(anItemInv.getQuantity()+1);
			anItemInv.setStatus(InventoryStatus.AVAILABLE.toString());;
			CNSwapLogger.trace(LOGGER,"anItemInv="+anItemInv);
			invRepo.save(anItemInv);
			CNSwapLogger.trace(LOGGER,"anItemInv saved");
		}

		
		// SET A COMMENT ON THE ORIGINAL_ORDER to point to the REFUNDED ID
		CNSwapLogger.trace(LOGGER,"Setting originalOrder comment and status");
		Order originalOrder = getOrder(originalOrderId);
		originalOrder.setComment(String.format(Locale.US, "REFUNDED BY REFU_ORDERID=%s",order.getId()));
		originalOrder.setStatus(OrderStatus.REFUNDED.toString());
		CNSwapLogger.trace(LOGGER,"Saving originalOrder comment");
		ordersRepo.save(originalOrder);
		
		// SET A COMMENT ON THE REFUNDED_ORDER to point to the ORIGINAL ID
		CNSwapLogger.trace(LOGGER,"Setting order comment and status");
		order.setComment(String.format(Locale.US, "REFUND FROM ORIG_ORDERID=%s,AUTH_TOKEN=%s,COMMENT=%s",originalOrderId,authToken,comment));
		order.setStatus(OrderStatus.REFUNDED.toString());
		CNSwapLogger.trace(LOGGER,"Saving order comment");
		ordersRepo.save(order);
		CNSwapLogger.logEntry(LOGGER, METHODNAME,"");
		
	}

	
	public void completeOrder(Order order) {
		if (isEmptyOrder(order)) {
			return; 
		}
		//List<Catentry> itemsListInOrder = getCatentryList(order);
		List<Inventory> orderItemInvList = getInventoryList(order); 
		
		for (Inventory anItemInv :orderItemInvList) {
			anItemInv.setQuantity(anItemInv.getQuantity()-1);
			if (anItemInv.getQuantity() ==0) {
				anItemInv.setStatus(InventoryStatus.SOLD.toString());
			}
			invRepo.save(anItemInv);
		}
		
		order.setStatus(OrderStatus.COMPLETED.toString());
		ordersRepo.save(order);
		
	}



	public Order refundOrder(String orderId, String refundApprovalUsername) {
		final String METHODNAME = "refundOrder";
		CNSwapLogger.logEntry(LOGGER,METHODNAME,orderId,refundApprovalUsername);
		
		Order orderOriginal = ordersRepo.findOneById(Integer.parseInt(orderId));
		CNSwapLogger.trace(LOGGER,"orderOriginal="+orderOriginal.toString());
		
		final Order orderRefund = new Order();
		
		CNSwapLogger.trace(LOGGER,"orderRefund="+orderRefund.getId());
		
    	// Set the username on the order
		User refundAuthUser = userRepo.findByUsername(refundApprovalUsername);
		orderRefund.setTransUsername(refundAuthUser);
		orderRefund.setTransTime(CNSwapUtil.getCurrentTimestamp());
		orderRefund.setStatus(OrderStatus.PENDING.toString());
		orderRefund.setOrderItems(new ArrayList<OrderItems>());
		CNSwapLogger.trace(LOGGER,"orderRefund="+orderRefund.toString());
		save(orderRefund);
		CNSwapLogger.trace(LOGGER,"orderRefund saved");
    	
    	List<OrderItems> orderItemsOriginalList = orderOriginal.getOrderItems();
    	orderItemsOriginalList.forEach((itemOriginal) -> {
    			OrderItems refundedItem = new OrderItems();
    			CNSwapLogger.trace(LOGGER,"ForEach orderItem");
    			refundedItem.setCatentry_id(itemOriginal.getCatentry_id());
    			refundedItem.setName(itemOriginal.getName());
    			refundedItem.setSize(itemOriginal.getSize());
    			refundedItem.setType(itemOriginal.getType());
    			refundedItem.setUnitPrice(itemOriginal.getUnitPrice().negate());
    			refundedItem.setOrders_id(orderRefund);
    			CNSwapLogger.trace(LOGGER,"refundedItem="+refundedItem);
    			
    			String comment = String.format(Locale.US, "REFUNDED. APPROVED BY: %s ON: %s ORIGINAL ORDERITEMID: %s", refundApprovalUsername,orderRefund.getTransTime(),refundedItem.getId());
    			refundedItem.setComment(comment);
    			
    			comment = String.format(Locale.US, "REFUNDED. APPROVED BY: %s ON: %s ORIGINAL ORDERID: %s", refundApprovalUsername,orderRefund.getTransTime(),orderId);
    			orderRefund.getOrderItems().add(refundedItem);
    			orderRefund.setComment(comment);
    			
    			save(orderRefund);
    			CNSwapLogger.trace(LOGGER,"orderRefund saved (2)");
    			}
    	);
    	
    	Order rv = calculate(orderRefund);
    	rv = save(rv);
    	CNSwapLogger.logExit(LOGGER, METHODNAME, rv);
		return rv;    	
	}


	
}