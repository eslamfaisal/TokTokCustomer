package com.fekrah.toktokcustomer.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fekrah.toktokcustomer.R;
import com.fekrah.toktokcustomer.activities.CurrentOrderActivity;
import com.fekrah.toktokcustomer.activities.MainActivity;
import com.fekrah.toktokcustomer.models.Order;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rafakob.drawme.DrawMeButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TalabatFragment extends Fragment {

    @BindView(R.id.from_location)
    TextView from;

    @BindView(R.id.to_location)
    TextView to;

    @BindView(R.id.distance_location)
    TextView distance;

    @BindView(R.id.cost_location)
    TextView cost;

    @BindView(R.id.order_details_edt)
    EditText orderDetailsEdt;

    @BindView(R.id.notes_details_edt)
    EditText notesEdt;

    @BindView(R.id.send_order)
    DrawMeButton sendOrderBtn;
    public static final String ORDER_ID = "order_id";

    Order order;

    String receiverAddress;
    String arrivalString;
    String costString;
    String distanceString;
    LatLng receiverLatLng, arrivalLatLng;

    String userId;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mCurrentUserOrderReference;


    public static TalabatFragment newInstance() {
        return new TalabatFragment();
    }

    public TalabatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_talabat2, container, false);
        ButterKnife.bind(this, mainView);
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCurrentUserOrderReference = mFirebaseDatabase.getReference()
                .child("Customer_current_order").child(userId);

        sendOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrivalString != null) {
                    String key = FirebaseDatabase.getInstance().getReference().push().getKey();
                    order = new Order(
                            key,
                            costString,
                            receiverAddress,
                            arrivalString,
                            distanceString,
                            orderDetailsEdt.getText().toString(),
                            notesEdt.getText().toString(),
                            FirebaseAuth.getInstance().getUid(),
                            System.currentTimeMillis(),
                            arrivalLatLng.latitude,
                            arrivalLatLng.longitude,
                            receiverLatLng.latitude,
                            receiverLatLng.longitude
                    );
                    mCurrentUserOrderReference.child("order").setValue(order)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(getActivity(), CurrentOrderActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.putExtra(ORDER_ID, order);
                                    getActivity().startActivity(intent);
                                }
                            });

                } else {
                    Toast.makeText(getActivity(), R.string.choose_place_text, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return mainView;
    }

    public void change(LatLng receiverLatLng, LatLng arrivalLatLng, final String receiver,
                       final String arrival, String[] results, final int costkm) {
        if (receiverLatLng != null && arrivalLatLng != null && receiver != null && arrival != null) {

            String result = results[0].substring(0, results[0].length() - 2);

            this.arrivalLatLng = arrivalLatLng;
            this.receiverLatLng = receiverLatLng;

            float minDis = Float.parseFloat(MainActivity.prices.getMin_distance());
            float minPrice = Float.parseFloat(MainActivity.prices.getMin_distance_price());
            float extraKmPrice = Float.parseFloat(MainActivity.prices.getExtra_km_price());

            receiverAddress =  arrival;
            to.setText(receiverAddress);
            arrivalString = receiver;
            from.setText(arrivalString);

            if (Float.parseFloat(result)<= minDis ){
                costString = minPrice +getString(R.string.omla);
            }else {
                float extraDis =  Float.parseFloat(result)- minDis;
                float extraPrice = extraDis*extraKmPrice;
                float totalPrice = extraPrice+minPrice;
                int finalPrice = (int)totalPrice +1;
                costString = String.valueOf(finalPrice+ getString(R.string.omla));
            }

            cost.setText(costString);
            distanceString = result + getString(R.string.k_m);
            distance.setText(distanceString);
        }
    }
}
