package gov.dacfw.kcc.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CallData {
	private int call_id;
	private String location;
	private String crop;
	private String Annotation;
	private String date;
	
	public CallData(){		
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCrop() {
		return crop;
	}

	public void setCrop(String crop) {
		this.crop = crop;
	}

	public String getAnnotation() {
		return Annotation;
	}

	public void setAnnotation(String annotation) {
		Annotation = annotation;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getCall_id() {
		return call_id;
	}

	public void setCall_id(int call_id) {
		this.call_id = call_id;
	}

	
	
}
