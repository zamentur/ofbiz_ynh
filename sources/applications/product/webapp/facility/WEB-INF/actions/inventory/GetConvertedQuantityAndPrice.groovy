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

import org.ofbiz.base.util.UtilValidate;

String currentPiecesIncluded = request.getParameter("currentPiecesIncluded");
if (UtilValidate.isNotEmpty(currentPiecesIncluded) && currentPiecesIncluded.contains(",")) {
    currentPiecesIncluded = currentPiecesIncluded.replaceAll(",", ".");
}
if (UtilValidate.isNotEmpty(currentPiecesIncluded) && currentPiecesIncluded.contains(" ")) {
    currentPiecesIncluded = currentPiecesIncluded.replaceAll(" ", "");
}
String newPackagingUnit = request.getParameter("newPackagingUnit");
if (UtilValidate.isNotEmpty(newPackagingUnit) && newPackagingUnit.contains(",")) {
    newPackagingUnit = newPackagingUnit.replaceAll(",", ".");
}
if (UtilValidate.isNotEmpty(newPackagingUnit) && newPackagingUnit.contains(" ")) {
    newPackagingUnit = newPackagingUnit.replaceAll(" ", "");
}
String currentQuantity = request.getParameter("currentQuantity");
if (UtilValidate.isNotEmpty(currentQuantity) && currentQuantity.contains(",")) {
    currentQuantity = currentQuantity.replaceAll(",", ".");
}
if (UtilValidate.isNotEmpty(currentQuantity) && currentQuantity.contains(" ")) {
    currentQuantity = currentQuantity.replaceAll(" ", "");
}
String currentRejectedQuantity = request.getParameter("currentRejectedQuantity");
if (UtilValidate.isNotEmpty(currentRejectedQuantity) && currentRejectedQuantity.contains(",")) {
    currentRejectedQuantity = currentRejectedQuantity.replaceAll(",", ".");
}
if (UtilValidate.isNotEmpty(currentRejectedQuantity) && currentRejectedQuantity.contains(" ")) {
    currentRejectedQuantity = currentRejectedQuantity.replaceAll(" ", "");
}
String currentUnitCost = request.getParameter("currentUnitCost");
if (UtilValidate.isNotEmpty(currentUnitCost) && currentUnitCost.contains(",")) {
    currentUnitCost = currentUnitCost.replaceAll(",", ".");
}
if (UtilValidate.isNotEmpty(currentUnitCost) && currentUnitCost.contains(" ")) {
    currentUnitCost = currentUnitCost.replaceAll(" ", "");
}
if (UtilValidate.isNotEmpty(currentPiecesIncluded) && UtilValidate.isNotEmpty(newPackagingUnit)) {
    BigDecimal piecesIncluded = currentPiecesIncluded.toBigDecimal();
    BigDecimal newPiecesIncluded = newPackagingUnit.toBigDecimal();
    //Accepted quantity update
    if (UtilValidate.isNotEmpty(currentQuantity)) {
        BigDecimal qty = currentQuantity.toBigDecimal();
        if (currentPiecesIncluded == '1') {
            qty = qty.divide(newPiecesIncluded);
        } else {
            qty = qty.multiply(piecesIncluded);
        }
        Double convertedQuantity = qty.doubleValue();
        request.setAttribute("convertedQuantity", convertedQuantity);
    }
    //Rejected quantity update
    if (UtilValidate.isNotEmpty(currentRejectedQuantity)) {
        BigDecimal rejectedQty = currentRejectedQuantity.toBigDecimal();
        if (currentPiecesIncluded == '1') {
            rejectedQty = rejectedQty.divide(newPiecesIncluded);
        } else {
            rejectedQty = rejectedQty.multiply(piecesIncluded);
        }
        Double convertedRejectedQuantity = rejectedQty.doubleValue();
        request.setAttribute("convertedRejectedQuantity", convertedRejectedQuantity);
    }
    if (UtilValidate.isNotEmpty(currentUnitCost)) {
        BigDecimal cost = currentUnitCost.toBigDecimal();
        if (currentPiecesIncluded == '1') {
            cost = cost.multiply(newPiecesIncluded);
        } else {
            cost = cost.divide(piecesIncluded);
        }
        Double convertedUnitCost = cost.doubleValue();
        request.setAttribute("convertedUnitCost", convertedUnitCost);
    }
    return "success";
} else {
    return "error";
}
