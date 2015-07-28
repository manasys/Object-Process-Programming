package com.vainolo.opm.editor.factory;

import org.eclipse.gef.requests.CreationFactory;

import com.vainolo.opm.model.OPModelFactory;
import com.vainolo.opm.model.OPObject;
import com.vainolo.opm.model.view.OPNodeView;
import com.vainolo.opm.model.view.OPThingView;

public class OPObjectViewFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		OPThingView view = OPModelFactory.createThingView();
		OPObject object = OPModelFactory.createObject();
		object.setName("...");
		view.setModel(object);
		return view;
	}

	@Override
	public Object getObjectType() {
		return OPNodeView.class;
	}

}