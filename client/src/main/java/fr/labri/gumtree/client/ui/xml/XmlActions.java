package fr.labri.gumtree.client.ui.xml;

import fr.labri.gumtree.actions.ActionGenerator;
import fr.labri.gumtree.client.DiffClient;
import fr.labri.gumtree.client.DiffOptions;
import fr.labri.gumtree.io.ActionsIoUtils;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;

public class XmlActions extends DiffClient {

	private static final String EMPTY_FILE_NAME = "devnull";
	
	public XmlActions(DiffOptions diffOptions) {
		super(diffOptions);
	}

	@Override
	public void start() {
		Matcher m = (diffOptions.getMatcher() == null) ? MatcherFactories.newMatcher(getSrcTreeContext().getRoot(), getDstTreeContext().getRoot()) : MatcherFactories.newMatcher(getSrcTreeContext().getRoot(), getDstTreeContext().getRoot(), diffOptions.getMatcher());		
		if (!(diffOptions.getSrc().contains(EMPTY_FILE_NAME) || diffOptions.getDst().contains(EMPTY_FILE_NAME))) m.match();
		
		ActionGenerator actionGenerator = new ActionGenerator(m.getSrc(), m.getDst(), m.getMappings());
		actionGenerator.generate();
		String xmlActions = ActionsIoUtils.toXml(getSrcTreeContext(), actionGenerator.getActions());
		System.out.println(xmlActions);
	}
}
