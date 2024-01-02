package com.exam.events;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.exam.util.ConstantValues;

public class QuestionInformation {
	// Define a constant for the class name
	public static final String MODULE_NAME = QuestionInformation.class.getName();

	public static String getQuestionInfo(HttpServletRequest request, HttpServletResponse response) {
		// Retrieve the LocalDispatcher and Delegator from the request attributes
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

		// Retrieve examId, noOfQuestions, and initialize sequenceNum and performanceId
		String examId = request.getAttribute(ConstantValues.EXAM_ID).toString();
		String noOfQuestions = request.getAttribute(ConstantValues.EXAM_TOTAL_QUES).toString();
		int sequenceNum = 1;
		String performanceId = null;

		try {
			// Validate examId
			if (UtilValidate.isEmpty(examId)) {
				String errMsg = "Exam ID is a required field on the form and can't be empty.";
				request.setAttribute("ERROR_MESSAGE", errMsg);
				return "error";
			}

			// Validate noOfQuestions
			if (UtilValidate.isEmpty(noOfQuestions)) {
				String errMsg = "Number of Questions is a required field on the form and can't be empty.";
				request.setAttribute("ERROR_MESSAGE", errMsg);
				return "error";
			}

			Debug.log("=======Logging in process started=========");

			// Query UserExamMapping for the given examId
			List<GenericValue> userExamList = EntityQuery.use(delegator).from("UserExamMapping")
					.where(ConstantValues.EXAM_ID, examId).queryList();

			// Check if userExamList is empty
			if (UtilValidate.isEmpty(userExamList)) {
				String errMsg = "User Exam List is empty.";
				request.setAttribute("ERROR_MESSAGE", errMsg);
				return "error";
			}

			// Process each UserExamMapping
			for (GenericValue userExamInfo : userExamList) {
				// Retrieve necessary information from UserExamMapping
				String partyId = userExamInfo.getString(ConstantValues.USEREXAM_PARTY_ID);
				String noOfAttempt = userExamInfo.getString(ConstantValues.USEREXAM_NO_OF_ATTEMPTS);
				String allowedAttempt = userExamInfo.getString(ConstantValues.USEREXAM_ALLOWED_ATTEMPTS);
				Integer attemptCount = Integer.parseInt(noOfAttempt);
				noOfAttempt = String.valueOf(attemptCount + 1);

				// Call service to create a UserAttemptMaster record
				Map<String, Object> userAttemptMasterResult = dispatcher.runSync("createUserAttemptMaster",
						UtilMisc.toMap(ConstantValues.EXAM_ID, examId, ConstantValues.EXAM_TOTAL_QUES, noOfQuestions,
								ConstantValues.USEREXAM_PARTY_ID, partyId, ConstantValues.USEREXAM_NO_OF_ATTEMPTS,
								noOfAttempt));

				// Check if the service call resulted in an error
				if (ServiceUtil.isError(userAttemptMasterResult)) {
					String errorMessage = ServiceUtil.getErrorMessage(userAttemptMasterResult);
					request.setAttribute("ERROR_MESSAGE", errorMessage);
					Debug.logError(errorMessage, MODULE_NAME);
					return "error";
				} else {
					// Handle success scenario
					String successMessage = "User Attempt Master created successfully.";
					ServiceUtil.getMessages(request, userAttemptMasterResult, successMessage);
					performanceId = userAttemptMasterResult.get(ConstantValues.USER_ATTEMPT_PERFORMANCE_ID)
							.toString();
				}

				
				if (UtilValidate.isEmpty(performanceId)) {
					String errMsg = "Performance ID is a required field on the form and can't be empty.";
					request.setAttribute("ERROR_MESSAGE", errMsg);
					return "error";
				}

				Debug.log("Create User Attempt Master Result======================" + userAttemptMasterResult);

				// Query ExamTopicMapping for the given examId
				List<GenericValue> examTopicMapping = EntityQuery.use(delegator).from("ExamTopicMapping")
						.where(ConstantValues.EXAM_ID, examId).queryList();

				// Check if examTopicList is empty
				if (UtilValidate.isEmpty(examTopicMapping)) {
					String errMsg = "Exam Topic List is empty.";
					request.setAttribute("ERROR_MESSAGE", errMsg);
					return "error";
				}

				// Process each ExamTopicMapping
				for (GenericValue oneExamTopic : examTopicMapping) {
					// Retrieve necessary information from ExamTopicMapping
					String topicId = oneExamTopic.getString(ConstantValues.TOPIC_ID);
					String topicPassPercentage = oneExamTopic.getString(ConstantValues.EXAMTOPIC_TOPIC_PASS_PERCENTAGE);
					String questionsPerExam = oneExamTopic.getString(ConstantValues.EXAMTOPIC_QUES_PER_EXAM);

					// Validate topic information
					if (UtilValidate.isEmpty(topicId) || UtilValidate.isEmpty(topicPassPercentage)
							|| UtilValidate.isEmpty(questionsPerExam)) {
						String errMsg = "Topic information is incomplete or empty.";
						request.setAttribute("ERROR_MESSAGE", errMsg);
						return "error";
					}

					// Call service to create a UserAttemptTopicMaster record
					Map<String, Object> userAttemptTopicMasterResult = dispatcher.runSync(
							"createUserAttemptTopicMaster",
							UtilMisc.toMap(ConstantValues.TOPIC_ID, topicId, ConstantValues.USER_ANSWER_PERFORMANCE_ID,
									performanceId, ConstantValues.EXAMTOPIC_TOPIC_PASS_PERCENTAGE, topicPassPercentage,
									ConstantValues.USER_TOPIC_TOTAL_QUES, questionsPerExam));

					// Check if the service call resulted in an error
					if (ServiceUtil.isError(userAttemptTopicMasterResult)) {
						String errorMessage = ServiceUtil.getErrorMessage(userAttemptTopicMasterResult);
						request.setAttribute("ERROR_MESSAGE", errorMessage);
						Debug.logError(errorMessage, MODULE_NAME);
						return "error";
					} else {
						// Handle success scenario
						String successMessage = "User Attempt Topic Master created successfully.";
						ServiceUtil.getMessages(request, userAttemptTopicMasterResult, successMessage);
						request.setAttribute("EVENT_MESSAGE", successMessage);
					}

					// Call service to update UserExamMapping with noOfAttempts
					Map<String, Object> userExamMappingNoOfAttemptsResult = dispatcher
							.runSync("updateUserExamMappingnoOfAttempts",
									UtilMisc.toMap(ConstantValues.EXAM_ID, examId,
											ConstantValues.USEREXAM_NO_OF_ATTEMPTS, noOfAttempt,
											ConstantValues.USEREXAM_PARTY_ID, partyId));

					// Check if the service call resulted in an error
					if (ServiceUtil.isError(userExamMappingNoOfAttemptsResult)) {
						String errorMessage = ServiceUtil
								.getErrorMessage(userExamMappingNoOfAttemptsResult);
						request.setAttribute("ERROR_MESSAGE", errorMessage);
						Debug.logError(errorMessage, MODULE_NAME);
						return "error";
					} else {
						// Handle success scenario
						String successMessage = "Update User Exam Mapping No Of Attempts successful.";
						ServiceUtil.getMessages(request, userExamMappingNoOfAttemptsResult, successMessage);
						request.setAttribute("EVENT_MESSAGE", successMessage);
					}

					// Call service to get question information
					Map<String, Object> questionInformationResult = dispatcher.runSync("getQuestionInformation",
							UtilMisc.toMap(ConstantValues.EXAM_ID, examId, "request", request));

					// Check if the service call resulted in an error
					if (ServiceUtil.isError(questionInformationResult)) {
						String errorMessage = ServiceUtil.getErrorMessage(questionInformationResult);
						request.setAttribute("ERROR_MESSAGE", errorMessage);
						Debug.logError(errorMessage, MODULE_NAME);
						return "error";
					} else {
						// Handle success scenario
						String successMessage = "Get Question Information successful.";
						ServiceUtil.getMessages(request, questionInformationResult, successMessage);
						request.setAttribute("question", questionInformationResult);
					}
				}

				// Retrieve selectedQuestions from the session
				@SuppressWarnings("unchecked")
				List<List<GenericValue>> questionsInfoList = (List<List<GenericValue>>) request.getSession()
						.getAttribute("selectedQuestions");

				// Process each question in selectedQuestions
				for (List<GenericValue> questions : questionsInfoList) {
					for (GenericValue oneQuestion : questions) {
						// Retrieve questionId from the GenericValue
						String questionId = oneQuestion.getString(ConstantValues.QUES_ID);

						// Call service to create a UserAttemptAnswerMaster record
						Map<String, Object> userAttemptAnswerMasterResult = dispatcher
								.runSync("createUserAttemptAnswerMaster",
										UtilMisc.toMap(ConstantValues.QUES_ID, questionId,
												ConstantValues.USER_ANSWER_PERFORMANCE_ID, performanceId, "sequenceNum",
												sequenceNum));

						// Increment sequenceNum
						++sequenceNum;
						if (!ServiceUtil.isSuccess(userAttemptAnswerMasterResult)) {
							String errMsg = "User Attempt Answer Master not created successfully.";
							request.setAttribute("ERROR_MESSAGE", errMsg);
							return "error";
						}
					}
				}

				// Query UserAttemptAnswerMaster for the given performanceId
				List<GenericValue> userAttemptAnswerMasterList = EntityQuery.use(delegator)
						.from("UserAttemptAnswerMaster")
						.where(ConstantValues.USER_ANSWER_PERFORMANCE_ID, performanceId).queryList();
				if (UtilValidate.isEmpty(userAttemptAnswerMasterList)) {
					String errMsg = "User Attempt Answer Master is empty.";
					request.setAttribute("ERROR_MESSAGE", errMsg);
					return "error";
				}

				// Set questionSequence in the request
				request.setAttribute("userAttemptAnswerMaster", userAttemptAnswerMasterList);
			}
		} catch (Exception e) {
			// Handle any exceptions that may occur
			String errMsg = "Error in calling or executing the service: " + e.toString();
			request.setAttribute("ERROR_MESSAGE", errMsg);
			return "error";
		}

		// Set success message in the request
		request.setAttribute("EVENT_MESSAGE", "Get Question Information successful.");
		request.getSession().setAttribute("performanceId",performanceId);
		return "success";
	}
}
