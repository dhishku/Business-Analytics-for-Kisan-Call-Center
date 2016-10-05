package gov.dacfw.kcc.resources;

import java.util.List;

import gov.dacfw.kcc.model.CallData;
import gov.dacfw.kcc.resources.beans.KCCBeans;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import gov.dacfw.kcc.services.KCCDatabaseService;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class KCCResource {
	private KCCDatabaseService kccService = new KCCDatabaseService();

 @GET
 public List<CallData> getData(@BeanParam KCCBeans kccBean) {
	 /*Calendar cal = Calendar.getInstance();
	 SimpleDateFormat dForm = new SimpleDateFormat("yyyy-MM-dd");
	 cal.add(Calendar.DATE, -300);
	 return dForm.format(cal.getTime());*/ 
	 
  return kccService.getData(kccBean.getLocation(), 
		  kccBean.getCrop(), 
		  kccBean.getAnnotation(),
		  kccBean.getBeginDate(), 
		  kccBean.getEndDate());
 }
 
 @Path("/alerts")
 public AlertResource getAlertResource(){
	 return new AlertResource();
 }
 
}