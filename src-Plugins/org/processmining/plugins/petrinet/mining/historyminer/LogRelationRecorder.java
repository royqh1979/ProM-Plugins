package org.processmining.plugins.petrinet.mining.historyminer;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Roy on 2014/8/12.
 */
public class LogRelationRecorder {
    Map<Pair<XEventClass, XEventClass>, Integer> followRelationCount =
            new HashMap<Pair<XEventClass, XEventClass>, Integer>();
    Map<Pair<XEventClass, XEventClass>, Integer> followRelationLastPos =
            new HashMap<Pair<XEventClass, XEventClass>, Integer>();

    public void record(Set<Pair<XEventClass, XEventClass>> newPairs, int pos) {
        addCounts(newPairs);
        recordLastPositions(newPairs, pos);
    }

    public void clear() {
        followRelationCount.clear();
        followRelationLastPos.clear();
    }

    private void recordLastPositions(Set<Pair<XEventClass, XEventClass>> newPairs, int pos) {
        for (Pair<XEventClass,XEventClass> pair:newPairs) {
            recordLastPos(pair,pos);
        }
    }

    private void recordLastPos(Pair<XEventClass, XEventClass> pair, int pos) {
        followRelationLastPos.put(pair,pos);
    }

    private void addCounts(Set<Pair<XEventClass, XEventClass>> newPairs) {
        for (Pair<XEventClass, XEventClass> pair : newPairs) {
            addCount(pair);
        }
    }

    private void addCount(Pair<XEventClass, XEventClass> pair) {
        int c = followRelationCount.getOrDefault(pair,0);
        followRelationCount.put(pair, c + 1);
    }

    public Set<Pair<XEventClass,XEventClass>> getFollowRelations() {
        return followRelationCount.keySet();
    }

    public int getCount(Pair<XEventClass, XEventClass> p) {
        return followRelationCount.getOrDefault(p,0);
    }

    public int getLastPosition(Pair<XEventClass, XEventClass> p) {
        return followRelationLastPos.get(p);
    }
}