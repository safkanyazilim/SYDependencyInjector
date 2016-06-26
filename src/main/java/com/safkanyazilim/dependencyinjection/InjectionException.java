package com.safkanyazilim.dependencyinjection;

public class InjectionException extends RuntimeException {
	private static final long serialVersionUID = -8116358973899708482L;

	public InjectionException() {
	}

	public InjectionException(String detailMessage) {
		super(detailMessage);
	}

	public InjectionException(Throwable throwable) {
		super(throwable);
	}

	public InjectionException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
