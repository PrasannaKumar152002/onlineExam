package com.exam.events;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.exam.forms.HibernateValidationMaster;
import com.exam.forms.checks.ExamTopicMappingCheck;
import com.exam.helper.HibernateHelper;
import com.exam.util.ConstantValues;
import com.exam.util.EntityConstants;

public class ExamTopicMappingEvents {
	public static final String MODULE = ExamTopicMappingEvents.class.getName();
	private static final String RES_ERR = "OnlineExamPortalUiLabels";

	public static String calculatePercentage(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Map<String, Object> combinedMap = UtilHttp.getCombinedMap(request);

		Delegator delegator = (Delegator) combinedMap.get(EntityConstants.DELEGATOR);

		String examId = (String) combinedMap.get(ConstantValues.EXAM_ID);
		String percentage = (String) combinedMap.get(ConstantValues.EXAM_TOPIC_PERCENTAGE);
		GenericValue exam = EntityQuery.use(delegator).from("ExamMaster").where(ConstantValues.EXAM_ID, examId)
				.queryOne();
		Debug.log("Exam: " + exam);
		String examQues = exam.getString(ConstantValues.EXAM_TOTAL_QUES);
		Debug.log("--------------------- " + examQues + " " + (Integer.valueOf(examQues)));
		Debug.log("Percentage: " + percentage + " " + Integer.valueOf(percentage));
		Integer questionsPerExam = (int) ((Float.valueOf(percentage) / 100) * Integer.valueOf(examQues));
		Debug.log("TopicPerExamQuestions: " + ((Integer.valueOf(percentage) / 100) * Integer.valueOf(examQues)));
		Debug.log("=============" + questionsPerExam);
		request.setAttribute("output", questionsPerExam);
		return "success";
	}

	// Method to insert data into ExamTopicMapping Entity
	public static String createTopicForExam(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> combinedMap = UtilHttp.getCombinedMap(request);

		Delegator delegator = (Delegator) combinedMap.get(EntityConstants.DELEGATOR);
		LocalDispatcher dispatcher = (LocalDispatcher) combinedMap.get(EntityConstants.DISPATCHER);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute(EntityConstants.USER_LOGIN);
		Integer questionsForExam = 0;
		String examId = (String) combinedMap.get(ConstantValues.EXAM_ID);
		String topicId = (String) combinedMap.get(ConstantValues.TOPIC_ID);
		String percentage = (String) combinedMap.get(ConstantValues.EXAM_TOPIC_PERCENTAGE);
		String topicPassPercentage = (String) combinedMap.get(ConstantValues.EXAM_TOPIC_PASS_PERCENTAGE);
		String questionsPerTopic = (String) combinedMap.get(ConstantValues.TOPIC_QUES_PER_EXAM);
		GenericValue exam = EntityQuery.use(delegator).from("ExamMaster").where(ConstantValues.EXAM_ID, examId)
				.queryOne();
		Debug.log("Exam: " + exam);

		Map<String, Object> examTopicInfo = UtilMisc.toMap(ConstantValues.EXAM_ID, examId, ConstantValues.TOPIC_ID,
				topicId, ConstantValues.EXAM_TOPIC_PERCENTAGE, percentage, ConstantValues.EXAM_TOPIC_PASS_PERCENTAGE,
				topicPassPercentage, ConstantValues.TOPIC_QUES_PER_EXAM, questionsPerTopic, EntityConstants.USER_LOGIN,
				userLogin);
		Debug.log("EXAMTOPIC: " + examTopicInfo);
		try {
			Integer examQues = Integer.parseInt(exam.getString(ConstantValues.EXAM_TOTAL_QUES));
			Debug.log("*********************" + examQues);
			List<GenericValue> questionsPerTopicList = EntityQuery.use(delegator).from("ExamTopicMapping")
					.where(ConstantValues.EXAM_ID, examId).queryList();
			Debug.log("List\n" + questionsPerTopicList);
			if (UtilValidate.isEmpty(questionsPerTopicList)) {

				Map<String, ? extends Object> createExamTopicMappingInfoResult = dispatcher
						.runSync("CreateExamTopicMapping", examTopicInfo);
				Debug.log("Result " + createExamTopicMappingInfoResult);
			} else {
				for (GenericValue questionPerTopic : questionsPerTopicList) {
					String questionsCount = questionPerTopic.getString(ConstantValues.TOPIC_QUES_PER_EXAM);
					Debug.log("--------" + questionsCount);
					questionsForExam = questionsForExam + Integer.parseInt(questionsCount);
				}
				Integer checkQuestionsCount = questionsForExam + Integer.parseInt(questionsPerTopic);
				Debug.log("QuestionForExam:" + questionsForExam);
				Debug.log("TotalQuesPerCount:" + checkQuestionsCount);
				if (checkQuestionsCount <= examQues) {
					Map<String, ? extends Object> createExamTopicMappingInfoResult = dispatcher
							.runSync("CreateExamTopicMapping", examTopicInfo);
					Debug.log("Result " + createExamTopicMappingInfoResult);
				} else {
					Debug.log("You can't create more questions for topics in this exam...");
				}
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		Map<String, Object> examtopicinfo = UtilMisc.toMap(ConstantValues.EXAM_ID, examId, ConstantValues.TOPIC_ID,
//				topicId, ConstantValues.EXAMTOPIC_PERCENTAGE, percentage,
//				ConstantValues.EXAMTOPIC_TOPIC_PASS_PERCENTAGE, topicPassPercentage,
//				EntityConstants.USER_LOGIN, userLogin);
//
//		try {
//			Debug.logInfo(
//					"=======Creating ExamTopicMapping record in event using service UpdateExamTopicMapping=========",
//					MODULE);
//			HibernateValidationMaster hibernate = HibernateHelper.populateBeanFromMap(examtopicinfo,
//					HibernateValidationMaster.class);
//
//			Set<ConstraintViolation<HibernateValidationMaster>> errors = HibernateHelper
//					.checkValidationErrors(hibernate, ExamTopicMappingCheck.class);
//			boolean hasFormErrors = HibernateHelper.validateFormSubmission(delegator, errors, request, locale,
//					"Mandatory Err ExamTopicMapping Entity", RES_ERR, false);
//
//			if (hasFormErrors) {
//				request.setAttribute("_ERROR_MESSAGE", errors);
//				return "error";
//			}
//				try {
//					// Calling Entity-Auto Service to Insert data into ExamTopicMapping Entity
//					Map<String, ? extends Object> createExamTopicMappingInfoResult = dispatcher
//							.runSync("CreateExamTopicMapping", examtopicinfo);
//					ServiceUtil.getMessages(request, createExamTopicMappingInfoResult, null);
//					if (ServiceUtil.isError(createExamTopicMappingInfoResult)) {
//						String errorMessage = ServiceUtil.getErrorMessage(createExamTopicMappingInfoResult);
//						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
//						Debug.logError(errorMessage, MODULE);
//						return "error";
//					} else {
//						String successMessage = UtilProperties.getMessage(RES_ERR, "ServiceSuccessMessage",
//								UtilHttp.getLocale(request));
//						ServiceUtil.getMessages(request, createExamTopicMappingInfoResult, successMessage);
//						request.setAttribute("_EVENT_MESSAGE_", successMessage);
//						return "success";
//					}
//				} catch (GenericServiceException e) {
//					String errMsg = UtilProperties.getMessage(RES_ERR, "ServiceCallingError",
//							UtilHttp.getLocale(request)) + e.toString();// "Error setting exam_topic_mapping info: " +
//																		// e.toString();
//					request.setAttribute("_ERROR_MESSAGE_", errMsg);
//					return "error";
//				}
//			
//		} catch (Exception e) {
//			Debug.logError(e, MODULE);
//			request.setAttribute("_ERROR_MESSAGE", e);
//			return "error";
//		}
		request.setAttribute("output", questionsForExam);
		return "onprocess";
	}

	// Method to retrieve data's from ExamTopicMapping Entity
	public static String fetchAllExamTopics(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute(EntityConstants.DELEGATOR);
		List<Map<String, Object>> viewExamTopicMapList = new ArrayList<Map<String, Object>>();
		try {
			// Query to retrieve data's from ExamTopicMapping Entity
			List<GenericValue> listOfExamTopicMapData = EntityQuery.use(delegator).from("ExamTopicMapping").queryList();
			if (UtilValidate.isNotEmpty(listOfExamTopicMapData)) {
				for (GenericValue topicOfExam : listOfExamTopicMapData) {
					Map<String, Object> topicMapList = new HashMap<String, Object>();
					topicMapList.put(ConstantValues.EXAM_ID, topicOfExam.get(ConstantValues.EXAM_ID));
					topicMapList.put(ConstantValues.TOPIC_ID, topicOfExam.get(ConstantValues.TOPIC_ID));

					topicMapList.put(ConstantValues.EXAM_TOPIC_PERCENTAGE,
							topicOfExam.get(ConstantValues.EXAM_TOPIC_PERCENTAGE));
					topicMapList.put(ConstantValues.EXAM_TOPIC_PASS_PERCENTAGE,
							topicOfExam.get(ConstantValues.EXAM_TOPIC_PASS_PERCENTAGE));
					topicMapList.put(ConstantValues.TOPIC_QUES_PER_EXAM,
							topicOfExam.get(ConstantValues.TOPIC_QUES_PER_EXAM));
					viewExamTopicMapList.add(topicMapList);
				}
				Map<String, Object> examforTopicsInfo = new HashMap<>();
				examforTopicsInfo.put("ExamTopicList", viewExamTopicMapList);
				request.setAttribute("ExamTopicsInfo", examforTopicsInfo);
				return "success";
			}
			String errorMessage = UtilProperties.getMessage(RES_ERR, "ErrorInFetchingData",
					UtilHttp.getLocale(request));// "No matched fields in ExamTopicMapping Entity";
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			Debug.logError(errorMessage, MODULE);
			return "error";

		} catch (GenericEntityException e) {
			request.setAttribute("Error", e);
			return "error";
		}
	}

	// Method to Update data into ExamTopicMapping Entity
	public static String updateTopicForExam(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);

		Delegator delegator = (Delegator) request.getAttribute(EntityConstants.DELEGATOR);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute(EntityConstants.DISPATCHER);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute(EntityConstants.USER_LOGIN);

		String examId = (String) request.getAttribute(ConstantValues.EXAM_ID);
		String topicId = (String) request.getAttribute(ConstantValues.TOPIC_ID);
		String percentage = (String) request.getAttribute(ConstantValues.EXAM_TOPIC_PERCENTAGE);
		String topicPassPercentage = (String) request.getAttribute(ConstantValues.EXAM_TOPIC_PASS_PERCENTAGE);
		String questionsPerExam = (String) request.getAttribute(ConstantValues.TOPIC_QUES_PER_EXAM);

		Map<String, Object> examtopicinfo = UtilMisc.toMap(ConstantValues.EXAM_ID, examId, ConstantValues.TOPIC_ID,
				topicId, ConstantValues.EXAM_TOPIC_PERCENTAGE, percentage, ConstantValues.EXAM_TOPIC_PASS_PERCENTAGE,
				topicPassPercentage, ConstantValues.TOPIC_QUES_PER_EXAM, questionsPerExam, EntityConstants.USER_LOGIN,
				userLogin);

		try {
			Debug.logInfo(
					"=======Updating ExamTopicMapping record in event using service UpdateExamTopicMappingMaster=========",
					MODULE);
			HibernateValidationMaster hibernate = HibernateHelper.populateBeanFromMap(examtopicinfo,
					HibernateValidationMaster.class);

			Set<ConstraintViolation<HibernateValidationMaster>> errors = HibernateHelper
					.checkValidationErrors(hibernate, ExamTopicMappingCheck.class);
			boolean hasFormErrors = HibernateHelper.validateFormSubmission(delegator, errors, request, locale,
					"Mandatory Err ExamTopicMapping Entity", RES_ERR, false);

			if (hasFormErrors) {
				request.setAttribute("_ERROR_MESSAGE", errors);
				return "error";
			}
			try {
				// Calling Entity-Auto Service to Update data into ExamTopicMapping
				Map<String, ? extends Object> updateExamTopicMappingInfoResult = dispatcher
						.runSync("UpdateExamTopicMappingMaster", examtopicinfo);
				ServiceUtil.getMessages(request, updateExamTopicMappingInfoResult, null);
				if (ServiceUtil.isError(updateExamTopicMappingInfoResult)) {
					String errorMessage = ServiceUtil.getErrorMessage(updateExamTopicMappingInfoResult);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					Debug.logError(errorMessage, MODULE);
					return "error";
				}
				String successMessage = UtilProperties.getMessage(RES_ERR, "ServiceSuccessMessage",
						UtilHttp.getLocale(request));
				ServiceUtil.getMessages(request, updateExamTopicMappingInfoResult, successMessage);
				request.setAttribute("_EVENT_MESSAGE_", successMessage);
				return "success";

			} catch (GenericServiceException e) {
				String errMsg = UtilProperties.getMessage(RES_ERR, "ServiceCallingError", UtilHttp.getLocale(request))
						+ e.toString();// "Error setting exam_topic_mapping info: " +
										// e.toString();
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
			request.setAttribute("_ERROR_MESSAGE", e);
			return "error";
		}
	}

//	public static String questionsPerTopic(HttpServletRequest request, HttpServletResponse response)
//	{
//		Delegator delegator = (Delegator) request.getAttribute(EntityConstants.DELEGATOR);
//		Map<String,Object> combinedMap = UtilHttp.getCombinedMap(request);
//		
//		Integer totalQuesCount = 0;
//		String examId = (String)combinedMap.get(ConstantValues.EXAM_ID);
//		String topicId = (String)combinedMap.get(ConstantValues.TOPIC_ID);
//		String questionPercentage = (String)combinedMap.get(ConstantValues.EXAMTOPIC_PERCENTAGE);
//		String questionsPerTopic = (String)combinedMap.get(ConstantValues.EXAMTOPIC_QUES_PER_EXAM);
//		String percentage = (String) combinedMap.get(ConstantValues.EXAMTOPIC_PERCENTAGE);
//		String topicPassPercentage = (String) combinedMap.get(ConstantValues.EXAMTOPIC_TOPIC_PASS_PERCENTAGE);
//		Integer questionsPerExam = 0;
//		GenericValue userLogin = (GenericValue) request.getSession().getAttribute(EntityConstants.USER_LOGIN);
//		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute(EntityConstants.DISPATCHER);
//		
//		Debug.log("^^^^^^^^^^^^^ "+examId + " " + topicId + " " + questionPercentage + " " + questionsPerTopic + " " + percentage + " " + topicPassPercentage);
//		try {
//			GenericValue exam = EntityQuery.use(delegator).from("ExamMaster").where(ConstantValues.EXAM_ID, examId).queryOne();
//			Debug.log("@@@@@@@@@@@@@@@" + exam);
//			if (exam==null)
//			{
//				questionsPerExam = 
//				Map<String, Object> examtopicinfo = UtilMisc.toMap(ConstantValues.EXAM_ID, examId, ConstantValues.TOPIC_ID,
//						topicId, ConstantValues.EXAMTOPIC_PERCENTAGE, percentage,
//						ConstantValues.EXAMTOPIC_TOPIC_PASS_PERCENTAGE, topicPassPercentage,ConstantValues.EXAMTOPIC_QUES_PER_EXAM, questionsPerExam,
//						EntityConstants.USER_LOGIN, userLogin);
//				try {
//					Map<String, ? extends Object> createExamTopicMappingInfoResult = dispatcher
//							.runSync("CreateExamTopicMapping", examtopicinfo);
//					Debug.log("Result " + createExamTopicMappingInfoResult);
//				} catch (GenericServiceException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			String examQues = exam.getString(ConstantValues.EXAM_TOTAL_QUES);
//			Debug.log("*********************"+examQues);
//			List<GenericValue> questionsPerTopicList = EntityQuery.use(delegator).from("ExamTopicMapping").where(ConstantValues.EXAM_ID, examId).queryList();
//			Debug.log("List\n"+questionsPerTopicList);
//			for (GenericValue questionPerTopic:questionsPerTopicList)
//			{
//				String questionsCount = questionPerTopic.getString(ConstantValues.EXAMTOPIC_QUES_PER_EXAM);
//				Debug.log("--------"+questionsCount);
//				totalQuesCount = totalQuesCount + Integer.parseInt(questionsCount);
//			}
//		} catch (GenericEntityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		request.setAttribute("output", totalQuesCount);
//		return "success";
//		
//	}

}
