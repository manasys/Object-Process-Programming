/*******************************************************************************
 * Copyright (c) 2012 Arieh 'Vainolo' Bibliowicz
 * You can use this code for educational purposes. For any other uses
 * please contact me: vainolo@gmail.com
 *******************************************************************************/

package com.vainolo.phd.opp.editor.policy;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import com.google.common.base.Preconditions;
import com.vainolo.phd.opp.editor.command.OPPCreateLinkCommand;
import com.vainolo.phd.opp.editor.command.OPPNodeCreateCommand;
import com.vainolo.phd.opp.editor.factory.OPPIdManager;
import com.vainolo.phd.opp.editor.factory.OPPStructuralLinkPartFactory;
import com.vainolo.phd.opp.editor.part.OPPStructuralLinkAggregatorEditPart;
import com.vainolo.phd.opp.model.*;
import com.vainolo.phd.opp.utilities.analysis.OPPOPDAnalyzer;
import com.vainolo.phd.opp.validation.OPPLinkValidator;

/**
 * Policy used to connect two nodes in the diagram. Currently connections can
 * only be created between two {@link OPPThing} instances.
 * 
 * @author Arieh "Vainolo" Bibliowicz
 */
public class OPPNodeConnectionEditPolicy extends GraphicalNodeEditPolicy {

  private static final Dimension DEFAULT_AGGREGATOR_DIMENSION = new Dimension(15, 15);
  OPPLinkValidator validator;
  OPPOPDAnalyzer analyzer;

  OPPStructuralLinkPartFactory linkFactory;

  public OPPNodeConnectionEditPolicy(OPPLinkValidator validator, OPPOPDAnalyzer analyzer, OPPIdManager idManager) {
    Preconditions.checkNotNull(validator);
    this.validator = validator;
    this.analyzer = analyzer;
    this.linkFactory = new OPPStructuralLinkPartFactory(idManager);
  }

  /**
   * Create a command used to begin connecting to nodes.
   * {@link OPPStructuralLinkAggregatorEditPart} nodes cannot be source nodes,
   * therefore in this case a {@link UnexecutableCommand} is returned.
   * 
   * @return a {@link Command} that contains the initial information neede to
   *         create a connection between two nodes.
   */
  @Override
  protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
    // We must return null and not the usual UnexecutableCommand because if we
    // return a non-null value the framework thinks that the link can be created
    // from this host, something that we don't want to happen.
    if(getHost() instanceof OPPStructuralLinkAggregatorEditPart) {
      return null;
    }

    if(request.getNewObject() instanceof OPPStructuralLinkAggregator) {
      request.setStartCommand(new Command() {
      });
      return request.getStartCommand();
    }

    if(!validator.validateAddSource((OPPNode) getHost().getModel(), (OPPLink) request.getNewObject())) {
      return null;
    }

    OPPCreateLinkCommand result = new OPPCreateLinkCommand();
    result.setSource((OPPNode) getHost().getModel());
    result.setLink((OPPLink) request.getNewObject());
    result.setOPD(analyzer.findOPD((OPPNode) (OPPNode) getHost().getModel()));
    request.setStartCommand(result);
    return result;
  }

  /**
   * Retrieves the command created by
   * {@link OPPNodeConnectionEditPolicy#getConnectionCreateCommand(CreateConnectionRequest)
   * getConnectionCreateCommand}, and adds it information so that the command
   * can be executed. {@link OPPStructuralLinkAggregatorEditPart} nodes cannot
   * be source nodes, therefore in this case a {@link UnexecutableCommand} is
   * returned.
   * 
   * @return a {@link Command} that can be executed to create a connection
   *         between two nodes.
   */
  @Override
  protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
    // A null command must be returned (and not the usual UnexecutableCommand),
    // otherwise GEF shows a symbol that the connection can be completed but
    // when the used clicks it is not created.
    if(request.getStartCommand() == null || request.getTargetEditPart() instanceof OPPStructuralLinkAggregatorEditPart) {
      return null;
    }

    Command command = null;

    if(request.getNewObject() instanceof OPPStructuralLinkAggregator) {
      command = handleOPMStructuralLinkRequest(request);
    } else {
      if(!validator.validateAddTarget((OPPLink) request.getNewObject(), (OPPNode) getHost().getModel())) {
        return null;
      }

      OPPCreateLinkCommand linkCreateCommand = (OPPCreateLinkCommand) request.getStartCommand();
      linkCreateCommand.setTarget((OPPNode) getHost().getModel());
      command = linkCreateCommand;
    }

    return command;
  }

  /**
   * <p>
   * When the user requests the creation of a structural link, the following is
   * done:
   * </p>
   * <ol>
   * <li>If this is the first structural link of its kind between the source and
   * target nodes, we create a new aggregator and connect it to the source and
   * target.</li>
   * <li>If there already is an aggregator of its kind between the nodes, we
   * only add a new link from the aggregator to the new target.</li>
   * </ol>
   * 
   * @param request
   *          the user request to create a new strucutral link between the
   *          nodes.
   * @return a command that creates the links as stated above.
   */
  private Command handleOPMStructuralLinkRequest(CreateConnectionRequest request) {
    Command command = null;

    OPPNode sNode = (OPPNode) request.getSourceEditPart().getModel();
    OPPNode tNode = (OPPNode) request.getTargetEditPart().getModel();
    OPPStructuralLinkAggregator agrNode = (OPPStructuralLinkAggregator) request.getNewObject();

    // Search for an outgoing structural link aggregator matching the
    // requested kind.
    boolean aggregatorFound = false;
    for(OPPLink structuralLink : analyzer.findOutgoingStructuralLinks(sNode)) {
      OPPStructuralLinkAggregator existingAggregator = (OPPStructuralLinkAggregator) structuralLink.getTarget();
      if(existingAggregator.getKind() == agrNode.getKind()) {
        aggregatorFound = true;
        agrNode = existingAggregator;
      }
    }

    if(aggregatorFound) {
      // Just create a link from the aggregator to the target.
      command = createCreateOPMLlinkCreateCommand(agrNode, tNode, analyzer.findOPD(agrNode));
    } else {
      // Create a compound command consisting of three commands.
      CompoundCommand cCommand = new CompoundCommand();
      cCommand.add(createCreateAggregatorNodeCommand(sNode, tNode, agrNode));
      cCommand.add(createCreateOPMLlinkCreateCommand(sNode, agrNode, analyzer.findOPD(sNode)));
      cCommand.add(createCreateOPMLlinkCreateCommand(agrNode, tNode, analyzer.findOPD(sNode)));

      command = cCommand;
    }

    return command;
  }

  /**
   * Helper function to create a command that connects two nodes with a factory
   * generated link.
   * 
   * @param source
   *          the source of the link.
   * @param target
   *          the target of the link.
   * @return
   */
  private OPPCreateLinkCommand createCreateOPMLlinkCreateCommand(OPPNode source, OPPNode target,
      OPPObjectProcessDiagram opd) {
    OPPCreateLinkCommand command = new OPPCreateLinkCommand();
    command.setSource(source);
    command.setTarget(target);
    command.setOPD(opd);
    OPPStructuralLinkPart link = linkFactory.getNewObject();
    command.setLink(link);
    return command;
  }

  /**
   * Create a command that adds the provided {@link OPPStructuralLinkAggregator}
   * to the diagram located between the source and the target {@link OPPNode}.
   * 
   * @param source
   *          the source for the structural link.
   * @param target
   *          the target of the structural link.
   * @param aggregator
   *          the aggregator that should be added to the diagram.
   * @return A {@link OPMNodeCreateCommand} whose execution add the aggregator
   *         to the diagram.
   */
  public OPPNodeCreateCommand createCreateAggregatorNodeCommand(OPPNode source, OPPNode target, OPPNode aggregator) {
    OPPNodeCreateCommand command = new OPPNodeCreateCommand();
    command.setNode(aggregator);
    command.setContainer(source.getContainer());

    // Calculate location of aggregator, between the source and targetnodes.
    Point sCenter = new Point(source.getX() + source.getWidth() / 2, source.getY() + source.getHeight() / 2);
    Point tCenter = new Point(target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2);
    Point aggrgLeftTopCorner = new Point();
    aggrgLeftTopCorner.x = sCenter.x + (tCenter.x - sCenter.x) / 2 - DEFAULT_AGGREGATOR_DIMENSION.width / 2;
    aggrgLeftTopCorner.y = sCenter.y + (tCenter.y - sCenter.y) / 2 - DEFAULT_AGGREGATOR_DIMENSION.height / 2;
    if(aggrgLeftTopCorner.x < 0) {
      aggrgLeftTopCorner.x = 0;
    }
    if(aggrgLeftTopCorner.y < 0) {
      aggrgLeftTopCorner.y = 0;
    }
    command.setConstraints(new Rectangle(aggrgLeftTopCorner, DEFAULT_AGGREGATOR_DIMENSION));

    return command;
  }

  @Override
  protected Command getReconnectTargetCommand(ReconnectRequest request) {
    return null;
  }

  @Override
  protected Command getReconnectSourceCommand(ReconnectRequest request) {
    return null;
  }
}