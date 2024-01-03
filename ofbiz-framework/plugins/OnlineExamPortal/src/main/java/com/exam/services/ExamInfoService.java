package com.exam.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import com.exam.util.ConstantValues;
import com.exam.util.EntityConstants;

public class ExamInfoService {
    public static final String module = ExamInfoService.class.getName();
    public static final Map<String,Object> emptyMap=UtilMisc.toMap("status","error");
    // Service method to get exam information based on user login ID
    public static Map<String, Object> getExamInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
        // Retrieve user ID from the context
        String userid = ((GenericValue)context.get("userLogin")).getString(EntityConstants.USER_LOGIN_ID);

        // Check if user ID is empty
        if (UtilValidate.isEmpty(userid)) {
            String errMsg = "Userid is required fields on the form and can't be empty.";
            Debug.log(errMsg);
            return emptyMap; // Return early if user ID is empty
        }

        Delegator delegator = (Delegator) dctx.getDelegator();

        try {
            // Get partyId from UserLogin entity
            String partyid = ((GenericValue)context.get("userLogin")).getString(ConstantValues.PARTY_ID);

            // Check if partyId is empty
            if (UtilValidate.isEmpty(partyid)) {
                String errMsg = "Inside UserLogin entity partyId is empty.";
                Debug.log(errMsg);
                return emptyMap; // Return early if partyId is empty
            }

            // Query UserExamMapping entity
            List<GenericValue> getuserExamMap = EntityQuery.use(delegator).from("UserExamMapping")
                    .where(ConstantValues.PARTY_ID, partyid).queryList();

            // Check if UserExamMapping entity is empty
            if (UtilValidate.isEmpty(getuserExamMap)) {
                String errMsg = "UserExamMapping entity is empty.";
                Debug.log(errMsg);
                return emptyMap; // Return early if UserExamMapping entity is empty
            }
            List<Map<String, Object>> listExam = new ArrayList<>();
            // Process each UserExamMapping
            for (GenericValue getvalueuserExamMap : getuserExamMap) {
                String examid = getvalueuserExamMap.getString(ConstantValues.EXAM_ID);

                // Check if examid is empty
                if (UtilValidate.isEmpty(examid)) {
                    String errMsg = "Inside UserExamMapping entity examid is empty.";
                    Debug.log(errMsg);
                    return emptyMap; // Return early if examid is empty
                }

                // Query ExamMaster entity
                GenericValue getExam = EntityQuery.use(delegator).from("ExamMaster").where(ConstantValues.EXAM_ID, examid).queryOne();

                // Check if ExamMaster entity is empty
                if (UtilValidate.isEmpty(getExam)) {
                    String errMsg = "ExamMaster entity is empty.";
                    Debug.log(errMsg);
                    return emptyMap; // Return early if ExamMaster entity is empty
                }

                // Create a map to store exam details
                Map<String, Object> examDetailsMap = new HashMap<>();

                // Process each attribute and add it to the map
                examDetailsMap.put("examId", examid);

                // Exam Name
                String examName = (String) getExam.getString(ConstantValues.EXAM_NAME);
                if (UtilValidate.isEmpty(examName)) {
                    String errMsg = "examName is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("examName", examName);

                // Description
                String description = (String) getExam.getString(ConstantValues.EXAM_DESCRIPTION);
                if (UtilValidate.isEmpty(description)) {
                    String errMsg = "description is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("description", description);

                // Creation Date
                String creationDate = (String) getExam.getString(ConstantValues.EXAM_CREATION_DATE);
                if (UtilValidate.isEmpty(creationDate)) {
                    String errMsg = "creationDate is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("creationDate", creationDate);

                // Expiration Date
                String expirationDate = (String) getExam.getString(ConstantValues.EXAM_EXPIRATION_DATE);
                if (UtilValidate.isEmpty(expirationDate)) {
                    String errMsg = "expirationDate is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("expirationDate", expirationDate);

                // No Of Questions
                String noOfQuestions = (String) getExam.getString(ConstantValues.EXAM_TOTAL_QUES);
                if (UtilValidate.isEmpty(noOfQuestions)) {
                    String errMsg = "noOfQuestions is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("noOfQuestions", noOfQuestions);

                // Duration Minutes
                String durationMinutes = (String) getExam.getString(ConstantValues.EXAM_DURATION);
                if (UtilValidate.isEmpty(durationMinutes)) {
                    String errMsg = "durationMinutes is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("durationMinutes", durationMinutes);

                // Pass Percentage
                String passPercentage = (String) getExam.getString(ConstantValues.EXAM_PASS_PERCENTAGE);
                if (UtilValidate.isEmpty(passPercentage)) {
                    String errMsg = "passPercentage is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("passPercentage", passPercentage);

                // Questions Randomized
                String questionsRandomized = (String) getExam.getString(ConstantValues.EXAM_QUES_RANDOMIZED);
                if (UtilValidate.isEmpty(questionsRandomized)) {
                    String errMsg = "questionsRandomized is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("questionsRandomized", questionsRandomized);

                // Answers Must
                String answersMust = (String) getExam.getString(ConstantValues.EXAM_ANS_MUST);
                if (UtilValidate.isEmpty(answersMust)) {
                    String errMsg = "answersMust is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("answersMust", answersMust);

                // Enable Negative Mark
                String enableNegativeMark = (String) getExam.getString(ConstantValues.EXAM_ENABLE_NEG_MARK);
                if (UtilValidate.isEmpty(enableNegativeMark)) {
                    String errMsg = "enableNegativeMark is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("enableNegativeMark", enableNegativeMark);

                // Negative Mark Value
                String negativeMarkValue = (String) getExam.getString(ConstantValues.EXAM_NEG_MARK);
                if (UtilValidate.isEmpty(negativeMarkValue)) {
                    String errMsg = "negativeMarkValue is emptyMap.";
                    Debug.log(errMsg);
                }
                examDetailsMap.put("negativeMarkValue", negativeMarkValue);


                //  list to store the exam details map
             
                listExam.add(examDetailsMap);

               
            }
            // Create a result map and return it
            Map<String, Object> result = new HashMap<>();
            result.put("exam", listExam);
            return result;

        } catch (Exception e) {
            // Log any exceptions that may occur
            Debug.logError(e, module);
        }

        return emptyMap; // Return emptyMap if there is an exception
    }
}