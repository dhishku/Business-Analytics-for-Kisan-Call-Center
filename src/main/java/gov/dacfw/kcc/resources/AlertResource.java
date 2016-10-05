package gov.dacfw.kcc.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
@Produces(MediaType.APPLICATION_JSON)

public class AlertResource {
	private AlertService alertService = new AlertService();
	
	@GET
	public List<Alert> getAllAlerts(@BeanParam AlertBeans alertBean){
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
				
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
		
		return alertService.getAllAlerts(cal, alertWindowLength);
	}
	

	@GET
	@Path("/crops")
	public List<Alert> getAllCropAlerts(@BeanParam AlertBeans alertBean){
		//String result = "<html><head></head><body>This is a <a href=\"www.google.com\">link</a></body></html>";
		
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
						
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
				
		return alertService.getAllCropAlerts(cal, alertWindowLength);
	}
	
	@GET
	@Path("/crops/{crop}")
	public List<Alert> getCropAlert(@PathParam("crop") String crop, 
			@BeanParam AlertBeans alertBean){
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
		System.out.println("Inside getCropAlert for crop = "+crop);	
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println("Inside getCropAlert for current date = "+ df.format(cal.getTime()));
		return alertService.getCropAlert(crop, cal, alertWindowLength);
	}
	
	@GET
	@Path("/locations")
	public List<Alert> getAllLocationAlerts(@BeanParam AlertBeans alertBean){
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
				
		return alertService.getAllLocationAlerts(cal, alertWindowLength);
	}
	
	@GET
	@Path("/locations/{location}")
	//@Produces(MediaType.TEXT_PLAIN)
	public List<Alert> getLocationAlert(@PathParam("location") String location, 
			@BeanParam AlertBeans alertBean){
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
				
		return alertService.getLocationAlert(location, cal, alertWindowLength);
	}
	
	@GET
	@Path("/annotations")
	public List<Alert> getAllAnnotationAlerts(@BeanParam AlertBeans alertBean){
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
				
		return alertService.getAllAnnotationAlerts(cal, alertWindowLength);
	}
	
	@GET
	@Path("/annotations/{annotation}")
	//@Produces(MediaType.TEXT_PLAIN)
	public List<Alert> getAnnotationAlert(@PathParam("location") String location, 
			@BeanParam AlertBeans alertBean){
		// Setting the window length based on query parameters passed
		int alertWindowLength = alertService.getAlertWindowLength(alertBean);
								
		// Setting the current date based on query parameters passed
		Calendar cal = alertService.getCurrentDate(alertBean);
				
		return alertService.getAnnotationAlert(location, cal, alertWindowLength);
	}
}
