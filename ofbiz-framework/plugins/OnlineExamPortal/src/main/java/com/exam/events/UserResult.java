package com.exam.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

public class UserResult {
	public static final String MODULE = UserResult.class.getName();
	private static final String RES_ERR = "OnlineExamPortalUiLabels";

	public static String getUserResult(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute(EntityConstants.DELEGATOR);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute(EntityConstants.DISPATCHER);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute(EntityConstants.USER_LOGIN);
		String performanceId = (String) request.getSession().getAttribute(ConstantValues.USER_ANSWER_PERFORMANCE_ID);
		System.out.println("performanceId------->" + performanceId);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> answer = (List<Map<String, Object>>) request.getAttribute("selectionAnswerResult");
		int isFlagged = 0;
		String questionId = null;
		String selectedAnswer = null;
		try {
			for (Map<String, Object> oneAns : answer) {
				Integer id = (Integer) oneAns.get("questionId");
				questionId = String.valueOf(id);
				selectedAnswer = (String) oneAns.get("answer");

			}
			Map<String, Object> userAttemptTopicMasterResult = dispatcher.runSync("updateUserAttemptAnswerMaster",
					UtilMisc.toMap(ConstantValues.QUES_ID, questionId, ConstantValues.USER_TOPIC_PERFORMANCE_ID,
							performanceId, ConstantValues.USER_ANSWER_SUBMITTED, selectedAnswer,
							ConstantValues.USER_ANSWER_FLAGGED, isFlagged, EntityConstants.USER_LOGIN, userLogin));
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
			
			List<GenericValue> userAttemptAnswerMasterList = EntityQuery.use(delegator)
					.from("UserAttemptAnswerMaster").where(ConstantValues.USER_ANSWER_PERFORMANCE_ID, performanceId)
					.queryList();
			if (UtilValidate.isEmpty(userAttemptAnswerMasterList)) {
				String errMsg = "UserAttemptAnswerMaster"
						+ UtilProperties.getMessage(RES_ERR, "EmptyVariableMessage", UtilHttp.getLocale(request));
				;
				request.setAttribute("ERROR_MESSAGE", errMsg);
				return "error";
			}
			for (GenericValue oneUserAttempt : userAttemptAnswerMasterList) {
				String userQuestionId = oneUserAttempt.getString(ConstantValues.QUES_ID);
				String submitAnswer=oneUserAttempt.getString(ConstantValues.USER_ANSWER_SUBMITTED);
				List<GenericValue> question = EntityQuery.use(delegator).from("QuestionMaster")
						.where(ConstantValues.QUES_ID, userQuestionId).queryList();
				for(GenericValue oneques:question) {
					String answerValue=oneques.getString(ConstantValues.QUES_ANSWER);
					
					if(submitAnswer.equals(answerValue)) {
						String topicId=oneques.getString(ConstantValues.TOPIC_ID);
						System.out.println("topicId if====="+topicId);
					}
					else {
						String topicId=oneques.getString(ConstantValues.TOPIC_ID);
						System.out.println("topicId else====="+topicId);
					}
				}
				
			}
			
		} catch (Exception e) {

			Debug.log(e);
		}
		return null;
	}
}
