package gov.dacfw.kcc.model;

public class CallSummaryData {
	private int count;
	private String location;
	private String crop;
	private String annotation;
	private int year;
	
	public CallSummaryData(){
		
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	public String toString(){
		return "location: "+location+" crop: "+crop+" annotation: "+annotation+" year: "+year+" count:"+count;
	}
	
}
