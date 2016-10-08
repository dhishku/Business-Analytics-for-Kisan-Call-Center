package gov.dacfw.kcc.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gov.dacfw.kcc.model.Alert;
import gov.dacfw.kcc.model.CallSummaryData;
import gov.dacfw.kcc.resources.beans.AlertBeans;

public class AlertService {

	KCCDatabaseService kccService = new KCCDatabaseService();


	public List<Alert> getAllAlerts(Calendar currentDate, int alertWindowLength) {
		List<String> cropsList = kccService.getAllCrops();
		List<String> locationsList = kccService.getAllLocations();
		List<String> annotationsList = kccService.getAllAnnotations();
		List<Alert> alertList = new ArrayList<Alert>();
		if ((cropsList.isEmpty()) || (locationsList.isEmpty()) || (annotationsList.isEmpty()))
			return alertList;
		
		// First create the beginning and the end dates
		int endDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		currentDate.add(Calendar.DATE, -alertWindowLength);
		int beginDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		// Now get the calls summary for this crop.
		List<CallSummaryData> callsSummaryTarget = kccService.getCallsSummary(locationsList, cropsList, annotationsList, beginDayOfYear, endDayOfYear, alertWindowLength);

		// Also get year wise total calls made in this window.
		List<CallSummaryData> callsSummaryTotal = kccService.getCallsSummary(null, null, null, beginDayOfYear, endDayOfYear, alertWindowLength);
		
		// Now return alerts from this data.
		return getAlerts(callsSummaryTarget, callsSummaryTotal);
	}

	public List<Alert> getAllCropAlerts(Calendar currentDate, int alertWindowLength) {
		List<String> cropsList = kccService.getAllCrops();
		List<Alert> alertList = new ArrayList<Alert>();
		if (cropsList.isEmpty())
			return alertList;
		
		// First create the beginning and the end dates
		int endDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		currentDate.add(Calendar.DATE, -alertWindowLength);
		int beginDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		// Now get the calls summary for this crop.
		List<CallSummaryData> callsSummaryTarget = kccService.getCallsSummary(null, cropsList, null, beginDayOfYear, endDayOfYear, alertWindowLength);

		// Also get year wise total calls made in this window.
		List<CallSummaryData> callsSummaryTotal = kccService.getCallsSummary(null, null, null, beginDayOfYear, endDayOfYear, alertWindowLength);
		
		// Now return alerts from this data.
		return getAlerts(callsSummaryTarget, callsSummaryTotal);
	}

	public List<Alert> getCropAlert(String crop, Calendar currentDate, int alertWindowLength) {
		List<String> crops = new ArrayList<String>();
		crops.add(crop);
		
		// First create the beginning and the end dates
		int endDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		currentDate.add(Calendar.DATE, -alertWindowLength);
		int beginDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		// Now get the calls summary for this crop.
		List<CallSummaryData> callsSummaryThisCrop = kccService.getCallsSummary(null, crops, null, beginDayOfYear, endDayOfYear, alertWindowLength);
		
		// Also get year wise total calls made in this window.
		List<CallSummaryData> callsSummaryTotal = kccService.getCallsSummary(null, null, null, beginDayOfYear, endDayOfYear, alertWindowLength);
		
		// Now return alerts from this data.
		return getAlerts(callsSummaryThisCrop, callsSummaryTotal);
	}

	/* In this method, first argument is the target summary table i.e. count of calls made for a 
	 * particular set of crop/location/annotation etc. Second argument is total calls summary 
	 * table i.e. count of total number of calls made in that year without any filters. 
	 * Note that, these tables contain only the number of calls made within the alert window.
	 * Also if there is any wrapping of calendar year involved, calls of previous year but this
	 * alert window will be showing as calls in this year.
	 * This method will calculate the proportion of target calls this season to the total calls 
	 * this season during our alert window. Similarly, it will calculate this metric for all
	 * the previous years. And then it will calculate the weighted average of previous years'
	 * metric decaying them by a lambda factor. Then it will calculate the intensity and if 
	 * it is greater than a particular threshold, it will declare it as an alert.
	 * */
	private List<Alert> getAlerts(List<CallSummaryData> callsSummaryTarget, List<CallSummaryData> callsSummaryTotal) {
		List<Alert> results = new ArrayList<Alert>();
		if (callsSummaryTarget.isEmpty() || callsSummaryTotal.isEmpty())
			return results;
		

		
		/* Now iterate through the target list. For each combination of location, crop, 
		 * annotation, see the count this year. Then see the count in previous years. Calculate
		 * respective proportions and see if it is an alert.
		 * */
		
		// First we create a parent callsummarydata object for storing previous values
		// of crop, location, annotaiton etc.
		CallSummaryData parentObject;
		
		// to begin, we set it to the first element in the list.
		parentObject = (CallSummaryData)callsSummaryTarget.get(0);
		String parentLocation = parentObject.getLocation();
		String parentCrop = parentObject.getCrop();
		String parentAnnotation = parentObject.getAnnotation();
		int parentYear = parentObject.getYear();
		int parentCount = parentObject.getCount();
		
		double currentMetric = 0;
		double previousMetric = 0;
		double previousWeight = 0;
		int prevSeason = 0;
		
		for (int i = 0; i < callsSummaryTarget.size(); i++){
			CallSummaryData currentObject = (CallSummaryData)callsSummaryTarget.get(i);
			//System.out.println("i: "+i+" currentObject[ "+currentObject.toString()+"]");
			if ((currentObject.getLocation().equals(parentLocation)) &&
					(currentObject.getCrop().equals(parentCrop)) &&
					(currentObject.getAnnotation().equals(parentAnnotation))){
				int totalCallsThisYear = getCount(currentObject.getYear(), callsSummaryTotal);
				double metric = getMetric((double)currentObject.getCount(), totalCallsThisYear);
				if ((currentObject.getYear() == parentYear)){
					// This section should be reached only in the first row.
					// Current Object is the present season's object. Get current year metric.
					currentMetric = metric;
					//System.out.print("LCAY equal. i: "+i+" Total calls: "+totalCallsThisYear);
					//System.out.println("location: "+parentLocation+" annotation: "+ parentAnnotation+ " count: "+parentCount+" currentMetric: "+ currentMetric+" div:"+(parentCount/totalCallsThisYear));
					
					// reset the previous year metric, weight and season count values
					previousMetric = 0; 
					previousWeight = 0;
					prevSeason = 0;

				} else{
					// Only the first 3 parameters are same. Year is different.
					prevSeason = parentYear-currentObject.getYear()-1;
					previousMetric += Math.pow(Alert.LAMBDA_DECAY, prevSeason) * metric;
					previousWeight += Math.pow(Alert.LAMBDA_DECAY, prevSeason);
					//System.out.print("LCA equal. But Y not equal. i: "+i+" Total calls: "+totalCallsThisYear);
					//System.out.println(" location: "+parentLocation+" annotation: "+ parentAnnotation+ " count: "+currentObject.getCount()+" previousWeight: "+previousWeight+" previousMetric: "+ previousMetric+ " metric: "+metric+" div:"+(parentCount/totalCallsThisYear));

					/* Now check if end of list has been reached. If end of list has been 
					 * reached, then we need to check for an alert and add it if found.
					 */
					if (i == callsSummaryTarget.size()-1){
						if (isAlert(currentMetric, previousMetric, previousWeight, parentCount)){
							/* Alert triggered. So set an alert and add it to the results. */
							Alert alert = new Alert();
							alert.setAlert(true);
							alert.setAnnotation(parentAnnotation);
							alert.setCrop(parentCrop);
							alert.setCount(parentCount);
							alert.setLocation(parentLocation);
							alert.setIntensity(getIntensity(currentMetric, previousMetric, previousWeight));
							results.add(alert);
						}
						// else no alert and no need to do anything.
					} // else not end of list and one can happily check next element.
				}
			} else{
				/* Reaching here means a different combination of location, crop, annotation found.
				 * This means we must evaluate the previous combination for alert.
				 * */
				if (isAlert(currentMetric, previousMetric, previousWeight, parentCount)){
					/* Alert triggered. So set an alert and add it to the results. */
					Alert alert = new Alert();
					alert.setAlert(true);
					alert.setAnnotation(parentAnnotation);
					alert.setCrop(parentCrop);
					alert.setCount(parentCount);
					alert.setLocation(parentLocation);
					alert.setIntensity(getIntensity(currentMetric, previousMetric, previousWeight));
					results.add(alert);
				}
				/* Reset parent Object and the current and previous parameters*/
				parentObject = (CallSummaryData)callsSummaryTarget.get(i);
				parentLocation = parentObject.getLocation();
				parentCrop = parentObject.getCrop();
				parentAnnotation = parentObject.getAnnotation();
				parentCount = parentObject.getCount();
				parentYear = parentObject.getYear();
				
				// Set currentMetric
				int totalCallsThisYear = getCount(parentYear, callsSummaryTotal);
				currentMetric = getMetric((double)parentCount, totalCallsThisYear);
				//System.out.print("New LCA. i: "+i+" Total calls = "+totalCallsThisYear);
				//System.out.println("location: "+parentLocation+" annotation: "+ parentAnnotation+ " count: "+parentCount+" currentMetric: "+ currentMetric+" div:"+(parentCount/totalCallsThisYear));

				// reset the previous year metric, weight and season count values
				previousMetric = 0; 
				previousWeight = 0;
			}
		}
		
		return results;
	}

	/* This returns the intensity of the metric. Presently simply divides currentMetric by
	 * the previousMetric and returns the result.*/
	private double getIntensity(double currentMetric, double previousMetric, double previousWeight) {
		// TODO Auto-generated method stub
		if ((previousMetric == 0) || (previousWeight == 0))
			return 0;
		
		return (currentMetric/(previousMetric/previousWeight));
	}

	/* This method tells if there is an alert. Presently it just divides the two metrics and
	 * sees if it is > a defined constant (Alert.THRESHOLD) and the count is > a defined 
	 * constant (Alert.MINIMUM_CALL_COUNT).
	 * */
	private boolean isAlert(double currentMetric, double previousMetric, double previousWeight, int count) {
		// TODO Auto-generated method stub
		if (previousMetric == 0)
			return false;
		if (count < Alert.MINIMUM_CALLS_FOR_ALERT)
			return false;
		if ((getIntensity(currentMetric, previousMetric, previousWeight) >= Alert.NORMAL_INTENSITY_CEILING))
			return true;
		if ((getIntensity(currentMetric, previousMetric, previousWeight) <= Alert.NORMAL_INTENSITY_FLOOR))
			return true;
		return false;
	}

	/* This method returns the metric value. Presently it simply returns the proportion.
	 * */
	private double getMetric(double count, int totalCount) {
		// TODO Auto-generated method stub
		if (totalCount == 0)
			return 0;
		return (count/totalCount);
	}

	private int getCount(int year, List<CallSummaryData> callsSummaryTotal) {
		int count = 0;
		if (callsSummaryTotal.isEmpty())
			return count;

		for (int i = 0; i < callsSummaryTotal.size(); i++){
			if (year == ((CallSummaryData)callsSummaryTotal.get(i)).getYear())
				count = count + ((CallSummaryData)callsSummaryTotal.get(i)).getCount();
		}
		
		return count;
	}

	public List<Alert> getAllLocationAlerts(Calendar currentDate, int alertWindowLength) {
		List<String> locationsList = kccService.getAllLocations();
		List<Alert> alertList = new ArrayList<Alert>();
		if (locationsList.isEmpty())
			return alertList;
		
		// First create the beginning and the end dates
		int endDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		currentDate.add(Calendar.DATE, -alertWindowLength);
		int beginDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		// Now get the calls summary for this crop.
		List<CallSummaryData> callsSummaryTarget = kccService.getCallsSummary(locationsList, null, null, beginDayOfYear, endDayOfYear, alertWindowLength);

		// Also get year wise total calls made in this window.
		List<CallSummaryData> callsSummaryTotal = kccService.getCallsSummary(null, null, null, beginDayOfYear, endDayOfYear, alertWindowLength);
		
		// Now return alerts from this data.
		return getAlerts(callsSummaryTarget, callsSummaryTotal);
	}

	public List<Alert> getLocationAlert(String location, Calendar currentDate, int alertWindowLength) {
		List<String> locations = new ArrayList<String>();
		locations.add(location);
		
		// First create the beginning and the end dates
		int endDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		
		currentDate.add(Calendar.DATE, -alertWindowLength);
		int beginDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		
		// Now get the calls summary for this crop.
		List<CallSummaryData> callsSummaryThisLocation = kccService.getCallsSummary(locations, null, null, beginDayOfYear, endDayOfYear, alertWindowLength);
		
		// Also get year wise total calls made in this window.
		List<CallSummaryData> callsSummaryTotal = kccService.getCallsSummary(null, null, null, beginDayOfYear, endDayOfYear, alertWindowLength);
		
		// Now return alerts from this data.
		return getAlerts(callsSummaryThisLocation, callsSummaryTotal);
	}

	/* This method will parse the alertBean or the query params.
	 * Return will be alertWindowLength to be used.
	 * If it has been passed with the query it will set to the passed 
	 * parameter else will use the default values.
	 * */

	public int getAlertWindowLength(AlertBeans alertBean) {
		if (alertBean.getAlertWindowLength() > 0)
			return alertBean.getAlertWindowLength();
		else
			return Alert.ALERT_WINDOW;

	}

	/* This method will parse the alertBean or the query params.
	 * Return will be Calendar object set to current date to be used.
	 * If it has been passed with the query it will set to the passed 
	 * parameter else will use the default values i.e. true current date.
	 * */

	public Calendar getCurrentDate(AlertBeans alertBean) {
		Calendar cal = Calendar.getInstance();
		if (alertBean.getCurrentDate() != null){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			try {
				date = df.parse(alertBean.getCurrentDate());
				cal.setTime(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} 
		return cal;
	}

	public List<Alert> getAllAnnotationAlerts(Calendar currentDate, int alertWindowLength) {
		List<String> annotationsList = kccService.getAllAnnotations();
		List<Alert> alertList = new ArrayList<Alert>();
		if (annotationsList.isEmpty())
			return alertList;

		// First create the beginning and the end dates
		int endDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		currentDate.add(Calendar.DATE, -alertWindowLength);
		int beginDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		// Now get the calls summary for this crop.
		List<CallSummaryData> callsSummaryTarget = kccService.getCallsSummary(null, null, annotationsList, beginDayOfYear, endDayOfYear, alertWindowLength);

		// Also get year wise total calls made in this window.
		List<CallSummaryData> callsSummaryTotal = kccService.getCallsSummary(null, null, null, beginDayOfYear, endDayOfYear, alertWindowLength);
		
		// Now return alerts from this data.
		return getAlerts(callsSummaryTarget, callsSummaryTotal);
	}

	public List<Alert> getAnnotationAlert(String annotation, Calendar currentDate, int alertWindowLength) {
		List<String> annotations = new ArrayList<String>();
		annotations.add(annotation);
		
		// First create the beginning and the end dates
		int endDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		
		currentDate.add(Calendar.DATE, -alertWindowLength);
		int beginDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
		
		// Now get the calls summary for this crop.
		List<CallSummaryData> callsSummaryThisAnnotation = kccService.getCallsSummary(null, null, annotations, beginDayOfYear, endDayOfYear, alertWindowLength);

		// Also get year wise total calls made in this window.
		List<CallSummaryData> callsSummaryTotal = kccService.getCallsSummary(null, null, null, beginDayOfYear, endDayOfYear, alertWindowLength);
		
		// Now return alerts from this data.
		return getAlerts(callsSummaryThisAnnotation, callsSummaryTotal);
	}
}
