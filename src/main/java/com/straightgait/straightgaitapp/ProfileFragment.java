package com.straightgait.straightgaitapp;

import android.graphics.Color;
import android.os.Bundle;
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
    private LineChart lineChartZeroStatus, lineChartOneStatus, lineChartAngleAvg;
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




        // ----------------------------------    line chart    -----------------------------------------------------
//       lineChartZeroStatus = (LineChart) rootView.findViewById(R.id.lineChartLegStatusZero);
//       lineChartOneStatus = (LineChart) rootView.findViewById(R.id.lineChartLegStatusOne);
       lineChartAngleAvg = (LineChart) rootView.findViewById(R.id.lineChartAngleAvg);

       // ---------------------------------    end line chart    ---------------------------------------------------

       monthAndYearFormat = new SimpleDateFormat("MMM\nyyyy", new Locale("en"));
       dayAndMonthFormat = new SimpleDateFormat("dd\nMMM", new Locale("en"));
       dayAndMonthNoSpaceFormat = new SimpleDateFormat("dd MMM", new Locale("en"));
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
//        userId = firebaseAuth.getCurrentUser().getUid();
        usersDocRef = db.collection("users").document(userId);
        setUiUserInfo();

        legMovmentsDocRef = db.collection("LegMovements").document(userId);
        legMovmentsDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     if(task.isSuccessful()){
                         DocumentSnapshot document = task.getResult();
                         if(document.exists()){
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
//                             HashMap<String, Integer> statusOnePerDay = new HashMap<String, Integer>();
//                             HashMap<String, Integer> statusZeroPerDay = new HashMap<String, Integer>();
                             HashMap<String, Integer> sampleAvgPerDay = new HashMap<String, Integer>();

//                             statusOnePerDay = countStatusPerDay(userStatusOneMovements);
//                             statusZeroPerDay = countStatusPerDay(userStatusZeroMovements);
                             sampleAvgPerDay = avgAngleSample(userMovementsList);

//                             Log.d(TAG, "statusOnePerDay"+statusOnePerDay.toString());
//                             Log.d(TAG, "statusZeroPerDay"+statusZeroPerDay.toString());
//                             Log.d(TAG, "sampleAvgPerDay"+sampleAvgPerDay.toString());

                             ArrayList<Entry> yValues = new ArrayList<>();
                             createChart(rootView, sampleAvgPerDay, "average of sample angle", lineChartAngleAvg);
//                             createChart(rootView, statusZeroPerDay, "bad angles sample per day", lineChartZeroStatus);
//                             createChart(rootView, statusOnePerDay, "good angles sample per day", lineChartOneStatus);
                         }
                         else {

                         }
                     }
                     else {

                     }
            }
        });

        return rootView;

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
            }
        });
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
