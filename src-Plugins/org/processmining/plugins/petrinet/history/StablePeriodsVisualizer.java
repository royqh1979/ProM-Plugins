package org.processmining.plugins.petrinet.history;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.petrinet.PetriNetVisualization;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by Roy on 2014/8/11.
 */
@Plugin(name = "Show Stable Periods",
        parameterLabels = {"StablePeriods"},
        returnLabels = {"Stable Periods Viewer"},
        returnTypes = {JComponent.class},
        userAccessible = false)
@Visualizer
public class StablePeriodsVisualizer {
    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visualize(final PluginContext context,final StablePeriods stablePeriods){
        JPanel panel= new JPanel(new BorderLayout());
        JLabel label=new JLabel(stablePeriods.getCount()+" Stable Periods");
        label.setFont(new Font("Dialog", Font.BOLD, 18));
        JTable table=new JTable(new StablePeriodsTableModel(stablePeriods));

        panel.add(label,BorderLayout.PAGE_START);
        panel.add(new JScrollPane(table),BorderLayout.CENTER);
        return panel;
    }
}
class StablePeriodsTableModel extends AbstractTableModel {
    private StablePeriods stablePeriods;

    StablePeriodsTableModel(StablePeriods stablePeriods) {
        this.stablePeriods = stablePeriods;
    }

    @Override
    public int getRowCount() {
        return stablePeriods.getCount();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "No.";
            case 1:
                return "Start Position";
            case 2:
                return "End Position";
            case 3:
                return "Length";
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
            case 2:
            case 3:
                return Integer.class;
        }
        return Object.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.format("No.%d",rowIndex+1);
            case 1:
                return stablePeriods.getPeriod(rowIndex).getStart();
            case 2:
                return stablePeriods.getPeriod(rowIndex).getEnd();
            case 3:
                return stablePeriods.getPeriod(rowIndex).getLength();
        }
        return "";
    }

}
