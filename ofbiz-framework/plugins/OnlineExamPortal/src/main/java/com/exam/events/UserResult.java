package com.exam.events;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.lucene.analysis.CharArrayMap.EntrySet;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.exam.util.ConstantValues;
import com.exam.util.EntityConstants;

import net.lingala.zip4j.headers.VersionMadeBy;

public class UserResult {
	public static final String MODULE = UserResult.class.getName();
	private static final String RES_ERR = "OnlineExamPortalUiLabels";

	public static String getUserResult(HttpServletRequest request, HttpServletResponse response) {

		Debug.log("inside......userresult.....");
		Delegator delegator = (Delegator) request.getAttribute(EntityConstants.DELEGATOR);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute(EntityConstants.DISPATCHER);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute(EntityConstants.USER_LOGIN);
		String performanceId = (String) request.getSession().getAttribute(ConstantValues.USER_ANSWER_PERFORMANCE_ID);
		String examId = (String) request.getAttribute(ConstantValues.EXAM_ID);

		List<Map<String, Object>> answer = (List<Map<String, Object>>) request.getAttribute("selectionAnswerResult");

		List<List<Map<String, Object>>> questions = (List<List<Map<String, Object>>>) request.getAttribute("questions");
		System.out.println("user result///////////////////////////////////////////////////////////////////////////");
		System.out.println("questions-" + questions + "answer-" + answer);
		int correctAnswerCount = 0, correctAnswerMark = 0, wrongAnswerCount = 0, wrongAnswerMark = 0,
				totalExamMarks = 0;
		;
		Map<String, Integer> topic = new HashMap<>();
		// key===topicID===2===5
		// VALUE===ANSWER ==3
		try {
			List<GenericValue> examTopicMapping = EntityQuery.use(delegator).from("ExamTopicMapping")
					.where(ConstantValues.EXAM_ID, examId).queryList();
			for (GenericValue getTopic : examTopicMapping) {
				String topicId = getTopic.getString(ConstantValues.TOPIC_ID);
				topic.put(topicId, 0);
			}
			for (Map<String, Object> oneAnswer : answer) {

				for (List<Map<String, Object>> questionList : questions) {
					for (Map<String, Object> oneQuestion : questionList) {

						if (oneAnswer.get("questionId").toString().equals(oneQuestion.get("questionId").toString())) {
							Integer questionIdInt = (Integer) oneAnswer.get("questionId");
							String questionId = String.valueOf(questionIdInt);
							String selectedAnswer = oneAnswer.get("answer").toString();
							int isFlagged = 0;
							Map<String, Object> userAttemptAnswerMasterResult = dispatcher.runSync(
									"updateUserAttemptAnswerMaster",
									UtilMisc.toMap(ConstantValues.QUES_ID, questionId,
											ConstantValues.USER_TOPIC_PERFORMANCE_ID, performanceId,
											ConstantValues.USER_ANSWER_SUBMITTED, selectedAnswer,
											ConstantValues.USER_ANSWER_FLAGGED, isFlagged, EntityConstants.USER_LOGIN,
											userLogin));
							if (ServiceUtil.isError(userAttemptAnswerMasterResult)) {
								String errorMessage = ServiceUtil.getErrorMessage(userAttemptAnswerMasterResult);
								request.setAttribute("ERROR_MESSAGE", errorMessage);
								Debug.logError(errorMessage, MODULE);
								return "error";
							} else {
								// Handle success scenario
								String successMessage = UtilProperties.getMessage(RES_ERR, "ServiceSuccessMessage",
										UtilHttp.getLocale(request));
								ServiceUtil.getMessages(request, userAttemptAnswerMasterResult, successMessage);

							}

							String currentQuestionTopicId = oneQuestion.get("topicId").toString();
							totalExamMarks += (Integer) (oneQuestion.get("answerValue"));
							if (oneAnswer.get("answer").toString().equals(oneQuestion.get("answer"))) {

								correctAnswerCount += 1;
								correctAnswerMark += (Integer) (oneQuestion.get("answerValue"));
								int topicMark = topic.get(currentQuestionTopicId)
										+ Integer.parseInt(oneQuestion.get("answerValue").toString());
								topic.put(currentQuestionTopicId, topicMark);
							} else {
								wrongAnswerCount += 1;
								wrongAnswerMark += Integer.parseInt(oneQuestion.get("negativeMarkValue").toString());
								int topicMark = topic.get(currentQuestionTopicId)
										+ Integer.parseInt(oneQuestion.get("negativeMarkValue").toString());
								topic.put(currentQuestionTopicId, topicMark);
							}

						}
					}
				}
			}
			if (!UtilValidate.isEmpty(topic)) {
				for (Entry<String, Integer> getTopic : topic.entrySet()) {
					String topicId = getTopic.getKey();

					List<GenericValue> userAttemptTopicMaster = EntityQuery.use(delegator)
							.from("UserAttemptTopicMaster").where(ConstantValues.TOPIC_ID, topicId).queryList();
					for (GenericValue oneUserTopicInformation : userAttemptTopicMaster) {
						Integer totalQuestionsInThisTopic = (Integer) oneUserTopicInformation
								.get("totalQuestionsInThisTopic");

						Integer correctQuestionsInThisTopic = getTopic.getValue();
						String correctQuestionsTopic = String.valueOf(correctQuestionsInThisTopic);
						Integer topicPassPercentage = (Integer) oneUserTopicInformation.get("topicPassPercentage");
						Integer userTopicPercentage = ((correctQuestionsInThisTopic / totalQuestionsInThisTopic) * 100);
						String userPassedThisTopic = "N";
						if (userTopicPercentage >= topicPassPercentage) {
							userPassedThisTopic = "Y";
						}
						Map<String, Object> userAttemptTopicMasterResult = dispatcher.runSync(
								"updateUserAttemptTopicMaster",
								UtilMisc.toMap(ConstantValues.USER_TOPIC_PERFORMANCE_ID, performanceId,
										ConstantValues.TOPIC_ID, topicId, ConstantValues.USER_TOPIC_CRCT_QUES,
										correctQuestionsTopic, ConstantValues.USER_TOPIC_PERCENTAGE,
										userTopicPercentage, ConstantValues.USER_TOPIC_PASSED, userPassedThisTopic,
										EntityConstants.USER_LOGIN, userLogin));

						if (ServiceUtil.isError(userAttemptTopicMasterResult)) {
							String errorMessage = ServiceUtil.getErrorMessage(userAttemptTopicMasterResult);
							request.setAttribute("ERROR_MESSAGE", errorMessage);
							Debug.logError(errorMessage, MODULE);
							return "error";
						} else {
							// Handle success scenario
							String successMessage = UtilProperties.getMessage(RES_ERR, "ServiceSuccessMessage",
									UtilHttp.getLocale(request));
							ServiceUtil.getMessages(request, userAttemptTopicMasterResult, successMessage);

						}

					}

				}
			}
			List<GenericValue> examMasterResult = EntityQuery.use(delegator).from("ExamMaster")
					.where(ConstantValues.EXAM_ID, examId).queryList();

			for (GenericValue oneExamInfo : examMasterResult) {
				int totalMarks = correctAnswerMark + wrongAnswerMark;
				Integer userMarkPercentage = (correctAnswerMark / totalMarks) * 100;
				Integer examPassPercentage = (Integer) oneExamInfo.get(ConstantValues.EXAM_PASS_PERCENTAGE);
				String userPassed = "N";
				if (userMarkPercentage > examPassPercentage) {
					userPassed = "Y";
				}
				String completedDate = LocalDateTime.now().toString();
				Map<String, Object> userAttemptMasterResult = dispatcher.runSync("UserAttemptMaster",
						UtilMisc.toMap(ConstantValues.USER_ATTEMPT_PERFORMANCE_ID, performanceId,
								ConstantValues.USER_ATTEMPT_SCORE, ConstantValues.USER_ANSWER_SUBMITTED,
								correctAnswerMark, ConstantValues.USER_ATTEMPT_COMPLETED_DATE, completedDate,
								ConstantValues.USER_ATTEMPT_TOTAL_CORRECT, correctAnswerCount,
								ConstantValues.USER_ATTEMPT_TOTAL_WRONG, wrongAnswerCount,
								ConstantValues.USER_ATTEMPT_PASSED, userPassed, EntityConstants.USER_LOGIN, userLogin));

				if (ServiceUtil.isError(userAttemptMasterResult)) {
					String errorMessage = ServiceUtil.getErrorMessage(userAttemptMasterResult);
					request.setAttribute("ERROR_MESSAGE", errorMessage);
					Debug.logError(errorMessage, MODULE);
					return "error";
				} else {
					// Handle success scenario
					String successMessage = UtilProperties.getMessage(RES_ERR, "ServiceSuccessMessage",
							UtilHttp.getLocale(request));
					ServiceUtil.getMessages(request, userAttemptMasterResult, successMessage);

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_", e);
			return "error";
		}

		request.setAttribute("success",
				UtilMisc.toMap("correctAnswerCount", correctAnswerCount, "correctAnswerMark", correctAnswerMark,
						"wrongAnswerCount", wrongAnswerCount, "wrongAnswerMark", wrongAnswerMark, "topic", topic));
		return "success";
	}
}
