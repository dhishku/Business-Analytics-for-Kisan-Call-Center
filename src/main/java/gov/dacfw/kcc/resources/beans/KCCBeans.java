package gov.dacfw.kcc.resources.beans;



import javax.ws.rs.QueryParam;

public class KCCBeans {
	private @QueryParam("location")  String location;
	private @QueryParam("crop") String crop;
	private @QueryParam("annotation") String annotation;
	private @QueryParam("beginDate") String beginDate;
	private @QueryParam("endDate") String endDate;
	
	public KCCBeans(){
		
	}
	
	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
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

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	
}
