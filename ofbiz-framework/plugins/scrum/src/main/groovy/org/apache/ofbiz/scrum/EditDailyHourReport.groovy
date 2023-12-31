/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
package org.apache.ofbiz.scrum

import java.util.*
import java.lang.*
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilProperties
import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.entity.condition.EntityComparisonOperator
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.entity.util.EntityFindOptions
import org.apache.ofbiz.entity.util.EntityUtil
import org.apache.ofbiz.webapp.website.WebSiteWorker

uiLabelMap = UtilProperties.getResourceBundleMap("scrumUiLabels", locale)
partyId = parameters.partyId
if (!partyId) {
    partyId = parameters.userLogin.partyId
}

// show the requested timesheet, otherwise the current , if not exist create
timesheet = null
timesheetId = parameters.timesheetId
if (timesheetId) {
    timesheet = from("Timesheet").where("timesheetId", timesheetId).queryOne()
    partyId = timesheet.partyId // use the party from this timesheet
} else {
    // make sure because of timezone changes, not a duplicate timesheet is created
    midweek = UtilDateTime.addDaysToTimestamp(UtilDateTime.getWeekStart(UtilDateTime.nowTimestamp()),3)
    entryExprs = EntityCondition.makeCondition([
        EntityCondition.makeCondition("fromDate", EntityComparisonOperator.LESS_THAN, midweek),
        EntityCondition.makeCondition("thruDate", EntityComparisonOperator.GREATER_THAN, midweek),
        EntityCondition.makeCondition("partyId", EntityComparisonOperator.EQUALS, partyId)
        ], EntityOperator.AND)
    entryIterator = from("Timesheet").where(entryExprs).queryIterator()
    timesheet = entryIterator.next()
    entryIterator.close()
    if (timesheet == null) {
        result = runService('createProjectTimesheet', ["userLogin" : parameters.userLogin, "partyId" : partyId])
        if (result && result.timesheetId) {
            timesheet = from("Timesheet").where("timesheetId", result.timesheetId).queryOne()
        }
    }
}
if (!timesheet) return
context.timesheet = timesheet
context.weekNumber = UtilDateTime.weekNumber(timesheet.fromDate)

// get the user names
context.partyNameView = from("PartyNameView").where("partyId", partyId).queryOne()
// get the default rate for this person
rateTypes = from("PartyRate").where("partyId", partyId, "defaultRate", "Y").filterByDate().queryList()
if (rateTypes) {
    context.defaultRateTypeId = rateTypes[0].rateTypeId
}

entries = []
entry = ["timesheetId" : timesheet.timesheetId]
leaveEntry = ["timesheetId" : timesheet.timesheetId]
taskTotal = 0.00
planTotal = 0.00
leaveTaskTotal = 0.00
leavePlanTotal = 0.00
day0Total = 0.00; day1Total=0.00; day2Total=0.00; day3Total=0.00; day4Total=0.00; day5Total=0.00; day6Total=0.00
pDay0Total = 0.00; pDay1Total=0.00; pDay2Total=0.00; pDay3Total=0.00; pDay4Total=0.00; pDay5Total=0.00; pDay6Total=0.00
pHours = 0.00
timeEntry = null
lastTimeEntry = null
emplLeaveEntry = null
lastEmplLeaveEntry = null

// retrieve work effort data when the workeffortId has changed.
void retrieveWorkEffortData() {
        // get the planned number of hours
        entryWorkEffort = lastTimeEntry.getRelatedOne("WorkEffort", false)
        if (entryWorkEffort) {
            plannedHours = entryWorkEffort.getRelated("WorkEffortSkillStandard", null, null, false)
            pHours = 0.00
            plannedHours.each { plannedHour ->
                if (plannedHour.estimatedDuration) {
                    pHours += plannedHour.estimatedDuration
                }
            }
            estimatedHour =  0.00
            
            estimatedMilliSeconds = entryWorkEffort.estimatedMilliSeconds
            if (estimatedMilliSeconds > 0) 
                estimatedHour = estimatedMilliSeconds/3600000
            entry.plannedHours = estimatedHour
            //entry.plannedHours = pHours
            planHours = 0.0
            planHours = lastTimeEntry.planHours
            lastTimeEntryOfTasks = from("TimeEntry").where("workEffortId", lastTimeEntry.workEffortId, "partyId", partyId).orderBy("-fromDate").queryList()
            if (lastTimeEntryOfTasks.size() != 0) lastTimeEntry = lastTimeEntryOfTasks[0]
            if (planHours < 1) {
                planHours = estimatedHour
            }
            entry.planHours = lastTimeEntry.planHours
            actualHours = entryWorkEffort.getRelated("TimeEntry", null, null, false)
            aHours = 0.00
            actualHours.each { actualHour ->
                if (actualHour.hours) {
                    aHours += actualHour.hours
                }
            }
            entry.actualHours = aHours
            // get party assignment data to be able to set the task to complete
            workEffortPartyAssigns = EntityUtil.filterByDate(entryWorkEffort.getRelated("WorkEffortPartyAssignment", ["partyId" : partyId], null, false))
            if (workEffortPartyAssigns) {
                workEffortPartyAssign = workEffortPartyAssigns[0]
                entry.fromDate = workEffortPartyAssign.getTimestamp("fromDate")
                entry.roleTypeId = workEffortPartyAssign.roleTypeId
                if ("SCAS_COMPLETED".equals(workEffortPartyAssign.statusId)) {
                    entry.checkComplete = "Y"
                    
                }
            } else {
                if ("STS_COMPLETED".equals(entryWorkEffort.currentStatusId)) {
                    entry.checkComplete = "Y"
                }
            }

            // get project/phase information
            entry.workEffortId = entryWorkEffort.workEffortId
            entry.workEffortName = entryWorkEffort.workEffortName
            result = runService('getProjectInfoFromTask', ["userLogin" : parameters.userLogin,"taskId" : entryWorkEffort.workEffortId])
                entry.phaseId = result.phaseId
                entry.phaseName = result.phaseName
                entry.projectId = result.projectId
                entry.projectName = result.projectName
                entry.taskWbsId = result.taskWbsId

        }
        entry.acualTotal = taskTotal
        entry.planTotal = planTotal
        //Drop Down Lists
        if ("Y" != entry.checkComplete) {
            if (aHours > 0.00)
                entries.add(entry)
        } else {
                entries.add(entry)
        }
        // start new entry
        taskTotal = 0.00
        planTotal = 0.00
        entry = ["timesheetId" : timesheet.timesheetId]
}

timeEntries = timesheet.getRelated("TimeEntry", null, ["workEffortId", "rateTypeId", "fromDate"], false)
te = timeEntries.iterator()
while (te.hasNext()) {
    // only fill lastTimeEntry when not the first time
    if (timeEntry!=void) {
        lastTimeEntry = timeEntry
    }
    timeEntry = te.next()

    if (lastTimeEntry &&
            (!lastTimeEntry.workEffortId.equals(timeEntry.workEffortId) ||
            !lastTimeEntry.rateTypeId.equals(timeEntry.rateTypeId))) {
            retrieveWorkEffortData()
        }
    if (timeEntry.hours) {
        dayNumber = "d" + (timeEntry.fromDate.getTime() - timesheet.fromDate.getTime()) / (24*60*60*1000)
        hours = timeEntry.hours.doubleValue()
        entry.put(String.valueOf(dayNumber), hours)
        if ("d0".equals(dayNumber)) day0Total += hours
        if ("d1".equals(dayNumber)) day1Total += hours
        if ("d2".equals(dayNumber)) day2Total += hours
        if ("d3".equals(dayNumber)) day3Total += hours
        if ("d4".equals(dayNumber)) day4Total += hours
        if ("d5".equals(dayNumber)) day5Total += hours
        if ("d6".equals(dayNumber)) day6Total += hours
        taskTotal += hours
    }
    if (timeEntry.planHours) {
        dayNumber = "pd" + (timeEntry.fromDate.getTime() - timesheet.fromDate.getTime()) / (24*60*60*1000)
        planHours = timeEntry.planHours.doubleValue()
        entry.put(String.valueOf(dayNumber), planHours)
        if ("pd0".equals(dayNumber)) pDay0Total += planHours
        if ("pd1".equals(dayNumber)) pDay1Total += planHours
        if ("pd2".equals(dayNumber)) pDay2Total += planHours
        if ("pd3".equals(dayNumber)) pDay3Total += planHours
        if ("pd4".equals(dayNumber)) pDay4Total += planHours
        if ("pd5".equals(dayNumber)) pDay5Total += planHours
        if ("pd6".equals(dayNumber)) pDay6Total += planHours
        planTotal += planHours

    }
    entry.rateTypeId = timeEntry.rateTypeId
}
//retrieve Empl Leave data.
void retrieveEmplLeaveData() {
        if (lastEmplLeaveEntry) {
            //service get Hours
            result = runService('getPartyLeaveHoursForDate', 
                ["userLogin": parameters.userLogin, "partyId": lastEmplLeaveEntry.partyId, "leaveTypeId": lastEmplLeaveEntry.leaveTypeId, "fromDate": lastEmplLeaveEntry.fromDate])
            if (result.hours) {
                leaveEntry.plannedHours = result.hours
                leaveEntry.planHours =  result.hours
            }
            if ("LEAVE_APPROVED" == lastEmplLeaveEntry.leaveStatus) {
                leaveEntry.checkComplete = "Y"
            }
            leaveEntry.partyId = lastEmplLeaveEntry.partyId
            leaveEntry.leaveTypeId = lastEmplLeaveEntry.leaveTypeId
            leaveEntry.leavefromDate = lastEmplLeaveEntry.fromDate
            leaveEntry.leavethruDate = lastEmplLeaveEntry.thruDate
            leaveEntry.description = lastEmplLeaveEntry.description
        }
        leaveEntry.acualTotal = leaveTaskTotal
        leaveEntry.planHours = leavePlanTotal
        leaveEntry.actualHours = leaveTaskTotal
        //Drop Down Lists
        entries.add(leaveEntry)
        // start new leaveEntry
        leaveTaskTotal = 0.00
        leavePlanTotal = 0.00
        leaveEntry = ["timesheetId" : timesheet.timesheetId]
   }

// define condition
leaveExprs = []
leaveExprs.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, timesheet.fromDate))
leaveExprs.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timesheet.thruDate))
leaveExprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId))
emplLeave = from("EmplLeave").where(leaveExprs).cursorScrollInsensitive().distinct().queryIterator()

while ((emplLeaveMap = emplLeave.next())) {
    if (emplLeaveEntry!=void) {
        lastEmplLeaveEntry = emplLeaveEntry
    }
    
    emplLeaveEntry = emplLeaveMap
    
    if (lastEmplLeaveEntry && (
            !lastEmplLeaveEntry.leaveTypeId.equals(emplLeaveEntry.leaveTypeId) ||
            !lastEmplLeaveEntry.partyId.equals(emplLeaveEntry.partyId))) {
            retrieveEmplLeaveData()
        }
    resultHours = runService('getPartyLeaveHoursForDate', 
        ["userLogin": parameters.userLogin, "partyId": emplLeaveEntry.partyId, "leaveTypeId": emplLeaveEntry.leaveTypeId, "fromDate": emplLeaveEntry.fromDate])
    
    if (resultHours.hours) {
        leaveDayNumber = "d" + (emplLeaveEntry.fromDate.getTime() - timesheet.fromDate.getTime()) / (24*60*60*1000)
        resultHours = runService('getPartyLeaveHoursForDate', 
            ["userLogin": parameters.userLogin, "partyId": emplLeaveEntry.partyId, "leaveTypeId": emplLeaveEntry.leaveTypeId, "fromDate": emplLeaveEntry.fromDate])
        leaveHours = resultHours.hours.doubleValue()
        leaveEntry.put(String.valueOf(leaveDayNumber), leaveHours)
        if ("d0".equals(leaveDayNumber)) day0Total += leaveHours
        if ("d1".equals(leaveDayNumber)) day1Total += leaveHours
        if ("d2".equals(leaveDayNumber)) day2Total += leaveHours
        if ("d3".equals(leaveDayNumber)) day3Total += leaveHours
        if ("d4".equals(leaveDayNumber)) day4Total += leaveHours
        if ("d5".equals(leaveDayNumber)) day5Total += leaveHours
        if ("d6".equals(leaveDayNumber)) day6Total += leaveHours
        leaveTaskTotal += leaveHours
    }
    if (resultHours.hours) {
        leavePlanDay = "pd" + (emplLeaveEntry.fromDate.getTime() - timesheet.fromDate.getTime()) / (24*60*60*1000)
        resultPlanHours = runService('getPartyLeaveHoursForDate', 
            ["userLogin": parameters.userLogin, "partyId": emplLeaveEntry.partyId, "leaveTypeId": emplLeaveEntry.leaveTypeId, "fromDate": emplLeaveEntry.fromDate])
        leavePlanHours = resultPlanHours.hours.doubleValue()
        leaveEntry.put(String.valueOf(leavePlanDay), leavePlanHours)
        if ("pd0".equals(leavePlanDay)) pDay0Total += leavePlanHours
        if ("pd1".equals(leavePlanDay)) pDay1Total += leavePlanHours
        if ("pd2".equals(leavePlanDay)) pDay2Total += leavePlanHours
        if ("pd3".equals(leavePlanDay)) pDay3Total += leavePlanHours
        if ("pd4".equals(leavePlanDay)) pDay4Total += leavePlanHours
        if ("pd5".equals(leavePlanDay)) pDay5Total += leavePlanHours
        if ("pd6".equals(leavePlanDay)) pDay6Total += leavePlanHours
        leavePlanTotal += leavePlanHours
    }
    leaveEntry.rateTypeId = "STANDARD"
}
emplLeave.close()

if (timeEntry) {
    lastTimeEntry = timeEntry
    retrieveWorkEffortData()
    }
if (emplLeaveEntry) {
    lastEmplLeaveEntry = emplLeaveEntry
    retrieveEmplLeaveData()
    }

// add empty lines if timesheet not completed
if (!"TIMESHEET_COMPLETED".equals(timesheet.statusId)) {
    for (c=0; c < 3; c++) { // add empty lines
        entries.add(["timesheetId" : timesheet.timesheetId])
    }
}

// add the totals line if at least one entry
if (timeEntry || emplLeaveEntry) {
    entry = ["timesheetId" : timesheet.timesheetId]
    entry.d0 = day0Total
    entry.d1 = day1Total
    entry.d2 = day2Total
    entry.d3 = day3Total
    entry.d4 = day4Total
    entry.d5 = day5Total
    entry.d6 = day6Total
    entry.pd0 = pDay0Total
    entry.pd1 = pDay1Total
    entry.pd2 = pDay2Total
    entry.pd3 = pDay3Total
    entry.pd4 = pDay4Total
    entry.pd5 = pDay5Total
    entry.pd6 = pDay6Total
    entry.phaseName = uiLabelMap.ScrumTotals
    entry.workEffortId = "Totals"
    entry.total = day0Total + day1Total + day2Total + day3Total + day4Total + day5Total + day6Total
    entries.add(entry)
}
context.timeEntries = entries
// get all timesheets of this user, including the planned hours
timesheetsDb = from("Timesheet").where("partyId", partyId).orderBy("fromDate DESC").queryList()
timesheets = new LinkedList()
timesheetsDb.each { timesheetDb ->
    //get hours from EmplLeave
    findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true)
    leaveExprsList = []
    leaveExprsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, timesheetDb.fromDate))
    leaveExprsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timesheetDb.thruDate))
    leaveExprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId))
    emplLeaveList = from("EmplLeave").where(leaveExprsList).cursorScrollInsensitive().distinct().queryIterator()
    leaveHours = 0.00
    
    while ((emplLeaveMap = emplLeaveList.next())) {
        emplLeaveEntry = emplLeaveMap
        resultHour = runService('getPartyLeaveHoursForDate', 
            ["userLogin": parameters.userLogin, "partyId": emplLeaveEntry.partyId, "leaveTypeId": emplLeaveEntry.leaveTypeId, "fromDate": emplLeaveEntry.fromDate])
        if (resultHour) {
            leaveActualHours = resultHour.hours.doubleValue()
            leaveHours += leaveActualHours
        }
    }
    //get hours from TimeEntry
    timesheet = [:]
    timesheet.putAll(timesheetDb)
    entries = timesheetDb.getRelated("TimeEntry", null, null, false)
    hours = 0.00
    entries.each { timeEntry ->
        if (timeEntry.hours) {
            hours += timeEntry.hours.doubleValue()
        }
    }
    timesheet.weekNumber = UtilDateTime.weekNumber(timesheetDb.fromDate)
    timesheet.hours = hours + leaveHours
    timesheets.add(timesheet)
    emplLeaveList.close()
}
context.timesheets = timesheets

// get existing task that no assign
taskList=[]
projectSprintBacklogAndTaskList = []
backlogIndexList = []
projectAndTaskList = from("ProjectSprintBacklogAndTask").where("sprintTypeId" : "SCRUM_SPRINT","taskCurrentStatusId" : "STS_CREATED").orderBy("projectName ASC","taskActualStartDate DESC").queryList()
projectAndTaskList.each { projectAndTaskMap ->
userLoginId = userLogin.partyId
    sprintId = projectAndTaskMap.sprintId
    workEffortList = from("WorkEffortAndProduct").where("workEffortId", projectAndTaskMap.projectId).queryList()
    backlogIndexList.add(workEffortList[0].productId)
	
    partyAssignmentSprintList = from("WorkEffortPartyAssignment").where("workEffortId", sprintId, "partyId", userLoginId).queryList()
    partyAssignmentSprintMap = partyAssignmentSprintList[0]
    // if this userLoginId is a member of sprint
    if (partyAssignmentSprintMap) {
        workEffortId = projectAndTaskMap.taskId
        partyAssignmentTaskList = from("WorkEffortPartyAssignment").where("workEffortId", workEffortId).queryList()
        partyAssignmentTaskMap = partyAssignmentTaskList[0]
        // if the task do not assigned
        if (partyAssignmentTaskMap) {
            custRequestTypeId = projectAndTaskMap.custRequestTypeId
			backlogStatusId = projectAndTaskMap.backlogStatusId
			if ("RF_SCRUM_MEETINGS".equals(custRequestTypeId) && "CRQ_REVIEWED".equals(backlogStatusId)) {
				projectSprintBacklogAndTaskList.add(projectAndTaskMap)
			   }
            } else {
					projectSprintBacklogAndTaskList.add(0,projectAndTaskMap)
             }
        }
    }

// for unplanned taks.
unplanList=[]
if (backlogIndexList) {
    backlogIndex = new HashSet(backlogIndexList)
    custRequestList = from("CustRequest").where("custRequestTypeId", "RF_UNPLAN_BACKLOG","statusId", "CRQ_REVIEWED").orderBy("custRequestDate DESC").queryList()
    if (custRequestList) {
        custRequestList.each { custRequestMap ->
            custRequestItemList = custRequestMap.getRelated("CustRequestItem", null, null, false)
			custRequestItem =  
			productOut = custRequestItemList[0].productId
			product = from("Product").where("productId", productOut).queryOne()
            backlogIndex.each { backlogProduct ->
                productId = backlogProduct
                if (productId.equals(productOut)) {
                    custRequestWorkEffortList = from("CustRequestWorkEffort").where("custRequestId", custRequestItemList[0].custRequestId).queryList()
                    custRequestWorkEffortList.each { custRequestWorkEffortMap ->
                        partyAssignmentTaskList = from("WorkEffortPartyAssignment").where("workEffortId", custRequestWorkEffortMap.workEffortId).queryList()
                        partyAssignmentTaskMap = partyAssignmentTaskList[0]
                        // if the task do not assigned
                        if (!partyAssignmentTaskMap) {
                            result = [:]
                            workEffortMap = from("WorkEffort").where("workEffortId", custRequestWorkEffortMap.workEffortId).queryOne()
                            result.description = custRequestMap.description
                            result.productName = product.internalName
                            result.taskId = workEffortMap.workEffortId
                            result.taskName = workEffortMap.workEffortName
							result.custRequestTypeId = custRequestMap.custRequestTypeId
							result.taskTypeId = workEffortMap.workEffortTypeId
                            unplanList.add(result)
                        }
                    }
                }
            }
        }
    }
}
projectSprintBacklogAndTaskList = UtilMisc.sortMaps(projectSprintBacklogAndTaskList, ["projectName","sprintName","-taskTypeId","custRequestId"])
projectSprintBacklogAndTaskList.each { projectSprintBacklogAndTaskMap ->
	blTypeId = projectSprintBacklogAndTaskMap.custRequestTypeId
	if (blTypeId == "RF_SCRUM_MEETINGS"){
		taskList.add(projectSprintBacklogAndTaskMap)
	}
}
projectSprintBacklogAndTaskList = UtilMisc.sortMaps(projectSprintBacklogAndTaskList, ["-projectName","sprintName","-taskTypeId","custRequestId"])
projectSprintBacklogAndTaskList.each { projectSprintBacklogAndTaskMap ->
	blTypeId = projectSprintBacklogAndTaskMap.custRequestTypeId
	if ("RF_PROD_BACKLOG" == blTypeId){
		taskList.add(0,projectSprintBacklogAndTaskMap)
	}
}
unplanList = UtilMisc.sortMaps(unplanList,["-productName","-taskTypeId","custRequestId"])
unplanList.each { unplanMap->
		taskList.add(0,unplanMap)
}
context.taskList = taskList

// notification context
webSiteId = WebSiteWorker.getWebSiteId(request)
context.webSiteId = webSiteId
