package ssa;

import java.io.Serializable;

public class Test implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String msg;

	public Test(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
