# Cadar

[ ![Download](https://api.bintray.com/packages/m-e-m-f-i-s/io.github.memfis19/cadar/images/download.svg) ](https://bintray.com/m-e-m-f-i-s/io.github.memfis19/cadar/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Cadar-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/4783)

Android solution which represents month and list calendars views and possibility to display events: just set of events and recurrent as well, but with limitations. For events processing I was using <a href="https://github.com/ical4j/ical4j">Ical4J</a> library.</br>
<img src="https://github.com/memfis19/Cadar/blob/master/art/list_calendar.png" width="200px" /> <img src="https://github.com/memfis19/Cadar/blob/master/art/month_calendar.png" width="200px" /> <img src="https://github.com/memfis19/Cadar/blob/master/art/interaction_anim.gif" width=200px/>

## Example of most common using:
##### Add to your layout:

```
 <io.github.memfis19.cadar.view.MonthCalendar
        android:id="@+id/monthCalendar"
        android:layout_width="match_parent"
        android:layout_height="300dp" />
```
##### Using in code:
```
MonthCalendarConfiguration.Builder builder = new MonthCalendarConfiguration.Builder(this);
monthCalendar.setCalendarPrepareCallback(this);
monthCalendar.prepareCalendar(builder.build());

monthCalendar.setOnDayChangeListener(new OnDayChangeListener() {
    @Override
    public void onDayChanged(Calendar calendar) {
        Toast.makeText(MonthCalendarActivity.this, calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
      }
    });
monthCalendar.setOnMonthChangeListener(new OnMonthChangeListener() {
    @Override
    public void onMonthChanged(Calendar calendar) {
        Toast.makeText(MonthCalendarActivity.this, calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
      }
    });
...

@Override
public void onCalendarReady(CalendarController calendar) {
   monthCalendar.displayEvents(events, new DisplayEventCallback() {
   @Override
     public void onEventsDisplayed() {

     }
 });
}
```
##### Don't forget to release calendar:
```
@Override
protected void onDestroy() {
   super.onDestroy();
   monthCalendar.releaseCalendar();
}
```
#### For more specific using please look at sample project.

## Events processing:
Events processing implemented in most simple way, each event has start date - date of start and end date - date of event end. Please notice that event does not have duration. so there is not exist stop date. Each event next periods for recurrency processting: none, every week, every 2 week, every 3 week, every 4 week, every month, every year. If event does npt have end date and repeat period set to none, that will single event, in case even has any other repeat period without end date, it will be endless event. 

Month and list calendars limited in time, by defualt they displayed for 3 years. You are able to extend it via calendar configurations. In case if you want to use list and month calendars coupled, then you need to keep the same configuration settings for them.

#### One more important thing to know about event processing
In some cases during event processing, periods of calculaction may overlap and in this case events can be duplicated, to avoid this please override ```equals()``` and ```hashCode()``` methods in your model class which implements ```Event```. For more details please look at sample source code.

## How to add to your project?
```
compile 'io.github.memfis19:cadar:0.2.0'
```

## Know issues
-Library has not release yet, so it will be extended in future.</br>
-Not full documentation will be fixed soon.

## Roadmap
-Add more documentation;</br>
-Extend event's recurring settings;</br>
-Add more tools to customize list calendar UI.

## Bugs and Feedback
For bugs, feature requests, and discussion please use <a href="https://github.com/memfis19/Cadar/issues">GitHub Issues</a>.

# [LICENSE](/LICENSE.md)

###### MIT License

###### Copyright (c) 2016 Rodion Surzhenko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
