# Cadar
Android solution which represents month and list calendars views and possibility to display events: just set of events and recurrent as well, but with limitations. For events processing I was using <a href="https://github.com/ical4j/ical4j">Ical4J</a> library.</br>
<img src="https://github.com/memfis19/Cadar/blob/master/art/list_calendar.png" width="200px" /> <img src="https://github.com/memfis19/Cadar/blob/master/art/month_calendar.png" width="200px" /> <img src="https://github.com/memfis19/Cadar/blob/master/art/interaction_anim.gif" width=200px/>

## Example of most common using
1. Add to your layout file:
```
 <io.github.memfis19.cadar.view.MonthCalendar
        android:id="@+id/monthCalendar"
        android:layout_width="match_parent"
        android:layout_height="300dp" />
```
2. Using in code:
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
#### For more specific using please look at sample project.

## How to add to your project?
```
compile 'io.github.memfis19:cadar:0.1.0'
```

## Know issue
-Library has not release yet, so it will be extended in future.</br>
-Not full documentation will be fixed soon.

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
