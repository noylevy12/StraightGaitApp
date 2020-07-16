package com.example.straightgaitapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragment extends Fragment {
    private static final String TAG = "StatisticsFragment";
    TextView textViewStatistics, textViewAngles;
    ListView listViewMoves;
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    MovementAdapter movementAdapter;
    RecyclerView fireStoreList;
    private FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;

    private String userId;
    private DocumentReference usersDocRef, legMovmentsDocRef;
    private LineChart lineChartZeroStatus, lineChartOneStatus, lineChartAngleAvg;
    private SimpleDateFormat dayAndMonthNoSpaceFormat;
    List<Map<String, Object>> userStatusOneMovements,userStatusZeroMovements, userMovementsList;

    private List<String> angels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        listViewMoves = (ListView) rootView.findViewById(R.id.listViewMoves);
        movementAdapter = new MovementAdapter(getContext(), R.layout.list_item_single);
        listViewMoves.setAdapter(movementAdapter);
        dayAndMonthNoSpaceFormat = new SimpleDateFormat("dd MMM", new Locale("en"));

        // ----------------------------------    line chart    -----------------------------------------------------
        lineChartZeroStatus = (LineChart) rootView.findViewById(R.id.lineChartLegStatusZero);
        lineChartOneStatus = (LineChart) rootView.findViewById(R.id.lineChartLegStatusOne);
        lineChartAngleAvg = (LineChart) rootView.findViewById(R.id.lineChartAngleAvg);

        // ---------------------------------    end line chart    ---------------------------------------------------

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        legMovmentsDocRef = db.collection("LegMovements").document(userId);


        legMovmentsDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userMovementsList = (ArrayList<Map<String, Object>>) document.get("movements");
                        userStatusOneMovements = new ArrayList<>();
                        userStatusZeroMovements = new ArrayList<>();
                        for (Map<String, Object> entry : userMovementsList) {
                            for (String key : entry.keySet()) {
                                if(key.equals("status")){
                                    long status = (long) entry.get(key);
                                    if (status == 1){
                                        userStatusOneMovements.add(entry);
                                    } else {
                                        userStatusZeroMovements.add(entry);
                                    }
                                }
                            }
                        }
                        HashMap<String, Integer> statusOnePerDay = new HashMap<String, Integer>();
                        HashMap<String, Integer> statusZeroPerDay = new HashMap<String, Integer>();
                        HashMap<String, Integer> sampleAvgPerDay = new HashMap<String, Integer>();

                        statusOnePerDay = countStatusPerDay(userStatusOneMovements);
                        statusZeroPerDay = countStatusPerDay(userStatusZeroMovements);
                        sampleAvgPerDay = avgAngleSample(userMovementsList);

//                        Log.d(TAG, "statusOnePerDay"+statusOnePerDay.toString());
//                        Log.d(TAG, "statusZeroPerDay"+statusZeroPerDay.toString());
//                        Log.d(TAG, "sampleAvgPerDay"+sampleAvgPerDay.toString());

                        ArrayList<Entry> yValues = new ArrayList<>();
                        createChart(rootView, sampleAvgPerDay, "average of sample angle", lineChartAngleAvg);
                        createChart(rootView, statusZeroPerDay, "bad angles sample per day", lineChartZeroStatus);
                        createChart(rootView, statusOnePerDay, "good angles sample per day", lineChartOneStatus);
                        if (document.get("movements") != null) {
                            json_string = document.getData().toString();
                            try {
                                jsonObject = new JSONObject(json_string);
                                jsonArray = jsonObject.getJSONArray("movements");
                                int count = 0;
                                String angle;
                                Long date;
                                int status;
                                while (count< jsonArray.length()){
                                    JSONObject JO = jsonArray.getJSONObject(count);
                                    angle = JO.getString("angle");
                                    date = JO.getLong("date");
                                    status = JO.getInt("status");
                                    Movement movement = new Movement(angle, date, status);
//                                    Toast.makeText(getActivity(), movement.toString(), Toast.LENGTH_LONG).show();

                                    movementAdapter.add(movement);
                                    count++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            //Create the filed
                        }
                    }
                }
            }
        });

        return rootView;


    }

    public class MovementAdapter extends ArrayAdapter{

        List list = new ArrayList();
        public MovementAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }


        public void add(@Nullable Movement object) {
            super.add(object);
            list.add(object);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Nullable
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            view = convertView;
            MovementHolder movementHolder;
            if(view == null){
                LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.list_item_single, parent,false);
                movementHolder = new MovementHolder();
                movementHolder.textViewAngle =(TextView) view.findViewById(R.id.textViewAngle);
                movementHolder.textViewDate =(TextView) view.findViewById(R.id.textViewDate);
                view.setTag(movementHolder);

            }else {
                movementHolder = (MovementHolder)view.getTag();
            }

            Movement movement = (Movement)this.getItem(position);
            movementHolder.textViewAngle.setText("Angle: " +movement.getAngle());
            movementHolder.textViewDate.setText("Time: "+ movement.getDateStr(movement.getDate()));
            if(movement.getStatus() == 1 ){
                movementHolder.textViewAngle.setTextColor(Color.RED);
            }else {
                movementHolder.textViewAngle.setTextColor(Color.GREEN);
            }

            return view;
        }


    }
    static class MovementHolder{
        TextView textViewAngle, textViewDate;

    }


    private HashMap<String, Integer> avgAngleSample(List<Map<String, Object>> queryList) {
        HashMap<String, Integer> countSampleMap = new HashMap<String, Integer>();
        HashMap<String, Integer> sumAngleMap = new HashMap<String, Integer>();
        HashMap<String, Integer> res = new HashMap<String, Integer>();

        int countSample = 1, sumAngle = 0, angle;
        String dateStr;

        for (Map<String, Object> entry : queryList) {
            for (String key : entry.keySet()) {
                if(key.equals("date")){
                    dateStr = getDate((long) entry.get(key));
                    angle = Integer.parseInt((String) entry.get("angle"));
                    if(countSampleMap.containsKey(dateStr)){
                        countSample = countSampleMap.get(dateStr);
                        countSample++;
                        countSampleMap.put(dateStr, countSample);
                    }else {
                        countSampleMap.put(dateStr, 1);
                        res.put(dateStr,1);
                    }
                    if(sumAngleMap.containsKey(dateStr)){
                        sumAngle = angle + sumAngleMap.get(dateStr);
                        sumAngleMap.put(dateStr, sumAngle);
                    }
                    else {
                        sumAngleMap.put(dateStr, angle);
                    }

                    res.put(dateStr,sumAngle/countSample);
                }
            }
        }
        return res;

    }

    private HashMap<String, Integer> countStatusPerDay(List<Map<String, Object>> queryList) {
        HashMap<String, Integer> res = new HashMap<String, Integer>();
        for (Map<String, Object> entry : queryList) {
            for (String key : entry.keySet()) {
                if(key.equals("date")){
                    String date = getDate((long) entry.get(key));
//                  Log.d(TAG, "date: "+ date);
                    if(res.containsKey(date)){
                        int count = res.get(date);
                        count++;
                        res.put(date, count);
                    }else {
                        res.put(date, 1);
                    }
                }
            }
        }
        return res;
    }

    private void createChart(View rootView, HashMap<String, Integer> queryList, String chartName, LineChart lineChart) {
        ArrayList<Entry> yValues = new ArrayList<>();
        int count =0;
        String[] xValues = new String[queryList.size()];
        for (Map.Entry<String, Integer> entry : queryList.entrySet()) {
            String key = entry.getKey();
            xValues[count] = key;
            Integer value = entry.getValue();

            yValues.add(new Entry(count,value));
            count++;
        }

        renderChart(rootView, yValues, xValues, chartName, lineChart);
    }

    private void renderChart(View rootView, ArrayList<Entry> yValues, String[] xValues,  String chartName, LineChart lineChart ) {
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPadding(20,20,20,20);
        lineChart.getDescription().setEnabled(false);

        //set values
        LineDataSet set1 = new LineDataSet(yValues, chartName);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setGranularity(1);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextSize(12f);
        leftAxis.setYOffset(0.2f);

        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);

        set1.setFillAlpha(110);
        set1.setLineWidth(4f);
        set1.setCircleColor(Color.parseColor("#E576FF"));
        set1.setValueTextSize(10f);
        set1.setDrawCircleHole(false);
        set1.setCircleRadius(4f);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);
        lineChart.setExtraOffsets(20,20,30,20);

        lineChart.setData(data);
        lineChart.invalidate();
        lineChart.animateXY(2000, 2000);
    }

    private String getDate(long time) {
        Date d = new Date(time*1000);
        String date = dayAndMonthNoSpaceFormat.format(d);
        return date;
    }

    public class DateAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
        private String[] values;
        public DateAxisValueFormatter(String[] values){
            this.values  = values;
        }


        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return values[(int)value];
        }
    }

}
