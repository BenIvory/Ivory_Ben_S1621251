package org.me.gcu.mpdcoursework;
//Ben Ivory S1621251

//Android Stuff
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;


// Java Stuff
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


//Parsing Stuff
import org.xmlpull.v1.XmlPullParser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MyAdapter.ItemClickListener
{

    private String result;

    //Buttons
    private Button startButton;
    private Button plotButton;

    //Search bar
    private EditText searchBox;

    //DateBox
    private EditText dateBox;

    // Traffic Scotland URLs
    private String urlSource1 = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String urlSource2 = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String urlSource3 = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String empty = "empty";


    private List<TrafficAccident> traffics = new ArrayList<>();
    private List<TrafficAccident> trafficsWorking = new ArrayList<>();
    private List<TrafficAccident> trafficsSearched = new ArrayList<>();

    //Recycler Stuff
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;


    //Feed Selection
    private Checkboxes checkboxes;


    private DatePickerDialog datePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        plotButton = findViewById(R.id.buttonPlot);

        checkboxes = new Checkboxes();


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, checkboxes)
                .commit();

        searchBox = findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Done();
            }
        });




        dateBox = findViewById(R.id.dateBox);
        dateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                datePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateBox.setText("" + dayOfMonth + "/" + month + "/" + year);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK));

                datePicker.show();
            }
        });

        dateBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { Done(); }
        });


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(this, traffics, this);
        recyclerView.setAdapter(adapter);


        //Plot
        plotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tried to do this, but ran into maximum size limitations.
                    //Intent intent = new Intent(MainActivity.this, PlotActivity.class);
                    //intent.putExtra("PlotTrafficAccidents", (Serializable) trafficsSearched);
                    //startActivity(intent);

                PlotActivity.trafficAccidents = trafficsSearched;
                Intent intent = new Intent(MainActivity.this, PlotActivity.class);
                startActivity(intent);
            }
        });


        //Tests
        //TestSearch();
    }

    public void onClick(View aview)
    {
        startButton.setEnabled(false);
        startProgress();

    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        //new Thread(new Task(urlSource)).start();


        //traffics.clear();
        //adapter.notifyDataSetChanged();

        String[] newUrls = new String[3];

        if (checkboxes.isChecked(0))
            newUrls[0] = urlSource1;
        else
            newUrls[0] = "";

        if (checkboxes.isChecked(1))
            newUrls[1] = urlSource2;
        else
            newUrls[1] = "";

        if (checkboxes.isChecked(2))
            newUrls[2] = urlSource3;
        else
            newUrls[2] = "";

        trafficsWorking.clear();
        new Thread(new Task(newUrls)).start();
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.e("Recycler", "" + position);

        //Skip "No results found." traffic accident.
        if (traffics.get(position).title == "No results found.")
            return;

        Intent intent = new Intent(this, ExtraInfoActivity.class);
        intent.putExtra("TrafficAccident", (Serializable) traffics.get(position));
        startActivity(intent);
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String[] urls;

        public Task(String[] urls)
        {
            this.urls = urls;
        }
        @Override
        public void run()
        {

            for (int i = 0; i < urls.length; i ++) {
                if (urls[i] != "") {
                    trafficsWorking.addAll(Get(urls[i], i));
                }

            }

            for (int i = 0; i < trafficsWorking.size(); i++) {
                String findStartDate = "Start Date: ";
                String findEndDate = "End Date: ";
                int startDateIndex = -1;
                int endDateIndex = -1;

                String description = trafficsWorking.get(i).description;

                startDateIndex = description.indexOf(findStartDate);
                endDateIndex = description.indexOf(findEndDate);

                String startDateText = "";
                String endDateText = "";
                if (startDateIndex > -1)
                    startDateText = description.substring(12, endDateIndex);
                if (endDateIndex > -1)
                    endDateText = description.substring(endDateIndex + 10);

                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy - hh:mm");

                Calendar startDate = Calendar.getInstance();
                if (startDateText != "")
                    startDate.setTime(sdf.parse(startDateText, new ParsePosition(0)));
                Calendar endDate = Calendar.getInstance();
                if (endDateText != "")
                    endDate.setTime(sdf.parse(endDateText, new ParsePosition(0)));

                trafficsWorking.get(i).calendarStart = startDate;
                trafficsWorking.get(i).calendarEnd = endDate;
                trafficsWorking.get(i).time = (endDate.getTimeInMillis() - startDate.getTimeInMillis()) / 1000 / 60 / 60 / 24;

                //Parse geo location;
                int space = trafficsWorking.get(i).geoPoint.indexOf(" ");
                String latitude = trafficsWorking.get(i).geoPoint.substring(0, space);
                String longitude = trafficsWorking.get(i).geoPoint.substring(space + 1);

                float lat = Float.parseFloat(latitude);
                float lng = Float.parseFloat(longitude);

                trafficsWorking.get(i).latitude = lat;
                trafficsWorking.get(i).longitude = lng;
            }

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                    Done();
                }
            });
        }
    }


    private List<TrafficAccident> Get(String aurl, int type) {
        List<TrafficAccident> list = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        InputStream stream = null;
        try {
            // auto-detect the encoding from the stream
            stream = new URL(aurl).openConnection().getInputStream();
            parser.setInput(stream, null);

            int eventType = parser.getEventType();
            boolean done = false;
            TrafficAccident item = null;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item")) {
                            item = new TrafficAccident();
                        } else if (item != null) {
                            if (name.equalsIgnoreCase( "title")) {
                                item.title = parser.nextText();
                            } else if (name.equalsIgnoreCase( "description")) {
                                item.description = parser.nextText();
                            } else if (name.equalsIgnoreCase( "point")) {
                                item.geoPoint = parser.nextText();
                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item") && item != null) {
                            item.type = type;
                            list.add(item);
                        } else if (name.equalsIgnoreCase("channel")) {
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private void Done() {
        traffics.clear();
        trafficsSearched.clear();

        Search();
        
        traffics.addAll(trafficsSearched);

        if (traffics.size() <= 0) {
            TrafficAccident ta = new TrafficAccident();
            ta.title = "No results found.";

            traffics.add(ta);
        }

        adapter.notifyDataSetChanged();
        layoutManager.scrollToPosition(0);

        startButton.setEnabled(true);
    }

    private void LogMe(String string) {
        Log.e("___Parse___", string);
    }


    private void Search() {
        String search = searchBox.getText().toString().toLowerCase();
        String date = dateBox.getText().toString();

        if (searchBox.getText().toString().length() <= 0 && dateBox.getText().toString().length() <= 0) {
            trafficsSearched.addAll(trafficsWorking);
            return;
        }

        for (int i = 0; i < trafficsWorking.size(); i++) {
            boolean foundSoFar = true;

            if (searchBox.getText().toString().length() > 0) {
                if (!(trafficsWorking.get(i).title.toLowerCase().contains(search) || trafficsWorking.get(i).description.toLowerCase().contains(search)))
                    foundSoFar = false;
            }

            if (dateBox.getText().toString().length() > 0 && foundSoFar) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Calendar searchDate = Calendar.getInstance();
                Date dateDate = simpleDateFormat.parse(dateBox.getText().toString(), new ParsePosition(0));
                if (dateDate != null)
                {
                    searchDate.setTime(dateDate);

                    if (!(trafficsWorking.get(i).calendarStart.compareTo(searchDate) <= 0 && trafficsWorking.get(i).calendarEnd.compareTo(searchDate) >= 0))
                        foundSoFar = false;
                }
            }

            if (foundSoFar)
                trafficsSearched.add(trafficsWorking.get(i));
        }
    }


    private void TestSearch() {
        searchBox.setText("M9");
        dateBox.setText("19/04/2020");

        trafficsSearched.clear();
        trafficsWorking.clear();

        TrafficAccident ta = new TrafficAccident();
        ta.title = "Accident on the M8.";

        ta.calendarStart = Calendar.getInstance();
        ta.calendarStart.set(Calendar.DAY_OF_MONTH, 18);
        ta.calendarStart.set(Calendar.HOUR_OF_DAY, 0);
        ta.calendarStart.set(Calendar.MINUTE, 0);
        ta.calendarStart.set(Calendar.SECOND, 0);
        ta.calendarStart.set(Calendar.MILLISECOND, 0);

        ta.calendarEnd = Calendar.getInstance();
        ta.calendarEnd.set(Calendar.DAY_OF_MONTH, 18);
        ta.calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
        ta.calendarEnd.set(Calendar.MINUTE, 0);
        ta.calendarEnd.set(Calendar.SECOND, 0);
        ta.calendarEnd.set(Calendar.MILLISECOND, 0);

        trafficsWorking.add(ta);

        Done();

        if (trafficsSearched.size() == 1)
            Log.e("BlackBox", "test passed");
        else
            Log.e("BlackBox", "test failed");

    }
}
