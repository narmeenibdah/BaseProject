package application;

public class Supplier {

	private int supplierId;
	private String name;
	private String phone;
	private String email;

	public Supplier(int supplierId, String name, String phone, String email) {
		this.supplierId = supplierId;
		this.name = name;
		this.phone = phone;
		this.email = email;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
