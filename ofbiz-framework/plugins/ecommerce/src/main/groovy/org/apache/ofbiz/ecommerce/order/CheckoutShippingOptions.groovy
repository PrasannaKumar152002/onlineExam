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
package org.apache.ofbiz.ecommerce.order

import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.party.contact.*
import org.apache.ofbiz.product.store.*
import org.apache.ofbiz.order.shoppingcart.shipping.*

cart = session.getAttribute("shoppingCart")
party = userLogin.getRelatedOne("Party", false)
productStore = ProductStoreWorker.getProductStore(request)

if (cart) {
    shippingEstWpr = new ShippingEstimateWrapper(dispatcher, cart, 0)
    context.shippingEstWpr = shippingEstWpr
    context.carrierShipmentMethodList = shippingEstWpr.getShippingMethods()
}

context.shoppingCart = cart
context.userLogin = userLogin
context.productStoreId = productStore.productStoreId
context.productStore = productStore
context.emailList = ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false)

if (cart.getShipmentMethodTypeId() && cart.getCarrierPartyId()) {
    context.chosenShippingMethod = cart.getShipmentMethodTypeId() + '@' + cart.getCarrierPartyId()
}
