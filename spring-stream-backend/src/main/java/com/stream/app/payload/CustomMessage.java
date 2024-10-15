package com.stream.app.payload;

public class CustomMessage {

	public CustomMessage() {
		super();
	}

	public CustomMessage(String message, boolean success) {
		super();
		this.message = message;
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	private String message;
	private boolean success = false;
}
