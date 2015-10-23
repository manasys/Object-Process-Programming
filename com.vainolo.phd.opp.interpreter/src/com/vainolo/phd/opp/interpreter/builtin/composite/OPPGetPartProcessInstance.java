package com.vainolo.phd.opp.interpreter.builtin.composite;

import java.util.List;

import com.google.common.collect.Lists;
import com.vainolo.phd.opp.interpreter.OPPAbstractProcessInstance;
import com.vainolo.phd.opp.interpreter.OPPObjectInstance;
import com.vainolo.phd.opp.interpreter.OPPParameter;
import com.vainolo.phd.opp.interpreter.OPPObjectInstance.InstanceKind;

public class OPPGetPartProcessInstance extends OPPAbstractProcessInstance {

  @Override
  protected void executing() throws Exception {
    OPPObjectInstance composite = getArgument("whole");
    OPPObjectInstance key = getArgument("key");
    if (composite.containsPart(key.getStringValue())) {
      OPPObjectInstance part = composite.getPart(key.getStringValue());
      setArgument("part", part);
      setArgument("exists?", OPPObjectInstance.createFromValue("yes"));
    } else {
      setArgument("exists?", OPPObjectInstance.createFromValue("no"));
    }
  }

  @Override
  public void setArgument(String name, OPPObjectInstance value) {
    // Modified since we need the original ID of the instance to be used as key, and this ID will be changed when a new
    // instance is created - so we transform the instance to a string that is used as the key.
    if ("key".equals(name)) {
      if (value.kind == InstanceKind.COMPOSITE) {
        value = OPPObjectInstance.createFromValue(value.getId());
      }
    }
    super.setArgument(name, value);
  }

  @Override
  public List<OPPParameter> getIncomingParameters() {
    return Lists.newArrayList(new OPPParameter("whole"), new OPPParameter("key"));
  }

  @Override
  public List<OPPParameter> getOutgoingParameters() {
    return Lists.newArrayList(new OPPParameter("part"), new OPPParameter("exists?"));
  }
}
