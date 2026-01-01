package application;

public class PurchaseOrder {
	private int poId;
	private String date;
	private double totalAmount;
	private String status;
	private String supplierName;

	public PurchaseOrder(int poId, String date, double totalAmount, String status, String supplierName) {
		this.poId = poId;
		this.date = date;
		this.totalAmount = totalAmount;
		this.status = status;
		this.supplierName = supplierName;
	}

	public int getPoId() {
		return poId;
	}

	public String getDate() {
		return date;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public String getSupplierName() {
		return supplierName;
	}
}
