package gov.dacfw.kcc.resources;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import gov.dacfw.kcc.model.Alert;
import gov.dacfw.kcc.resources.beans.AlertBeans;
import gov.dacfw.kcc.services.AlertService;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_HTML)

public class AlertResource {
	private AlertService alertService = new AlertService();
	private static DecimalFormat df2 = new DecimalFormat(".##");
	
	@GET
	public String getAllAlerts(@BeanParam AlertBeans alertBean){
		String result = "<!doctype html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html><head>"
				+ "<style type=\"text/css\">"
				+ "h2{color:#3339ff;}"
				+ "table{border:1px solid black;text-align:center;align:center;}"
				+ "th{font-weight:bold;}"
				+ "tr{border:1px solid black;margin-top:10px;margin-bottom:10px;}"
				+ "td{margin-left:10px;margin-right:10px;}"
				+ "body{font-family:arial;}"
				+ "</style></head><body><h2>Showing All Alerts</h1>"
				+ "<table><tr>"
				+ "<th>Crop</th>"
				+ "<th>Location</th>"
				+ "<th>Annotation</th>"
				+ "<th>Intensity</th>"
				+ "<th>Count</th>"
				+ "</tr>";
		

		
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
				
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
		List<Alert> list = alertService.getAllAlerts(cal, alertWindowLength);
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++){
			result = result + "<tr>"
					+ "<td>"+list.get(i).getCrop()+"</td>"
					+ "<td>"+list.get(i).getLocation()+"</td>"
					+ "<td>"+list.get(i).getAnnotation()+"</td>"
					+ "<td>"+df2.format(list.get(i).getIntensity())+"</td>"
					+ "<td>"+list.get(i).getCount()+"</td>"
					+ "</tr>";
		}
		result += "</table></body></html>";
		return result;
		
		//return alertService.getAllAlerts(cal, alertWindowLength);
	}
	

	@GET
	@Path("/crops")
	public String getAllCropAlerts(@BeanParam AlertBeans alertBean){
		String result = "<!doctype html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html><head>"
				+ "<style type=\"text/css\">"
				+ "h2{color:#3339ff;}"
				+ "table{border:1px solid black;text-align:center;align:center;}"
				+ "th{font-weight:bold;}"
				+ "tr{border:1px solid black;margin-top:10px;margin-bottom:10px;}"
				+ "td{margin-left:10px;margin-right:10px;}"
				+ "body{font-family:arial;}"
				+ "</style></head><body><h2>Showing All Crop Alerts</h1>"
				+ "<table><tr>"
				+ "<th>Crop</th>"
				+ "<th>Location</th>"
				+ "<th>Annotation</th>"
				+ "<th>Intensity</th>"
				+ "<th>Count</th>"
				+ "</tr>";
		
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
						
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
		
		List<Alert> list = alertService.getAllCropAlerts(cal, alertWindowLength);
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++){
			result = result + "<tr>"
					+ "<td>"+list.get(i).getCrop()+"</td>"
					+ "<td>"+list.get(i).getLocation()+"</td>"
					+ "<td>"+list.get(i).getAnnotation()+"</td>"
					+ "<td>"+df2.format(list.get(i).getIntensity())+"</td>"
					+ "<td>"+list.get(i).getCount()+"</td>"
					+ "</tr>";
		}
		result += "</table></body></html>";
		return result;
		
		
	}
	
	@GET
	@Path("/crops/{crop}")
	public String getCropAlert(@PathParam("crop") String crop, 
			@BeanParam AlertBeans alertBean){
		String result = "<!doctype html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html><head>"
				+ "<style type=\"text/css\">"
				+ "h2{color:#3339ff;}"
				+ "table{border:1px solid black;text-align:center;align:center;}"
				+ "th{font-weight:bold;}"
				+ "tr{border:1px solid black;margin-top:10px;margin-bottom:10px;}"
				+ "td{margin-left:10px;margin-right:10px;}"
				+ "body{font-family:arial;}"
				+ "</style></head><body><h2>Showing Alerts For Crop: "+crop
				+ " </h1>"
				+ "<table><tr>"
				+ "<th>Location</th>"
				+ "<th>Annotation</th>"
				+ "<th>Intensity</th>"
				+ "<th>Count</th>"
				+ "</tr>";

		
		
		
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
		//System.out.println("Inside getCropAlert for crop = "+crop);	
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//System.out.println("Inside getCropAlert for current date = "+ df.format(cal.getTime()));
		
		List<Alert> list = alertService.getCropAlert(crop, cal, alertWindowLength);
				Collections.sort(list);
				for (int i = 0; i < list.size(); i++){
					result = result + "<tr>"
							+ "<td>"+list.get(i).getLocation()+"</td>"
							+ "<td>"+list.get(i).getAnnotation()+"</td>"
							+ "<td>"+df2.format(list.get(i).getIntensity())+"</td>"
							+ "<td>"+list.get(i).getCount()+"</td>"
							+ "</tr>";
				}
				result += "</table></body></html>";
		
		return result;
	}
	
	@GET
	@Path("/locations")
	public String getAllLocationAlerts(@BeanParam AlertBeans alertBean){
		String result = "<!doctype html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html><head>"
				+ "<style type=\"text/css\">"
				+ "h2{color:#3339ff;}"
				+ "table{border:1px solid black;text-align:center;align:center;}"
				+ "th{font-weight:bold;}"
				+ "tr{border:1px solid black;margin-top:10px;margin-bottom:10px;}"
				+ "td{margin-left:10px;margin-right:10px;}"
				+ "body{font-family:arial;}"
				+ "</style></head><body><h2>Showing All Location Alerts</h1>"
				+ "<table><tr>"
				+ "<th>Crop</th>"
				+ "<th>Location</th>"
				+ "<th>Annotation</th>"
				+ "<th>Intensity</th>"
				+ "<th>Count</th>"
				+ "</tr>";

		
		
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
		List<Alert> list = alertService.getAllLocationAlerts(cal, alertWindowLength);
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++){
			result = result + "<tr>"
					+ "<td>"+list.get(i).getCrop()+"</td>"
					+ "<td>"+list.get(i).getLocation()+"</td>"
					+ "<td>"+list.get(i).getAnnotation()+"</td>"
					+ "<td>"+df2.format(list.get(i).getIntensity())+"</td>"
					+ "<td>"+list.get(i).getCount()+"</td>"
					+ "</tr>";
		}
		result += "</table></body></html>";
		return result;
				
		//return alertService.getAllLocationAlerts(cal, alertWindowLength);
	}
	
	@GET
	@Path("/locations/{location}")
	//@Produces(MediaType.TEXT_PLAIN)
	public String getLocationAlert(@PathParam("location") String location, 
			@BeanParam AlertBeans alertBean){
		String result = "<!doctype html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html><head>"
				+ "<style type=\"text/css\">"
				+ "h2{color:#3339ff;}"
				+ "table{border:1px solid black;text-align:center;align:center;}"
				+ "th{font-weight:bold;}"
				+ "tr{border:1px solid black;margin-top:10px;margin-bottom:10px;}"
				+ "td{margin-left:10px;margin-right:10px;}"
				+ "body{font-family:arial;}"
				+ "</style></head><body><h2>Showing Alerts For Location: "+location
				+ "</h1>"
				+ "<table><tr>"
				+ "<th>Crop</th>"
				+ "<th>Annotation</th>"
				+ "<th>Intensity</th>"
				+ "<th>Count</th>"
				+ "</tr>";

		
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);

		List<Alert> list = alertService.getLocationAlert(location, cal, alertWindowLength);
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++){
			result = result + "<tr>"
					+ "<td>"+list.get(i).getCrop()+"</td>"
					+ "<td>"+list.get(i).getAnnotation()+"</td>"
					+ "<td>"+df2.format(list.get(i).getIntensity())+"</td>"
					+ "<td>"+list.get(i).getCount()+"</td>"
					+ "</tr>";
		}
		result += "</table></body></html>";
		return result;
				
		//return alertService.getLocationAlert(location, cal, alertWindowLength);
	}
	
	@GET
	@Path("/annotations")
	public String getAllAnnotationAlerts(@BeanParam AlertBeans alertBean){
		String result = "<!doctype html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html><head>"
				+ "<style type=\"text/css\">"
				+ "h2{color:#3339ff;}"
				+ "table{border:1px solid black;text-align:center;align:center;}"
				+ "th{font-weight:bold;}"
				+ "tr{border:1px solid black;margin-top:10px;margin-bottom:10px;}"
				+ "td{margin-left:10px;margin-right:10px;}"
				+ "body{font-family:arial;}"
				+ "</style></head><body><h2>Showing All Annotation Alerts</h1>"
				+ "<table><tr>"
				+ "<th>Crop</th>"
				+ "<th>Location</th>"
				+ "<th>Annotation</th>"
				+ "<th>Intensity</th>"
				+ "<th>Count</th>"
				+ "</tr>";


		
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
		List<Alert> list = alertService.getAllAnnotationAlerts(cal, alertWindowLength);
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++){
			result = result + "<tr>"
					+ "<td>"+list.get(i).getCrop()+"</td>"
					+ "<td>"+list.get(i).getLocation()+"</td>"
					+ "<td>"+list.get(i).getAnnotation()+"</td>"
					+ "<td>"+df2.format(list.get(i).getIntensity())+"</td>"
					+ "<td>"+list.get(i).getCount()+"</td>"
					+ "</tr>";
		}
		result += "</table></body></html>";
		return result;
				
		//return alertService.getAllAnnotationAlerts(cal, alertWindowLength);
	}
	
	@GET
	@Path("/annotations/{annotation}")
	//@Produces(MediaType.TEXT_PLAIN)
	public String getAnnotationAlert(@PathParam("annotation") String annotation, 
			@BeanParam AlertBeans alertBean){
		String result = "<!doctype html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html><head>"
				+ "<style type=\"text/css\">"
				+ "h2{color:#3339ff;}"
				+ "table{border:1px solid black;text-align:center;align:center;}"
				+ "th{font-weight:bold;}"
				+ "tr{border:1px solid black;margin-top:10px;margin-bottom:10px;}"
				+ "td{margin-left:10px;margin-right:10px;}"
				+ "body{font-family:arial;}"
				+ "</style></head><body><h2>Showing Alerts For Annotation: "+annotation
				+ "</h1>"
				+ "<table><tr>"
				+ "<th>Crop</th>"
				+ "<th>Location</th>"
				+ "<th>Intensity</th>"
				+ "<th>Count</th>"
				+ "</tr>";


		
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
		List<Alert> list = alertService.getAnnotationAlert(annotation, cal, alertWindowLength);
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++){
			result = result + "<tr>"
					+ "<td>"+list.get(i).getCrop()+"</td>"
					+ "<td>"+list.get(i).getLocation()+"</td>"
					+ "<td>"+list.get(i).getAnnotation()+"</td>"
					+ "<td>"+df2.format(list.get(i).getIntensity())+"</td>"
					+ "<td>"+list.get(i).getCount()+"</td>"
					+ "</tr>";
		}
		result += "</table></body></html>";
		return result;
				
		//return alertService.getAnnotationAlert(annotation, cal, alertWindowLength);
	}
}
