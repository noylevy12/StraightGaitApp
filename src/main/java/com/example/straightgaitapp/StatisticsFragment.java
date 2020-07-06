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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {
    TextView textViewStatistics, textViewAngles;
    ListView listViewMoves;
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    MovementAdapter movementAdapter;
    RecyclerView fireStoreList;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    FirestoreRecyclerAdapter adapter;

    private List<String> angels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        listViewMoves = (ListView) rootView.findViewById(R.id.listViewMoves);
        movementAdapter = new MovementAdapter(getContext(), R.layout.list_item_single);
        listViewMoves.setAdapter(movementAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("LegMovements").document(userId);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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
    public void parseJSON(){

    }
}
