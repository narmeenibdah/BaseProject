package application;

public class Batch {
	private int batchId;
	private String batchNumber;
	private String expiryDate;
	private int quantity;
	private double cost;
	private String medicineName;
	private String warehouseName;
	private int daysRemaining;

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

	public Batch(int batchId, String batchNumber, String expiryDate, int quantity, double cost, String medicineName,
			String warehouseName, int daysRemaining) {
		super();
		this.batchId = batchId;
		this.batchNumber = batchNumber;
		this.expiryDate = expiryDate;
		this.quantity = quantity;
		this.cost = cost;
		this.medicineName = medicineName;
		this.warehouseName = warehouseName;
		this.daysRemaining = daysRemaining;
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

	public int getDaysRemaining() {
		return daysRemaining;
	}

	public void setDaysRemaining(int daysRemaining) {
		this.daysRemaining = daysRemaining;
	}

}
