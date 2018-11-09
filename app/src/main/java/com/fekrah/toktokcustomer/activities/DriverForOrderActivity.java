package com.fekrah.toktokcustomer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fekrah.toktokcustomer.R;
import com.fekrah.toktokcustomer.models.Driver;

import butterknife.ButterKnife;

import static com.fekrah.toktokcustomer.adapters.AcceptedOrderDriversAdapter.DRIVER_FOR_ORDER_ID;

public class DriverForOrderActivity extends AppCompatActivity {

    Driver driver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_for_order);
        ButterKnife.bind(this);

        driver = (Driver) getIntent().getSerializableExtra(DRIVER_FOR_ORDER_ID);



    }
}
