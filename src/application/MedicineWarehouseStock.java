package application;

public class MedicineWarehouseStock {

	private int medicineId;
	private String medicineName;

	private int warehouseId;
	private String warehouseName;

	private int availableQuantity;

	public MedicineWarehouseStock(int medicineId, String medicineName, int warehouseId, String warehouseName,
			int availableQuantity) {
		super();
		this.medicineId = medicineId;
		this.medicineName = medicineName;
		this.warehouseId = warehouseId;
		this.warehouseName = warehouseName;
		this.availableQuantity = availableQuantity;
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

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public int getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

}
