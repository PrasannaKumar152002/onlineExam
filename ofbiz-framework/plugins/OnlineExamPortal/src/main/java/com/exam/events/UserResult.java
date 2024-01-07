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
		String examId = (String) request.getAttribute(ConstantValues.EXAM_ID);

		List<Map<String, Object>> answer = (List<Map<String, Object>>) request.getAttribute("selectionAnswerResult");
		List<List<Map<String, Object>>> questions = (List<List<Map<String, Object>>>) request.getAttribute("questions");
		System.out.println("user result///////////////////////////////////////////////////////////////////////////");
		System.out.println("questions-" + questions + "answer-" + answer);
		int correctAnswerCount = 0, correctAnswerMark = 0, wrongAnswerCount = 0, wrongAnswerMark = 0;
		Map<String, Integer> topic = new HashMap<>();
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
							String currentQuestionTopicId = oneQuestion.get("topicId").toString();
							if (oneAnswer.get("answer").toString().equals(oneQuestion.get("answer"))) {
								correctAnswerCount += 1;
								correctAnswerMark += Integer.parseInt(oneQuestion.get("answerValue").toString());
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
		} catch (GenericEntityException e) {
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
