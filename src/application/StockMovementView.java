package application;

public class StockMovementView {

	private String movementType; // IN / OUT
	private String movementDate;
	private int referenceId; // PO_ID أو Sale_ID
	private int quantity;

	public StockMovementView(String movementType, String movementDate, int referenceId, int quantity) {
		this.movementType = movementType;
		this.movementDate = movementDate;
		this.referenceId = referenceId;
		this.quantity = quantity;
	}

	public String getMovementType() {
		return movementType;
	}

	public String getMovementDate() {
		return movementDate;
	}

	public int getReferenceId() {
		return referenceId;
	}

	public int getQuantity() {
		return quantity;
	}
}
