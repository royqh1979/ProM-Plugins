package org.processmining.plugins.petrinet.mining.historyminer;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.Pair;
import org.processmining.plugins.petrinet.history.StablePeriods;
import org.processmining.plugins.petrinet.history.TimePeriod;

import java.util.*;

@Plugin(
        name="Stable Period Analyzer",
        parameterLabels = {"Log"},
        returnLabels = { "StablePeriods"},
        returnTypes = {StablePeriods.class}
)
public class SPAAnalyzer {
	static int NI=50;
	static int NP=30;
	static double newPairPro=0.05;
	static double delta=0.0000005;  //continuously not appear 20 times when prior appearing prob is 1/2
	static int minAppear;

    static {
        double p=(1-newPairPro);
        int n=1;
        while (p>delta) {
            n++;
            p*=(1-newPairPro);
        }
        System.out.println("N="+n);
        minAppear=n;
    }

    private Set<Pair<XEventClass,XEventClass>> getFollowRelations(XTrace trace, XEventClasses classes) {
        Set<Pair<XEventClass,XEventClass>> relations=new HashSet<Pair<XEventClass, XEventClass>>();

        XEventClass from=classes.getClassOf(trace.get(0));
        XEventClass to;

        for (int i=1;i<trace.size();i++) {
            to=classes.getClassOf(trace.get(i));
            relations.add(new Pair<XEventClass,XEventClass>(from,to));
            from=to;
        }
        return relations;
    }

    @UITopiaVariant(uiLabel = "Analyze Stable Periods using SPA algorithm",
            affiliation = "Beijing Forest University", author = "Qu Hua", email = "quhua@bjfu.edu.cn", pack = "HistoryMiner")
    @PluginVariant(requiredParameterLabels = { 0 })
    public StablePeriods analyze(PluginContext context, XLog log) {
		StablePeriods stablePeriods=new StablePeriods();
		int start=0; //Start of the current Stable Period
		int end=0; //End of the current Stable Period
		int length=0; //length of the current Stable Period
        LogRelationRecorder recorder=new LogRelationRecorder();
        XLogInfo info= XLogInfoFactory.createLogInfo(log);
        XEventClasses classes=info.getEventClasses();

        System.out.println("Found logs:"+log.size());
		for (int i=0;i<log.size();i++) {
			XTrace trace=log.get(i);
			Set<Pair<XEventClass,XEventClass>> newPairs=getFollowRelations(trace,classes);
			if (length<NP) {
				end=i;
				length++;
                recorder.record(newPairs, i);
				continue;
			}

			if (newTaskPairAppearTestFailed(recorder,newPairs,length)) {
				TimePeriod T=new TimePeriod(start+NP,end-NP);
				System.out.println("find new:"+T);
				if (T.getLength()>NI) {
					stablePeriods.add(T);
				}
				recorder.clear();
				start=i;
				end=i;
				length=1;
                recorder.record(newPairs,i);
				continue;
			} 
			
			if (oldTaskPairAppearTestFailed(recorder,newPairs,length,i)) {
				TimePeriod T=new TimePeriod(start+NP,end-NP);
				System.out.println("find old:"+T);
				if (T.getLength()>NI) {
					stablePeriods.add(T);
				}
				recorder.clear();
				start=i;
				end=i;
				length=1;
                recorder.record(newPairs, i);
				continue;
			}
			end=i;
			length++;
            recorder.record(newPairs, i);
		}
		
		TimePeriod T=new TimePeriod(start+NP,end);
		if (T.getLength()>NI) {
			stablePeriods.add(T);
		}	
		
		return stablePeriods;
	}

	private boolean oldTaskPairAppearTestFailed(LogRelationRecorder recorder,
            Set<Pair<XEventClass, XEventClass>> newPairs,
			int length,int pos) {
		for (Pair<XEventClass, XEventClass> p:recorder.getFollowRelations()) {
			if (newPairs.contains(p)){
				continue;
			}
			double prob=(double)recorder.getCount(p) /length;
			int n=pos-recorder.getLastPosition(p);
			if (Math.pow((1-prob),n) < delta) {
                System.out.println(recorder.getCount(p)+"**"+length+"**"+prob);
				System.out.println(p+" "+prob+" "+n);
				return true;
			}
		}
		return false;
	}

	private boolean newTaskPairAppearTestFailed(LogRelationRecorder recorder,
			Set<Pair<XEventClass, XEventClass>> newTaskPairs,
			int length) {
		for (Pair<XEventClass, XEventClass> p:newTaskPairs){
			if ((recorder.getCount(p)==0) && (length>minAppear)) {
				return true;
			}

		}
		return false;
	}


}
