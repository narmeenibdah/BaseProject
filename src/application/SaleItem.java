package application;

public class SaleItem {

	private int saleId;
	private int medicineId;
	private String medicineName;
	private int batchId;
	private int quantity;
	private double unitPrice;

	public SaleItem(int saleId, int medicineId, String medicineName, int batchId, int quantity, double unitPrice) {
		super();
		this.saleId = saleId;
		this.medicineId = medicineId;
		this.medicineName = medicineName;
		this.batchId = batchId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	public int getSaleId() {
		return saleId;
	}

	public void setSaleId(int saleId) {
		this.saleId = saleId;
	}

	public int getMedicineId() {
		return medicineId;
	}

	public void setMedicineId(int medicineId) {
		this.medicineId = medicineId;
	}

	public String getMedicineName() {
		return medicineName;
	}

	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}

	public int getBatchId() {
		return batchId;
	}

	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

}
