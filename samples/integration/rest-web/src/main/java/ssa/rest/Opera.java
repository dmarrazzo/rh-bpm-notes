package ssa.rest;

import java.io.Serializable;

public class Opera implements Serializable{

	private static final long serialVersionUID = -7497396560421176025L;
	private long id;
	private String author;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
}
