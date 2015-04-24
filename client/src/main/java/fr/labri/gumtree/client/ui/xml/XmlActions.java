package fr.labri.gumtree.client.ui.xml;

import fr.labri.gumtree.actions.ActionGenerator;
import fr.labri.gumtree.client.DiffClient;
import fr.labri.gumtree.client.DiffOptions;
import fr.labri.gumtree.io.ActionsIoUtils;
import fr.labri.gumtree.matchers.Matcher;

public class XmlActions extends DiffClient {

	public XmlActions(DiffOptions diffOptions) {
		super(diffOptions);
	}

	@Override
	public void start() {
		Matcher m = matchTrees();
		ActionGenerator actionGenerator = new ActionGenerator(m.getSrc(), m.getDst(), m.getMappings());
		actionGenerator.generate();
		String xmlActions = ActionsIoUtils.toXml(getSrcTreeContext(), actionGenerator.getActions());
		System.out.println(xmlActions);
	}

}
