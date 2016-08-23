package insurance.cep.model;

public enum CustomerLevel {
	BASIC("basic"), SILVER("silver"), GOLDEN("golden");

	private String level;

	CustomerLevel(String level) {
		this.setLevel(level);
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
