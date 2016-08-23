package insurance.cep.model;

public class Customer {
	private String id;
	private CustomerLevel level;
	private String plateId;

	public Customer(String id, CustomerLevel level, String plateId) {
		this.id = id;
		this.level = level;
		this.plateId = plateId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CustomerLevel getLevel() {
		return level;
	}

	public void setLevel(CustomerLevel level) {
		this.level = level;
	}

	public String getPlateId() {
		return plateId;
	}

	public void setPlateId(String plateId) {
		this.plateId = plateId;
	}

}
