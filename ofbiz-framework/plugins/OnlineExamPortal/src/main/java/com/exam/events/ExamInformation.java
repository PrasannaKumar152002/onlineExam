package com.exam.events;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.exam.util.ConstantValues;
import com.exam.util.EntityConstants;

public class ExamInformation {

	// Define a constant for the class name
	public static final String module = ExamInformation.class.getName();
	private static final String RES_ERR = "OnlineExamPortalUiLabels";

	// Method to retrieve exam information
	public static String getExamInfo(HttpServletRequest request, HttpServletResponse response) {
		// Retrieve delegator and local dispatcher from the request attributes
		Delegator delegator = (Delegator) request.getAttribute(EntityConstants.DELEGATOR);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute(EntityConstants.DISPATCHER);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute(EntityConstants.USER_LOGIN);
		

		try {
			// Log the login process
			Debug.log("=======Logging in process started=========");

			// Invoke the service to get exam information
			Map<String, Object> getExamInformationServiceresult = dispatcher.runSync("getExamInformation",
					UtilMisc.toMap(EntityConstants.USER_LOGIN, userLogin));

			// Check if the service call resulted in an error
			if (ServiceUtil.isError(getExamInformationServiceresult)) {
				// Handle error scenario
				String errorMessage = ServiceUtil.getErrorMessage(getExamInformationServiceresult);
				request.setAttribute("ERROR_MESSAGE", errorMessage);
				Debug.logError(errorMessage, module);
				return "error";
			}

			// Handle success scenario
			String successMessage = "getExamInformation successfully.";
			ServiceUtil.getMessages(request, getExamInformationServiceresult, successMessage);
			request.setAttribute("exam", getExamInformationServiceresult);
			return "success";

		} catch (Exception e) {
			// Handle exceptions during service invocation
			Debug.logError(e, module);
			String errMsg = UtilProperties.getMessage(RES_ERR, "ServiceCallingError", UtilHttp.getLocale(request))+ e.toString();//"Error in calling or executing the service: ";
			request.setAttribute("ERROR_MESSAGE", errMsg);
			return "error";
		}
	}
}