/*******************************************************************************
 * Copyright (c) 2012 Arieh 'Vainolo' Bibliowicz
 * You can use this code for educational purposes. For any other uses
 * please contact me: vainolo@gmail.com
 *******************************************************************************/
package com.vainolo.phd.opm.interpreter;

import com.vainolo.phd.opm.interpreter.builtin.OPMAddProcessInstance;
import com.vainolo.phd.opm.interpreter.builtin.OPMConceptualProcess;
import com.vainolo.phd.opm.interpreter.builtin.OPMInputProcessInstance;
import com.vainolo.phd.opm.interpreter.builtin.OPMOutputProcessInstance;
import com.vainolo.phd.opm.interpreter.builtin.OPMSleepProcessInstance;
import com.vainolo.phd.opm.model.OPMProcess;
import com.vainolo.phd.opm.model.OPMProcessKind;

public enum OPMProcessInstanceFactory {
  INSTANCE;

  public OPMProcessInstance createProcessInstance(final OPMProcess process, final OPMProcessKind kind) {
    OPMProcessInstance processInstance;
    switch(kind) {
      case BUILT_IN:
        processInstance = createBuildInProcess(process);
        break;
      case COMPOUND:
        processInstance = new OPMCompoundProcessInstance(process);
        break;
      case CONCEPTUAL:
        processInstance = new OPMConceptualProcess(process);
        break;
      case JAVA:
        throw new UnsupportedOperationException("Java processes are not supported yet.");
      default:
        throw new IllegalStateException("Received unexpected OPMProcessKind " + kind.getLiteral());
    }

    return processInstance;
  }

  private OPMProcessInstance createBuildInProcess(final OPMProcess process) {
    OPMProcessInstance processInstance;

    if(process.getName().equals("Input")) {
      processInstance = new OPMInputProcessInstance(process);
    } else if(process.getName().equals("Output")) {
      processInstance = new OPMOutputProcessInstance(process);
    } else if(process.getName().equals("Add")) {
      processInstance = new OPMAddProcessInstance(process);
    } else if(process.getName().equals("Sleep")) {
      processInstance = new OPMSleepProcessInstance(process);
    } else {
      throw new IllegalStateException("Tried to create unexistent build-in process " + process.getName());
    }

    return processInstance;

  }
}