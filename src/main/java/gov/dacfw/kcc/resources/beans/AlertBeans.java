package gov.dacfw.kcc.resources.beans;

import javax.ws.rs.QueryParam;

public class AlertBeans {
	private @QueryParam("window") int alertWindowLength;
	private @QueryParam("currentDate") String currentDate;
	
	public AlertBeans(){
		
	}
	

	public String getCurrentDate() {
		return currentDate;
	}


	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}


	public int getAlertWindowLength() {
		return alertWindowLength;
	}

	public void setAlertWindowLength(int alertWindowLength) {
		this.alertWindowLength = alertWindowLength;
	}
	

}
