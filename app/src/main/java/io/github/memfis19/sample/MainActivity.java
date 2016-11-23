package io.github.memfis19.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        findViewById(R.id.demo_month_calendar).setOnClickListener(this);
        findViewById(R.id.demo_list_calendar).setOnClickListener(this);
        findViewById(R.id.demo_interaction_calendar).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.demo_month_calendar:
                Intent monthCalendar = new Intent(this, MonthCalendarActivity.class);
                startActivity(monthCalendar);
                break;
            case R.id.demo_list_calendar:
                Intent listCalendar = new Intent(this, ListCalendarActivity.class);
                startActivity(listCalendar);
                break;
            case R.id.demo_interaction_calendar:
                Intent interactionActivity = new Intent(this, MonthListCalendarInteractionActivity.class);
                startActivity(interactionActivity);
                break;
        }
    }
}
