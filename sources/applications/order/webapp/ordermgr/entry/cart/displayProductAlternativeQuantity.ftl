                          <#assign quantity = parameters.quantity!>
                          <#assign altQuantityUomIds = parameters.altQuantityUomIds!>
                          <#if altQuantityUomIds?has_content>
                              <#if (altQuantityUomIds.size() > 1)>
                              <select name="quantityUomId" id="quantityUomId">
                                  <option value="">${uiLabelMap.CommonSelectUnitOfMeasure}</option>
                                  <#list altQuantityUomIds as altQuantityUomId>
                                      <#assign uom = delegator.findOne("Uom", {"uomId" : altQuantityUomId}, false)?if_exists>
                                      <option value="${altQuantityUomId}">${uom.get("description", locale)!} </option>
                                  </#list>
                              </select>
                              <#else>
                                  <#assign altQuantityUomId = altQuantityUomIds[0]>
                                  <#assign uom = delegator.findOne("Uom", {"uomId" : altQuantityUomId}, false)?if_exists>
                                  <input type="hidden" name="quantityUomId" id="quantityUomId" value="${altQuantityUomId!}"/>
                                  ${uom.get("description", locale)!}
                              </#if>
                          </#if>