package com.exam.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import com.exam.util.ConstantValues;
import com.exam.util.EntityConstants;

public class ExamInfoService {
	public static final String MODULE_NAME = ExamInfoService.class.getName();

	// Service method to get exam information based on user login ID
	public static Map<String, Object> getExamInfo(DispatchContext dispatchContext,
			Map<String, ? extends Object> context) {
		// Retrieve user ID from the context
		String userLoginId = ((GenericValue) context.get("userLogin")).getString(EntityConstants.USER_LOGIN_ID);

		// Check if user ID is empty
		if (UtilValidate.isEmpty(userLoginId)) {
			String errMsg = "User ID is required fields on the form and can't be empty.";
			Debug.log(errMsg);
			return null; // Return early if user ID is empty
		}

		Delegator delegator = dispatchContext.getDelegator();

		try {
			// Query UserLogin entity
			GenericValue userLoginEntity = EntityQuery.use(delegator).from("UserLogin")
					.where(EntityConstants.USER_LOGIN_ID, userLoginId).cache().queryOne();

			// Check if UserLogin entity is empty
			if (UtilValidate.isEmpty(userLoginEntity)) {
				String errMsg = "UserLogin entity is empty.";
				Debug.log(errMsg);
				return null; // Return early if UserLogin entity is empty
			}

			// Get partyId from UserLogin entity
			String partyId = userLoginEntity.getString(ConstantValues.USEREXAM_PARTY_ID);

			// Check if partyId is empty
			if (UtilValidate.isEmpty(partyId)) {
				String errMsg = "Inside UserLogin entity partyId is empty.";
				Debug.log(errMsg);
				return null; // Return early if partyId is empty
			}

			// Query UserExamMapping entity
			List<GenericValue> userExamMappingList = EntityQuery.use(delegator).from("UserExamMapping")
					.where(ConstantValues.USEREXAM_PARTY_ID, partyId).queryList();

			// Check if UserExamMapping entity is empty
			if (UtilValidate.isEmpty(userExamMappingList)) {
				String errMsg = "UserExamMapping entity is empty.";
				Debug.log(errMsg);
				return null; // Return early if UserExamMapping entity is empty
			}

			List<Map<String, Object>> examList = new ArrayList<>();
			// Process each UserExamMapping
			for (GenericValue userExamMapping : userExamMappingList) {
				String examId = userExamMapping.getString(ConstantValues.EXAM_ID);

				// Check if examId is empty
				if (UtilValidate.isEmpty(examId)) {
					String errMsg = "Inside UserExamMapping entity examId is empty.";
					Debug.log(errMsg);
					return null; // Return early if examId is empty
				}

				// Query ExamMaster entity
				GenericValue examMasterEntity = EntityQuery.use(delegator).from("ExamMaster")
						.where(ConstantValues.EXAM_ID, examId).queryOne();

				// Check if ExamMaster entity is empty
				if (UtilValidate.isEmpty(examMasterEntity)) {
					String errMsg = "ExamMaster entity is empty.";
					Debug.log(errMsg);
					return null; // Return early if ExamMaster entity is empty
				}

				// Create a map to store exam details
				Map<String, Object> examDetailsMap = new HashMap<>();

				examDetailsMap.put("examId", examId);

				// Exam Name
				String examName = (String) examMasterEntity.getString(ConstantValues.EXAM_NAME);
				if (UtilValidate.isEmpty(examName)) {
					String errMsg = "Exam Name is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("examName", examName);

				// Description
				String description = (String) examMasterEntity.getString(ConstantValues.EXAM_DESCRIPTION);
				if (UtilValidate.isEmpty(description)) {
					String errMsg = "Description is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("description", description);

				// Creation Date
				String creationDate = (String) examMasterEntity.getString(ConstantValues.EXAM_CREATION_DATE);
				if (UtilValidate.isEmpty(creationDate)) {
					String errMsg = "Creation Date is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("creationDate", creationDate);

				// Expiration Date
				String expirationDate = (String) examMasterEntity.getString(ConstantValues.EXAM_EXPIRATION_DATE);
				if (UtilValidate.isEmpty(expirationDate)) {
					String errMsg = "Expiration Date is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("expirationDate", expirationDate);

				// No Of Questions
				String noOfQuestions = (String) examMasterEntity.getString(ConstantValues.EXAM_TOTAL_QUES);
				if (UtilValidate.isEmpty(noOfQuestions)) {
					String errMsg = "Number of Questions is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("noOfQuestions", noOfQuestions);

				// Duration Minutes
				String durationMinutes = (String) examMasterEntity.getString(ConstantValues.EXAM_DURATION);
				if (UtilValidate.isEmpty(durationMinutes)) {
					String errMsg = "Duration Minutes is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("durationMinutes", durationMinutes);

				// Pass Percentage
				String passPercentage = (String) examMasterEntity.getString(ConstantValues.EXAM_PASS_PERCENTAGE);
				if (UtilValidate.isEmpty(passPercentage)) {
					String errMsg = "Pass Percentage is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("passPercentage", passPercentage);

				// Questions Randomized
				String questionsRandomized = (String) examMasterEntity.getString(ConstantValues.EXAM_QUES_RANDOMIZED);
				if (UtilValidate.isEmpty(questionsRandomized)) {
					String errMsg = "Questions Randomized is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("questionsRandomized", questionsRandomized);

				// Answers Must
				String answersMust = (String) examMasterEntity.getString(ConstantValues.EXAM_ANS_MUST);
				if (UtilValidate.isEmpty(answersMust)) {
					String errMsg = "Answers Must is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("answersMust", answersMust);

				// Enable Negative Mark
				String enableNegativeMark = (String) examMasterEntity.getString(ConstantValues.EXAM_ENABLE_NEG_MARK);
				if (UtilValidate.isEmpty(enableNegativeMark)) {
					String errMsg = "Enable Negative Mark is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("enableNegativeMark", enableNegativeMark);

				// Negative Mark Value
				String negativeMarkValue = (String) examMasterEntity.getString(ConstantValues.EXAM_NEG_MARK);
				if (UtilValidate.isEmpty(negativeMarkValue)) {
					String errMsg = "Negative Mark Value is null.";
					Debug.log(errMsg);
				}
				examDetailsMap.put("negativeMarkValue", negativeMarkValue);

				// Add the exam details map to the list
				examList.add(examDetailsMap);
			}

			// Create a result map and return it
			Map<String, Object> result = new HashMap<>();
			result.put("examList", examList);
			return result;

		} catch (Exception e) {
			// Log any exceptions that may occur
			Debug.logError(e, MODULE_NAME);
			Map<String, Object> errorMap = new HashMap<>();
			return errorMap;
		}

	}
}
