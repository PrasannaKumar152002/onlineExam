package com.exam.events;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import com.exam.util.ConstantValues;
import com.exam.util.EntityConstants;

public class EnumerationEntityListEvent {
	private static final String MODULE = EnumerationEntityListEvent.class.getName();
	private static final String RES_ERR = "OnlineExamPortalUiLabels";
	// Retrieve All Values From Enumeration Entity
	public static String fetchEnumerationEntity(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute(EntityConstants.DELEGATOR);
		List<Map<String, Object>> enumerationData = new ArrayList<Map<String, Object>>();
		try {
			// Query to retrieve data's from Enumeration Entity
			List<GenericValue> listOfEnumerationData = EntityQuery.use(delegator).from(EntityConstants.ENUMERTION)
					.where(ConstantValues.ENUM_TYPE_ID, "Q_TYPE").cache().queryList();
			if (UtilValidate.isNotEmpty(listOfEnumerationData)) {
				for (GenericValue oneEnumerationData : listOfEnumerationData) {
					Map<String, Object> EnumerationEntityDatas = new HashMap<String, Object>();
					EnumerationEntityDatas.put(ConstantValues.ENUM_ID, oneEnumerationData.get(ConstantValues.ENUM_ID));
					EnumerationEntityDatas.put(ConstantValues.ENUM_SEQUENCE_ID,
							oneEnumerationData.get(ConstantValues.ENUM_SEQUENCE_ID));
					EnumerationEntityDatas.put(ConstantValues.ENUM_TYPE_ID, oneEnumerationData.get(ConstantValues.ENUM_TYPE_ID));
					EnumerationEntityDatas.put(ConstantValues.ENUM_DESCRIPTION,
							oneEnumerationData.get(ConstantValues.ENUM_DESCRIPTION));
					enumerationData.add(EnumerationEntityDatas);
				}
				request.setAttribute("EnumerationDatas", enumerationData);
				return "success";
			} else {
				String errorMessage = UtilProperties.getMessage(RES_ERR, "EnumerationEntityRecordNotFoundError", UtilHttp.getLocale(request));//"No records available for the field enumId=\"Q_TYPE\" in Enumeration Entity";
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				Debug.logError(errorMessage, MODULE);
				return "error";
			}
		} catch (GenericEntityException e) {
			request.setAttribute("Error", e);
			return "error";
		}
	}
}
