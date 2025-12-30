package application;

public class Batch {
	private int batchId;
	private String batchNumber;
	private String expiryDate;
	private int quantity;
	private double cost;
	private String medicineName;
	private String warehouseName;

	public Batch(int batchId, String batchNumber, String expiryDate, int quantity, double cost, String medicineName,
			String warehouseName) {
		this.batchId = batchId;
		this.batchNumber = batchNumber;
		this.expiryDate = expiryDate;
		this.quantity = quantity;
		this.cost = cost;
		this.medicineName = medicineName;
		this.warehouseName = warehouseName;
	}

	public int getBatchId() {
		return batchId;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getCost() {
		return cost;
	}

	public String getMedicineName() {
		return medicineName;
	}

	public String getWarehouseName() {
		return warehouseName;
	}
}
