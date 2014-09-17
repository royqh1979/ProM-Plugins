package org.processmining.plugins.petrinet.history;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.log.LogVisualizer;
import org.processmining.plugins.petrinet.PetriNetVisualization;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roy on 2014/8/11.
 */
@Plugin(name = "Show Petrinet History",
        parameterLabels = {"PetrinetHistory"},
        returnLabels = {"Petrinet History Viewer"},
        returnTypes = {JComponent.class},
        userAccessible = false)
@Visualizer
public class PetrinetHistoryVisualizer {
    Map<DefaultMutableTreeNode, JComponent> nodePanels=new HashMap<DefaultMutableTreeNode, JComponent>();

    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visualize(final PluginContext context,final PetrinetHistory petrinetHistory) {

        final JScrollPane navPanel=new JScrollPane();
        final JScrollPane mainPanel=new JScrollPane();
        final JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                true, navPanel,mainPanel);
        splitPane.setDividerLocation(200);
        final DefaultMutableTreeNode treeNode=createTreeNode(context,petrinetHistory);
        final JTree tree=new JTree(treeNode);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                if (node == null)
                    return;
                if (nodePanels.containsKey(node))     {
                    mainPanel.setViewportView(nodePanels.get(node));
                }else {
                    JComponent panel=createPeriodPanel(context,(PetrinetHistoryPeriod)node.getUserObject());
                    nodePanels.put(node,panel);
                    mainPanel.setViewportView(panel);
                }
                mainPanel.updateUI();

            }
        });
        navPanel.setViewportView(tree);
        navPanel.updateUI();
        return splitPane;
    }

    private DefaultMutableTreeNode createTreeNode(PluginContext context, PetrinetHistory petrinetHistory) {
        DefaultMutableTreeNode root=new DefaultMutableTreeNode("Petrinet evolving history");
        StablePeriodsVisualizer stablePeriodsVisualizer=new StablePeriodsVisualizer();
        nodePanels.put(root,stablePeriodsVisualizer.visualize(context,petrinetHistory.getStablePeriods()));
        System.out.println("Period Counts -- :"+petrinetHistory.getPeriodsCount());
        for (int i=0;i<petrinetHistory.getPeriodsCount();i++) {
            DefaultMutableTreeNode child=new DefaultMutableTreeNode(petrinetHistory.getHistoryPeriod(i));
            root.add(child);
        }
        return root;
    }

    private JComponent createPeriodPanel(PluginContext context, PetrinetHistoryPeriod historyPeriod) {
        JTabbedPane panel=new JTabbedPane();
        PetriNetVisualization petriNetVisualization=new PetriNetVisualization();
        panel.addTab("Petrinet model",petriNetVisualization.visualize(context,historyPeriod.getPetriNet(),
                historyPeriod.getMarking()));
        LogVisualizer logVisualizer=new LogVisualizer();
        panel.addTab("Log Detail",logVisualizer.visualize(context,historyPeriod.getXLog()));
        return panel;
    }
    /*
    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visualize(final PluginContext context,final PetrinetHistory petrinetHistory){
        final JPanel panel=new JPanel(new BorderLayout());
        final JPanel cards=new JPanel(new CardLayout());
        final JComboBox<String> comboBox=new JComboBox<String>();
        comboBox.setEditable(false);
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    CardLayout cl = (CardLayout) (cards.getLayout());
                    cl.show(cards, (String) e.getItem());
                    System.out.println(e.getItem());
                }
            }
        });


        PetriNetVisualization petriNetVisualization=new PetriNetVisualization();

        for(int i=0;i<petrinetHistory.getPeriodsCount();i++){
            comboBox.addItem("Period."+i);
            cards.add(petriNetVisualization.visualize(context,
                    petrinetHistory.getNet(i),
                    petrinetHistory.getMarking(i)),"Period."+i);
            //cards.add(new JLabel(net.getLabel()),net.getLabel());
        }
        panel.add(comboBox,BorderLayout.PAGE_START);
        panel.add(cards, BorderLayout.CENTER);
        panel.updateUI();
        return panel;
    }
    */
}


