package com.vainolo.phd.opp.interpreter.builtin.composite;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.vainolo.phd.opp.interpreter.OPPObjectInstance;

public class OPPSetPartProcessInstanceTest {

  private String stringValue = "hello";
  private BigDecimal numericalValue = new BigDecimal(5);
  private OPPObjectInstance compositeInstance1, compositeInstance2;
  private OPPObjectInstance numericInstance;
  private OPPObjectInstance stringInstance;
  private OPPSetPartProcessInstance setPartProcessInstance;

  private OPPObjectInstance doTest(OPPObjectInstance composite, OPPObjectInstance key, OPPObjectInstance value) throws Exception {
    setPartProcessInstance.setArgument("whole", composite);
    setPartProcessInstance.setArgument("key", key);
    setPartProcessInstance.setArgument("part", value);
    setPartProcessInstance.call();
    return setPartProcessInstance.getArgument("new whole");
  }

  @Test
  public void test_setPart_byStringInstance() throws Exception {
    OPPObjectInstance result = doTest(compositeInstance1, stringInstance, numericInstance);
    assertEquals(numericInstance.getNumericalValue(), result.getPart(stringInstance.getStringValue()).getNumericalValue());
  }

  @Test
  public void test_setPart_byNumberInstance() throws Exception {
    OPPObjectInstance result = doTest(compositeInstance1, numericInstance, stringInstance);
    assertEquals(stringInstance.getStringValue(), result.getPart(numericInstance.getStringValue()).getStringValue());
  }

  @Test
  public void test_setPart_byInstance() throws Exception {
    OPPObjectInstance result = doTest(compositeInstance1, compositeInstance2, numericInstance);
    assertEquals(numericInstance.getNumericalValue(), result.getPart(compositeInstance2.getId()).getNumericalValue());
  }

  @Test(expected = NullPointerException.class)
  public void test_setPart_getPartOnOriginalObjectThrowsException() throws Exception {
    doTest(compositeInstance1, stringInstance, numericInstance);
    compositeInstance1.getPart(stringInstance.getStringValue());
  }

  @Before
  public void setup() {
    compositeInstance1 = OPPObjectInstance.createCompositeInstance();
    compositeInstance2 = OPPObjectInstance.createCompositeInstance();
    numericInstance = OPPObjectInstance.createFromValue(numericalValue);
    stringInstance = OPPObjectInstance.createFromValue(stringValue);

    setPartProcessInstance = new OPPSetPartProcessInstance();
  }

}