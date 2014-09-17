package org.processmining.plugins.petrinet.history;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * Created by Roy on 2014/9/17.
 */
public class PetrinetHistoryPeriod {
    final Petrinet net;
    final Marking marking;
    final TimePeriod period;
    final String name;
    final XLog log;

    PetrinetHistoryPeriod(TimePeriod timePeriod,Petrinet net,Marking marking,XLog log) {
        this(null,timePeriod,net,marking,log);
    }
    PetrinetHistoryPeriod(String name,TimePeriod period,Petrinet net,Marking marking,XLog log){
        this.net=net;
        this.marking=marking;
        this.log=log;
        this.period=period;
        if (name==null) {
            this.name=String.format("Period from %d to %d",period.getStart(),period.getEnd());
        } else {
            this.name=name;
        }
    }

    public TimePeriod getTimePeriod() {
        return period;
    }

    public Petrinet getPetriNet(){
        return net;
    }

    public Marking getMarking() {
        return marking;
    }

    public XLog getXLog() {
        return log;
    }

    @Override
    public String toString() {
        return name;
    }
}
