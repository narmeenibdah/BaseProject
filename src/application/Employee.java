package application;

public class Employee {

	private int employeeId;
	private String name;
	private String role;
	private double salary;

	public Employee(int employeeId, String name, String role, double salary) {
		this.employeeId = employeeId;
		this.name = name;
		this.role = role;
		this.salary = salary;
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
}
