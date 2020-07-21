package com.straightgait.straightgaitapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    Button btnEditProfile;
    private FirebaseFirestore db;
    private String userId;
    private DocumentReference usersDocRef, legMovmentsDocRef;
    private String userName, gender, lastDate, firstDate;
    private Timestamp lastDateTS, firstDateTS;
    private Date lastDateD, firstDateD;
    private PieChart pieChartAngle;
    SimpleDateFormat monthAndYearFormat,dayAndMonthFormat, dayAndMonthNoSpaceFormat;
    ImageView imageViewUser;
    TextView textViewUserName, textViewStartDate,textViewLastDate;
    List<Map<String, Object>> userStatusOneMovements,userStatusZeroMovements, userMovementsList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        imageViewUser = (ImageView) rootView.findViewById(R.id.imageViewUser);
        textViewUserName = (TextView) rootView.findViewById(R.id.textViewUserName);
        textViewStartDate = (TextView) rootView.findViewById(R.id.textViewStartDate);
        textViewLastDate = (TextView) rootView.findViewById(R.id.textViewLastDate);
        btnEditProfile = (Button) rootView.findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment updateProfileFragment = new UpdateProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.fragmant_container, updateProfileFragment);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }
        });




        // ----------------------------------    chart    -----------------------------------------------------
//        lineChartAngleAvg = (LineChart) rootView.findViewById(R.id.lineChartAngleAvg);
        pieChartAngle = (PieChart) rootView.findViewById(R.id.pieChartAngle);

       // ---------------------------------    end chart    ---------------------------------------------------

       monthAndYearFormat = new SimpleDateFormat("MMM\nyyyy", new Locale("en"));
       dayAndMonthFormat = new SimpleDateFormat("dd\nMMM", new Locale("en"));
       dayAndMonthNoSpaceFormat = new SimpleDateFormat("dd MMM", new Locale("en"));
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        usersDocRef = db.collection("users").document(userId);
        legMovmentsDocRef = db.collection("LegMovements").document(userId);
        setUiUserInfo();


        return rootView;

    }
    private void setUiUserInfo() {
        usersDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    userName = documentSnapshot.getString("firstName");
                    gender = documentSnapshot.getString("gender");
                    lastDateTS = documentSnapshot.getTimestamp("lastDate");
                    firstDateTS = documentSnapshot.getTimestamp("firstDate");

                    lastDateD = lastDateTS.toDate();
                    firstDateD = firstDateTS.toDate();

                    firstDate = monthAndYearFormat.format(firstDateD);
                    lastDate = dayAndMonthFormat.format(lastDateD);


                    if(gender.equals("female")){
                        imageViewUser.setImageResource(R.drawable.icon_green_female);
                    }else if(gender.equals("male")){
                        imageViewUser.setImageResource(R.drawable.icon_green_male);
                    }
                    textViewUserName.setText(userName);
                    textViewStartDate.setText(firstDate);
                    textViewLastDate.setText(lastDate);

                }
                else {

                }

                legMovmentsDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                userMovementsList = (ArrayList<Map<String, Object>>) document.get("movements");

                                List<Map<String, Object>> lastDateMovment = new ArrayList<Map<String, Object>>();
                                lastDateMovment = filterByLastDate(userMovementsList);
                                HashMap<String, Integer> lastDateSamplePercent = new HashMap<String, Integer>();
                                if(!lastDateMovment.isEmpty()){
                                    lastDateSamplePercent = lastDateSamplePercent(lastDateMovment);
                                    createPieChart(pieChartAngle);
                                }else {
                                    pieChartAngle.setNoDataText("No data to show");
                                }

                            }
                            else {

                            }
                        }
                        else {

                        }
                    }
                });
            }
        });
    }

    private List<Map<String, Object>> filterByLastDate(List<Map<String, Object>> queryList) {
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> entry : queryList) {
            for (String key : entry.keySet()) {
                if(key.equals("date")) {
                    Date date = new Date(((long) entry.get(key)) * 1000);
                    if(lastDateD != null){
                        if (date.getDay() == lastDateD.getDay() && date.getMonth() == lastDateD.getMonth() && date.getYear() == lastDateD.getYear()) {
                            res.add(entry);
                        }
                    }

                }
            }
        }
        return res;
    }

    HashMap<String, Integer> lastDateSamplePercent(List<Map<String, Object>> queryList){
        HashMap<String, Integer> res = new HashMap<String, Integer>();
        int countOne =0, countZero = 0, sum=0;
        float percentOne, percentZero;
        res.put("countOne", countOne);
        res.put("countZero", countZero);
        for (Map<String, Object> entry : queryList) {
            for (String key : entry.keySet()) {
                if(key.equals("status")){
                    long status = (long) entry.get(key);
                    if (status == 1){
                        countOne = res.get("countOne");
                        countOne++;
                        res.put("countOne", countOne);
                    }else {
                        countZero = res.get("countZero");
                        countZero++;
                        res.put("countZero", countZero);
                    }
                }
            }
        }
        sum = countOne+countZero;
        percentZero =  ((float)countZero/sum)*100;
        percentOne =  ((float)countOne/sum)*100;
        Log.d(TAG,"countZero: "+ countZero);
        Log.d(TAG,"countOne: "+ countOne);
        Log.d(TAG,"sum: "+ sum);
        Log.d(TAG,"percentZero: "+ percentZero);
        Log.d(TAG,"percentOne: "+ percentOne);

        setData(percentOne,percentZero);

        return res;
    }

    private void createPieChart(PieChart pieChart){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.animateY(1500);
        pieChart.setEntryLabelTextSize(8f);
        pieChart.setDrawCenterText(true);
        pieChart.setExtraOffsets(10,10,10,10);
    }

    private void setData(float BadAngle, float GoodAngle){
        String[] param = new String[]{"Bad angle", "Good angle"};
        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(BadAngle,param[0]));
        values.add(new PieEntry(GoodAngle,param[1]));

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSelectionShift(8f);
        dataSet.setSliceSpace(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);
        data.setValueFormatter(new PercentFormatter(pieChartAngle));

        pieChartAngle.setNoDataText("Loading data");
        pieChartAngle.setData(data);
        pieChartAngle.invalidate();
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

    public class DateAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter{
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
