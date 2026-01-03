package application;

public class Employee {

	private int employeeId;
	private String name;
	private String role;
	private double salary;
	private String branchName;
	
	
	public Employee(int employeeId, String name, String role, double salary, String branchName) {
		this.employeeId = employeeId;
		this.name = name;
		this.role = role;
		this.salary = salary;
		this.branchName = branchName;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}

	public double getSalary() {
		return salary;
	}

	
	public String getBranchName() {
		return branchName;
	}
	@Override
	public String toString() {
	    return employeeId + " - " + name;
	}

}
