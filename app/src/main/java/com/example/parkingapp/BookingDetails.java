package com.example.parkingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookingDetails extends AppCompatActivity {

    private TextView slotNumberTextView, carModelTextView, carNumberTextView, amountTextView;
    private Button checkSlotsButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        sharedPreferences = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);

        slotNumberTextView = findViewById(R.id.slotNumberTextView);
        carModelTextView = findViewById(R.id.carModelTextView);
        carNumberTextView = findViewById(R.id.carNumberTextView);
        amountTextView = findViewById(R.id.amountTextView);
        checkSlotsButton = findViewById(R.id.button3);

        Intent intent = getIntent();
        if (intent != null) {
            int slotNumber = intent.getIntExtra("slotNumber",-1);
            String carModel = intent.getStringExtra("carModel");
            String carNumber = intent.getStringExtra("carNumber");
            String amount = String.valueOf(intent.getIntExtra("Cost", 0));

            slotNumberTextView.setText("Slot Number: " + slotNumber);
            carModelTextView.setText("Car Model: " + carModel);
            carNumberTextView.setText("Car Number: " + carNumber);
            amountTextView.setText("Amount Paid: " + amount);
        }

        checkSlotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Park class
                Intent intent = new Intent(BookingDetails.this, Park.class);
                startActivity(intent);

                // Change color of booked slot (assuming you have a method in the Park class to handle this)
                // For example, if you have a method called changeSlotColor(int slotNumber, int color)
                int bookedSlotNumber = Integer.parseInt(slotNumberTextView.getText().toString().split(" ")[2]);

                // Save the registered slot number to shared preferences
                saveRegisteredSlot(bookedSlotNumber);

            }
        });
    }
    private void saveRegisteredSlot(int slotNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("registeredSlot", slotNumber);
        editor.apply();
    }
}
