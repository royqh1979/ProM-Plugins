package org.processmining.plugins.petrinet.history;

import java.util.ArrayList;

/**
 * Created by Roy on 2014/8/12.
 */
public class StablePeriods {
    ArrayList<TimePeriod> periods=new ArrayList<TimePeriod>();

    public void add(TimePeriod period){
        periods.add(period);
    }
    public void add(int start, int end) {
        add(new TimePeriod(start,end));
    }

    public TimePeriod getPeriod(int index) {
        return periods.get(index);
    }

    public int getCount(){
        return periods.size();
    }
}
