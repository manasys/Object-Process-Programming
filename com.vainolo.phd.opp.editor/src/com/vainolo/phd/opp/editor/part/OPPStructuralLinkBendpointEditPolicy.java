package com.vainolo.phd.opp.editor.part;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import com.vainolo.phd.opp.editor.command.OPPLinkCreateBendpointCommand;
import com.vainolo.phd.opp.editor.command.OPPLinkDeleteBendpointCommand;
import com.vainolo.phd.opp.editor.command.OPPLinkMoveBendpointCommand;
import com.vainolo.phd.opp.model.OPPLink;

public class OPPStructuralLinkBendpointEditPolicy extends BendpointEditPolicy {

  @Override
  protected Command getCreateBendpointCommand(BendpointRequest request) {
    OPPLinkCreateBendpointCommand command = new OPPLinkCreateBendpointCommand();
    Point p = request.getLocation();
    command.setLink((OPPLink) request.getSource().getModel());
    command.setLocation(p);
    command.setIndex(request.getIndex());
    return command;
  }

  @Override
  protected Command getDeleteBendpointCommand(BendpointRequest request) {
    OPPLinkMoveBendpointCommand command = new OPPLinkMoveBendpointCommand();
    Point p = request.getLocation();
    command.setLink((OPPLink) request.getSource().getModel());
    command.setLocation(p);
    command.setIndex(request.getIndex());
    return command;
  }

  @Override
  protected Command getMoveBendpointCommand(BendpointRequest request) {
    OPPLinkDeleteBendpointCommand command = new OPPLinkDeleteBendpointCommand();
    command.setLink((OPPLink) request.getSource().getModel());
    command.setIndex(request.getIndex());
    return command;
  }

}
