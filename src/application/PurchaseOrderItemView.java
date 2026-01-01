package application;

public class PurchaseOrderItemView {

	private int poId;
	private int medicineId;
	private String medicineName;
	private int quantity;
	private double unitPrice;

	public PurchaseOrderItemView(int poId, int medicineId, String medicineName, int quantity, double unitPrice) {
		this.poId = poId;
		this.medicineId = medicineId;
		this.medicineName = medicineName;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	public int getPoId() {
		return poId;
	}

	public int getMedicineId() {
		return medicineId;
	}

	public String getMedicineName() {
		return medicineName;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getUnitPrice() {
		return unitPrice;
	}
}
