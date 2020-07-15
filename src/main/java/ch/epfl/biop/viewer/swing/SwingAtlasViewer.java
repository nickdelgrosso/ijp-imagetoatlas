package ch.epfl.biop.viewer.swing;

import ch.epfl.biop.atlas.BiopAtlas;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Plugin(type = DisplayViewer.class)
public class SwingAtlasViewer extends
        EasySwingDisplayViewer<BiopAtlas> implements TreeSelectionListener {

    private JTree tree;

    public SwingAtlasViewer()
    {
        super( BiopAtlas.class );
    }

    BiopAtlas atlas = null;

    @Override
    protected boolean canView(BiopAtlas value) {
        return true;
    }

    @Override
    protected void redoLayout() {

    }

    @Override
    protected void setLabel(String s) {

    }

    @Override
    protected void redraw() {

    }

    @Override
    public JPanel createDisplayPanel(BiopAtlas atlas) {
        this.atlas=atlas;
        final JPanel panel = new JPanel();
        panel.setLayout( new GridLayout(2,1));

        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode(atlas.toString());
        createNodes(top);

        tree = new JTree(top);

        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        JScrollPane treeView = new JScrollPane(tree);

        panel.add(treeView);
        return panel;
    }

    private void addNodes(DefaultMutableTreeNode basenode, int index) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                atlas.ontology.getProperties(index).get(atlas.ontology.getNamingDisplayProperty())
            );
        basenode.add(node);
        atlas.ontology.getChildren(index).forEach( i -> {
            addNodes(node,i);
        });
    }

    private void createNodes(DefaultMutableTreeNode top) {
        int rootIndex = atlas.ontology.getRootIndex();
        addNodes(top, rootIndex);
    }


    List<TreeSelectionListener> listeners = new ArrayList<>();

    public synchronized void addTreeSelectionListener(TreeSelectionListener tsl) {
        listeners.add(tsl);
    }

    @Override
    public synchronized void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        listeners.forEach(tsl -> tsl.valueChanged(treeSelectionEvent));
    }

}
