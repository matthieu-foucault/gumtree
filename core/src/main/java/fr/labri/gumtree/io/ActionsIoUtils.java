package fr.labri.gumtree.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.actions.model.Delete;
import fr.labri.gumtree.actions.model.Insert;
import fr.labri.gumtree.actions.model.Move;
import fr.labri.gumtree.actions.model.Update;
import fr.labri.gumtree.io.TreeIoUtils.XMLFormater;
import fr.labri.gumtree.matchers.MappingStore;
import fr.labri.gumtree.tree.ITree;
import fr.labri.gumtree.tree.TreeContext;

public final class ActionsIoUtils {

    private ActionsIoUtils() {
    }

    public static String toText(List<Action> script) {
        StringWriter w = new StringWriter();
        for (Action a: script) w.append(a.toString() + "\n");
        String result = w.toString();
        try {
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void toText(List<Action> script, String file) {
        try {
            FileWriter w = new FileWriter(file);
            for (Action a : script) w.append(a.toString() + "\n");
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toXml(TreeContext ctx, List<Action> actions) {
    	StringWriter s = new StringWriter();
    	XMLOutputFactory f = XMLOutputFactory.newInstance();
          try {
              XMLStreamWriter w = new IndentingXMLStreamWriter(f.createXMLStreamWriter(s));
              w.writeStartDocument();
              w.writeStartElement("actions");
              for (Action action : actions)
            	  writeAction(action, ctx, w);
              w.writeEndElement();
              w.writeEndDocument();
              w.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
          
          return s.toString();
    }
    
    public static void writeAction(Action a, TreeContext ctx, XMLStreamWriter w) throws Exception {
    	w.writeStartElement("action");
        w.writeAttribute("type", a.getClass().getSimpleName());
        XMLFormater xmlFormater = new XMLFormater(w, ctx);
        xmlFormater.startTree(a.getNode());
        xmlFormater.endTree(a.getNode());
    	w.writeEndElement();
    }
    
    public static void toXml(TreeContext sctx, List<Action> actions, MappingStore mappings, String file) throws IOException {
        FileWriter f = new FileWriter(file);
        try {
            toXml(sctx, f, actions, mappings);
        } finally {
            try {
                f.close();
            } catch (IOException e) { }
        }
    }

    public static String toXml(TreeContext sctx, List<Action> actions, MappingStore mappings) {
        StringWriter s = new StringWriter();
        try {
            toXml(sctx, s, actions, mappings);
            return s.toString();
        } finally {
            try {
                s.close();
            } catch (IOException e) { }
        }
    }

    public static void toXml(TreeContext sctx, Writer writer, List<Action> actions, MappingStore mappings) {
        XMLOutputFactory f = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter w = new IndentingXMLStreamWriter(f.createXMLStreamWriter(writer));
            w.writeStartDocument();
            w.writeStartElement("actions");
            writeActions(sctx, actions, mappings, w);
            w.writeEndElement();
            w.writeEndDocument();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeActions(TreeContext sctx, List<Action> actions, MappingStore mappings, XMLStreamWriter w) throws XMLStreamException {
        for (Action a : actions) {
            w.writeStartElement("action");
            w.writeAttribute("type", a.getClass().getSimpleName());
            w.writeAttribute("tree", sctx.getTypeLabel(a.getNode()));
            if (a instanceof Move || a instanceof Update) {
                ITree src = a.getNode();
                ITree dst = mappings.getDst(src);
                writeTreePos(w, true, src);
                writeTreePos(w, false, dst);
            } else if (a instanceof Insert) {
                ITree dst = a.getNode();
                if (dst.isRoot()) writeInsertPos(w, true, new int[] {0, 0});
                else {
                    int[] pos;
                    int idx = dst.getParent().getChildPosition(dst);

                    if (idx == 0) pos = dst.getParent().getLcPosStart();
                    else pos = dst.getParent().getChildren().get(idx -1).getLcPosEnd();

                    writeInsertPos(w, true,pos);
                }
                writeTreePos(w, false, dst);
            } else if (a instanceof Delete) {
                ITree src = a.getNode();
                writeTreePos(w, true, src);
            }
            w.writeEndElement();
        }
    }

    private static void writeTreePos(XMLStreamWriter w, boolean isBefore, ITree tree) throws XMLStreamException {
        if (isBefore) w.writeEmptyElement("before"); else w.writeEmptyElement("after");
        if (tree.getLcPosStart() != null) {
            w.writeAttribute("begin_line", Integer.toString(tree.getLcPosStart()[0]));
            w.writeAttribute("begin_col", Integer.toString(tree.getLcPosStart()[1]));
            w.writeAttribute("end_line", Integer.toString(tree.getLcPosEnd()[0]));
            w.writeAttribute("end_col", Integer.toString(tree.getLcPosEnd()[1]));
        }
    }

    private static void writeInsertPos(XMLStreamWriter w, boolean isBefore, int[] pos) throws XMLStreamException {
        if (isBefore) w.writeEmptyElement("before"); else w.writeEmptyElement("after");
        w.writeAttribute("begin_line", Integer.toString(pos[0]));
        w.writeAttribute("begin_col", Integer.toString(pos[1]));
        w.writeAttribute("end_line", Integer.toString(pos[0]));
        w.writeAttribute("end_col", Integer.toString(pos[1]));
    }

}
