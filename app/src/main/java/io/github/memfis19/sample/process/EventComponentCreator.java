package io.github.memfis19.sample.process;

import android.util.Log;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactoryImpl;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.RRule;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.internal.utils.SyncUtils;

/**
 * Created by memfis on 4/2/15.
 */
class EventComponentCreator {

    private final static String TAG = "EventComponentCreator";

    private Event event;

    EventComponentCreator(Event event) {
        this.event = event;
    }

    Component createEventComponent(ComponentFactoryImpl componentFactory) {
        return componentFactory.createComponent(Component.VEVENT, createEventPropertyList());
    }

    private PropertyList createEventPropertyList() {
        PropertyList propertyList = new PropertyList();

        DtStart dtStart = new DtStart(new Date(event.getEventStartDate()));
        Duration duration = new Duration(new Dur(0, 1, 0, 0));

        propertyList.add(dtStart);
        propertyList.add(createRepeatRule(event));
        propertyList.add(duration);

        return propertyList;
    }

    private RRule createRepeatRule(Event event) {
        RRule rRule = new RRule();
        String rRuleString = "";
        try {
            rRuleString = SyncUtils.getRepeatRule(event);
            rRule.setValue(rRuleString);
        } catch (Exception e) {
            Log.e(TAG, "Can't set RRULE value: " + rRuleString);
        }
        return rRule;
    }
}
