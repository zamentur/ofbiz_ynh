/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package org.ofbiz.common.uom;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.ibm.icu.util.Calendar;

/**
 * UomWorker
 */
public class UomWorker {

    public static final String module = UomWorker.class.getName();

    private UomWorker () {}

    public static int[] uomTimeToCalTime(String uomId) {
        if ("TF_ms".equals(uomId)) {
            return new int[] { Calendar.MILLISECOND, 1 };
        } else if ("TF_s".equals(uomId)) {
            return new int[] { Calendar.SECOND, 1 };
        } else if ("TF_min".equals(uomId)) {
            return new int[] { Calendar.MINUTE, 1 };
        } else if ("TF_hr".equals(uomId)) {
            return new int[] { Calendar.HOUR, 1 };
        } else if ("TF_day".equals(uomId)) {
            return new int[] { Calendar.DAY_OF_YEAR, 1 };
        } else if ("TF_wk".equals(uomId)) {
            return new int[] { Calendar.WEEK_OF_YEAR, 1 };
        } else if ("TF_mon".equals(uomId)) {
            return new int[] { Calendar.MONTH, 1 };
        } else if ("TF_yr".equals(uomId)) {
            return new int[] { Calendar.YEAR, 1 };
        } else if ("TF_decade".equals(uomId)) {
            return new int[] { Calendar.YEAR, 10 };
        } else if ("TF_score".equals(uomId)) {
            return new int[] { Calendar.YEAR, 20 };
        } else if ("TF_century".equals(uomId)) {
            return new int[] { Calendar.YEAR, 100 };
        } else if ("TF_millenium".equals(uomId)) {
            return new int[] { Calendar.YEAR, 1000 };
        }

        return null;
    }

    public static Calendar addUomTime(Calendar cal, Timestamp startTime, String uomId, int value) {
        if (cal == null) {
            cal = Calendar.getInstance();
        }
        if (startTime != null) {
            cal.setTimeInMillis(startTime.getTime());
        }
        int[] conv = uomTimeToCalTime(uomId);

        // conversion multiplier * value by type
        cal.add(conv[0], (value * conv[1]));
        return cal;
    }

    public static Calendar addUomTime(Calendar cal, String uomId, int value) {
        return addUomTime(cal, null, uomId, value);
    }

    public static Calendar addUomTime(Timestamp startTime, String uomId, int value) {
        return addUomTime(null, startTime, uomId, value);
    }

    /*
     * Convenience method to call the convertUom service
     */
    public static BigDecimal convertUom(BigDecimal originalValue, String uomId, String uomIdTo, LocalDispatcher dispatcher) {
        if (originalValue == null || uomId == null || uomIdTo == null) return null;
        if (uomId.equals(uomIdTo)) return originalValue;

        Map<String, Object> svcInMap = FastMap.newInstance();
        svcInMap.put("originalValue", originalValue);
        svcInMap.put("uomId", uomId);
        svcInMap.put("uomIdTo", uomIdTo);

        Map<String, Object> svcOutMap = FastMap.newInstance();
        try {
            svcOutMap = dispatcher.runSync("convertUom", svcInMap);
        } catch (GenericServiceException ex) {
            Debug.logError(ex, module);
            return null;
        }

        if (svcOutMap.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS) && svcOutMap.get("convertedValue") != null) {
            return (BigDecimal) svcOutMap.get("convertedValue");
        }
        Debug.logError("Failed to perform conversion for value [" + originalValue.toPlainString() + "] from Uom [" + uomId + "] to Uom [" + uomIdTo + "]",module);
        return null;
    }

    public static BigDecimal convertStockUom(BigDecimal originalValue, BigDecimal conversionFactor, String roundingMode, Long decimalScale, String uomId, String uomIdTo, LocalDispatcher dispatcher) {
        Integer rounding = null;
        if (UtilValidate.isNotEmpty(roundingMode)) {
            rounding = UtilNumber.roundingModeFromString(roundingMode);
        }
        Integer scale = null;
        if (UtilValidate.isNotEmpty(decimalScale)) {
            scale = Integer.valueOf(decimalScale.intValue());
        }
        return convertUom(originalValue, conversionFactor, rounding, scale, uomId, uomIdTo, dispatcher);
    }

    public static BigDecimal convertUom(BigDecimal originalValue, BigDecimal conversionFactor, Integer roundingMode, Integer decimalScale, String uomId, String uomIdTo, LocalDispatcher dispatcher) {
        if (conversionFactor == null && (uomId == null || uomIdTo == null)) return null;
        if (conversionFactor == null && uomId.equals(uomIdTo)) return originalValue;

        Integer defaultRoundingMode = UtilNumber.getBigDecimalRoundingMode("stock.rounding");
        Integer defaultDecimalScale = UtilNumber.getBigDecimalScale("stock.decimals");
        if (UtilValidate.isNotEmpty(conversionFactor)) {
            if (UtilValidate.isEmpty(roundingMode)) {
                roundingMode = defaultRoundingMode;
            }
            if (UtilValidate.isEmpty(decimalScale)) {
                decimalScale = defaultDecimalScale;
            }
            BigDecimal convertedValue = originalValue.multiply(conversionFactor).setScale(decimalScale, roundingMode);
            return convertedValue;
        } else {
            GenericValue uomConversion = null;
            try {
                uomConversion = dispatcher.getDelegator().findOne("UomConversion", UtilMisc.toMap("uomId", uomId, "uomIdTo", uomIdTo), false);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            if (UtilValidate.isNotEmpty(uomConversion)) {
                //Call conversion service
                Map<String, Object> svcInMap = FastMap.newInstance();
                svcInMap.put("originalValue", originalValue);
                svcInMap.put("uomId", uomId);
                svcInMap.put("uomIdTo", uomIdTo);
                if (UtilValidate.isNotEmpty(defaultRoundingMode)) {
                    svcInMap.put("defaultRoundingMode", defaultRoundingMode.toString());
                }
                if (UtilValidate.isNotEmpty(defaultDecimalScale)) {
                    svcInMap.put("defaultDecimalScale", defaultDecimalScale.longValue());
                }
                if (UtilValidate.isNotEmpty(roundingMode)) {
                    svcInMap.put("roundingMode", roundingMode.toString());
                }
                if (UtilValidate.isNotEmpty(decimalScale)) {
                    svcInMap.put("decimalScale", decimalScale.longValue());
                }
                Map<String, Object> svcOutMap = FastMap.newInstance();
                try {
                    svcOutMap = dispatcher.runSync("convertUom", svcInMap);
                } catch (GenericServiceException ex) {
                    Debug.logError(ex, module);
                    return null;
                }
                if (svcOutMap.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS) && svcOutMap.get("convertedValue") != null) {
                    return (BigDecimal) svcOutMap.get("convertedValue");
                }
            } else {
                //Use default values
                Debug.logInfo(UtilProperties.getMessage("CommonErrorUiLabels", "CommonErrorErrorCouldNotFindConversion", UtilMisc.toMap("uomId", uomId, "uomIdTo", uomIdTo), Locale.getDefault()), module);
                return convertUom(originalValue, BigDecimal.valueOf(1), defaultRoundingMode, defaultDecimalScale, null, null, dispatcher);
            }
            Debug.logError("Failed to perform conversion for value [" + originalValue.toPlainString() + "] from Uom [" + uomId + "] to Uom [" + uomIdTo + "]",module);
            return null;
        }
    }
}
