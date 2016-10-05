package gov.dacfw.kcc.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Alert {
	public static final double LAMBDA_DECAY = 0.80;
	public static final double NORMAL_INTENSITY_CEILING = 1.2;
	public static final double NORMAL_INTENSITY_FLOOR = 0.8;
	public static final int ALERT_WINDOW = 45;
	public static final int MINIMUM_CALLS_FOR_ALERT = 500;
	public static final int MINIMUM_CALLS_FOR_CROP = 3000;
	public static final int MINIMUM_CALLS_FOR_LOCATION = 20000;
	public static final int MINIMUM_CALLS_FOR_ANNOTATION = 3000;
	private boolean isAlert;
	private String crop;
	private String location;
	private String annotation;
	private double intensity;
	private int count;

	public Alert(){
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	public double getIntensity(){
		return intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public boolean isAlert() {
		return isAlert;
	}

	public void setAlert(boolean isAlert) {
		this.isAlert = isAlert;
	}

	public String getCrop() {
		return crop;
	}

	public void setCrop(String crop) {
		this.crop = crop;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

}
