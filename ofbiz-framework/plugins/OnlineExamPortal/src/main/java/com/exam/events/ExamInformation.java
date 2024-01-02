package com.exam.events;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;


public class ExamInformation {

	// Define a constant for the class name
	public static final String module = ExamInformation.class.getName();

	// Method to retrieve exam information
	public static String getExamInfo(HttpServletRequest request, HttpServletResponse response) {
		// Retrieve delegator and local dispatcher from the request attributes
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

		try {
			// Log the login process
			Debug.log("=======Logging in process started=========");

			// Invoke the service to get exam information
			Map<String, Object> examInformationResult = dispatcher.runSync("getExamInformation",
					UtilMisc.toMap("userLogin", userLogin));

			// Check if the service call resulted in an error
			if (ServiceUtil.isError(examInformationResult)) {
				// Handle error scenario
				String errorMessage = ServiceUtil.getErrorMessage(examInformationResult);
				request.setAttribute("ERROR_MESSAGE", errorMessage);
				Debug.logError(errorMessage, module);
				return "error";
			} else {
				// Handle success scenario
				String successMessage = "getExamInformation successfully.";
				ServiceUtil.getMessages(request, examInformationResult, successMessage);
				request.setAttribute("exam", examInformationResult);
				return "success";
			}

		} catch (Exception e) {
			// Handle exceptions during service invocation
			Debug.logError(e, module);
			String errMsg = "Error in calling or executing the service: " + e.toString();
			request.setAttribute("ERROR_MESSAGE", errMsg);
			return "error";
		}
	}
}
