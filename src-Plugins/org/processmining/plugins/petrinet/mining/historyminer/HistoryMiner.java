package org.processmining.plugins.petrinet.mining.historyminer;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.*;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.log.logabstraction.LogRelations;
import org.processmining.plugins.petrinet.history.PetrinetHistory;
import org.processmining.plugins.petrinet.history.StablePeriods;
import org.processmining.plugins.petrinet.history.TimePeriod;
import org.processmining.plugins.petrinet.mining.alphaminer.AlphaMiner;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Roy on 2014/8/12.
 */
@Plugin(name = "History Miner", parameterLabels = { "Log" },
        returnLabels = { "PetrinetHistory"}, returnTypes = { PetrinetHistory.class })
public class HistoryMiner {
    @UITopiaVariant(uiLabel = "Mine for a Workflow history using SPA algorithms", affiliation = UITopiaVariant.EHV, author = "B.F. van Dongen", email = "b.f.v.dongen@tue.nl", pack = "AlphaMiner")
    @PluginVariant(variantLabel = "Log", requiredParameterLabels = { 0 })
    public  PetrinetHistory mine(PluginContext context, XLog log) {
        SPAAnalyzer analyzer=new SPAAnalyzer();
        StablePeriods stablePeriods=analyzer.analyze(context, log);
        PetrinetHistory petrinetHistory=new PetrinetHistory(stablePeriods);

        System.out.println("Found Periods:"+stablePeriods.getCount());

        XFactory factory=new XFactoryBufferedImpl();

        PluginParameterBinding alphaMiner=null;
        alphaMiner = findAlphaMiner(context);
        if (alphaMiner==null) {
            context.log("AlphaMiner not found",
                    Logger.MessageLevel.ERROR);
            return null;
        }

        for (int i=0;i<stablePeriods.getCount();i++) {
            TimePeriod timePeriod=stablePeriods.getPeriod(i);
            //每TimePeriod生成一个单独的Log集以便挖掘
            XLog newLog=factory.createLog(log.getAttributes());
            newLog.getClassifiers().addAll(log.getClassifiers());
            newLog.getExtensions().addAll(log.getExtensions());

            newLog.getGlobalTraceAttributes().add((XAttribute) XConceptExtension.ATTR_NAME.clone());
            newLog.getGlobalEventAttributes().add((XAttribute) XConceptExtension.ATTR_NAME.clone());

            System.out.println(timePeriod.getStart() + " -- " + timePeriod.getEnd());
            for (int t=timePeriod.getStart();t<=timePeriod.getEnd();t++) {
                newLog.add(log.get(t));
            }


            try {
                //调用AlphaMiner进行挖掘
                PluginContext childContext = context.createChildContext("child");
                PluginExecutionResult executionResult = alphaMiner.invoke(childContext, newLog);
                executionResult.synchronize();
                Object[] objects=executionResult.getResults();
                petrinetHistory.addPeriod(timePeriod,(Petrinet)objects[0],(Marking)objects[1],newLog);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return petrinetHistory;

    }

    /**
     * 从系统中注册的Plugin里寻找AlphaMiner
     *
     * 注意，如果想直接new AlphaMiner实例并调用其方法，必须手动创建合适的context，否则会出错
     * @param context
     * @return
     */
    private PluginParameterBinding findAlphaMiner(PluginContext context) {
        Collection<Pair<Integer, PluginParameterBinding>> plugins = context.getPluginManager().find(Plugin.class,
                Petrinet.class, context.getPluginContextType(), true, true, true, XLog.class);
        if (plugins.isEmpty()) {
            return null;
        }


        for (Pair<Integer, PluginParameterBinding> plugin:plugins) {
            String pluginId=plugin.getSecond().getPlugin().getID().toString();
            if (pluginId.equals("org.processmining.plugins.petrinet.mining.alphaminer.AlphaMiner")){
                return plugin.getSecond();
            }
        }
        return null;
    }
}
