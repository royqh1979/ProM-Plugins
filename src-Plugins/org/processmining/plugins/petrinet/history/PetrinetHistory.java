package org.processmining.plugins.petrinet.history;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Roy on 2014/8/11.
 */
public class PetrinetHistory {
    private List<PetrinetHistoryPeriod> historyPeriods;
    private StablePeriods stablePeriods=null;

    public PetrinetHistory(StablePeriods stablePeriods){
        historyPeriods=new ArrayList<PetrinetHistoryPeriod>();
        this.stablePeriods=stablePeriods;
    }

    public void addPeriod(TimePeriod period, Petrinet net, Marking marking,XLog log) {
        PetrinetHistoryPeriod historyPeriod=new PetrinetHistoryPeriod(period,net,marking,log);
        historyPeriods.add(historyPeriod);
    }

    public int getPeriodsCount() {
        return historyPeriods.size();
    }

    public PetrinetHistoryPeriod getHistoryPeriod(int i) {
        return historyPeriods.get(i);
    }

    public StablePeriods getStablePeriods() {
        return stablePeriods;
    }

}


