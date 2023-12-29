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
package org.apache.ofbiz.lucene.content

import org.apache.ofbiz.product.feature.ParametricSearch
import org.apache.ofbiz.product.product.ProductSearchSession

productFeaturesByTypeMap = ParametricSearch.getAllFeaturesByType(delegator, 2000)
productFeatureTypeIdsOrdered = new ArrayList(new TreeSet(productFeaturesByTypeMap.keySet()))

searchOperator = request.getParameter("SEARCH_OPERATOR")
if (!"AND".equals(searchOperator) && !"OR".equals(searchOperator)) {
  searchOperator = "OR"
}

searchConstraintStrings = ProductSearchSession.searchGetConstraintStrings(false, session, delegator)
searchSortOrderString = ProductSearchSession.searchGetSortOrderString(false, request)

context.productFeaturesByTypeMap = productFeaturesByTypeMap
context.productFeatureTypeIdsOrdered = productFeatureTypeIdsOrdered
context.searchOperator = searchOperator
context.searchConstraintStrings = searchConstraintStrings
context.searchSortOrderString = searchSortOrderString
