package br.ufrj.ppgi.greco.kettle.sparqlupdate;

public class Response {

	private int statusCode;
	private String statusMessageString;

	public Response(int statusCode, String statusMessageString) {
		super();
		this.statusCode = statusCode;
		this.statusMessageString = statusMessageString;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessageString() {
		return statusMessageString;
	}

	public void setStatusMessageString(String statusMessageString) {
		this.statusMessageString = statusMessageString;
	}
}
