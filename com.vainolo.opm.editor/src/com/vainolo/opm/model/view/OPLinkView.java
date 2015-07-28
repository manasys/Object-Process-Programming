package com.vainolo.opm.model.view;


import com.vainolo.opm.model.OPLink;
import com.vainolo.opm.model.OPObjectProcessDiagram;

public interface OPLinkView extends OPElementView {
	OPLink getModel();
	void setModel(OPLink link);
	
	OPNodeView getSource();
	void setSource(OPNodeView nodeView);
	
	OPNodeView getTarget();
	void setTarget(OPNodeView nodeView);

	// TODO: remove
	OPObjectProcessDiagram getObjectProcessDiagram();
	void setObjectProcessDiagram(OPObjectProcessDiagram opd);
}