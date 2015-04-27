package fr.labri.gumtree.io;

import java.io.IOException;

import fr.labri.gumtree.tree.ITree;
import fr.labri.gumtree.tree.TreeContext;
import fr.labri.gumtree.tree.TreeUtils;

public abstract class TreeGenerator {
	
	public TreeContext fromFile(String file) throws IOException {
		TreeContext ctx = generate(file);
		if (ctx == null){
			ctx = new TreeContext();
			ctx.setRoot(ctx.createTree(-1, "empty", "empty"));
			return ctx;
		}
		ITree root = ctx.getRoot();
		root.refresh();
		TreeUtils.postOrderNumbering(root);
		return ctx;
	}
	
	public abstract TreeContext generate(String file) throws IOException;
	
	public abstract boolean handleFile(String file);
	
	public abstract String getName();

}
