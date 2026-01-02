package application;

public class Sale {

	private int saleId;
	private String saleDate;
	private double totalAmount;
	private int branchId;
	private int employeeId;
	private int customerId;

	public Sale(int saleId, String saleDate, double totalAmount, int branchId, int employeeId, int customerId) {
		super();
		this.saleId = saleId;
		this.saleDate = saleDate;
		this.totalAmount = totalAmount;
		this.branchId = branchId;
		this.employeeId = employeeId;
		this.customerId = customerId;
	}

	public int getSaleId() {
		return saleId;
	}

	public void setSaleId(int saleId) {
		this.saleId = saleId;
	}

	public String getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

}
