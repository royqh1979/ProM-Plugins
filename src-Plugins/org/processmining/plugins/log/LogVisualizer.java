package org.processmining.plugins.log;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

/**
 * 显示XLog相关信息
 * Created by Roy on 2014/9/2.
 */
@Plugin(name = "Show Log Infos",
        parameterLabels = {"Log"},
        returnLabels = {"Log Info Viewer"},
        returnTypes = {JComponent.class},
        userAccessible = false)
@Visualizer
public class LogVisualizer {
    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visualize(final PluginContext context,final XLog log){
        JPanel panel=new JPanel(new BorderLayout());
        JLabel lbInfo=new JLabel();
        lbInfo.setText("Log "+log.getAttributes().get("concept:name")
            +" has "+log.size()+" traces ");

        JTable tbTraces=new JTable(new XTraceTableModel(log));
        panel.add(lbInfo,BorderLayout.NORTH);
        panel.add(new JScrollPane(tbTraces),BorderLayout.CENTER);
        return panel;
    }
}

class XTraceTableModel extends AbstractTableModel {
    private XLog log;

    public XTraceTableModel(XLog log) {
        this.log=log;
    }
    @Override
    public int getRowCount() {
        return log.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return rowIndex+1;
            case 1:
                XTrace trace=log.get(rowIndex);
                return convertTraceToSeq(trace);
        }
        return "";
    }

    private String convertTraceToSeq(XTrace trace) {
        StringBuilder sb=new StringBuilder();
        for (XEvent e: trace) {
            sb.append(e.getAttributes().get("concept:name"));
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "No.";
            case 1:
                return "Trace";
        }
        return "";
    }
}

