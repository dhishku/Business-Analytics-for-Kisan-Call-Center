package gov.dacfw.kcc.resources;

import java.sql.Date;
import java.util.List;

import gov.dacfw.kcc.model.CallData;
import gov.dacfw.kcc.resources.beans.KCCBeansParams;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import gov.dacfw.kcc.services.KCCService;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class KCCResource {
	KCCService kccService = new KCCService();

 @GET
 public List<CallData> getData(@BeanParam KCCBeansParams kccBean) {
	 
  return kccService.getData(kccBean.getLocation(), kccBean.getCrop(), 
		  kccBean.getBeginDate(), kccBean.getEndDate());
 }
 
 
}