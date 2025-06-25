package com.example.parkingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    private int costText;
    private ImageView GPay, PPe;
    private Button bookSlotButton;
    private TextView costTextView;
    private SeekBar seekBar;
    private TextView timeTextView;
    private int selectedTime;
    private Spinner carModelSpinner;
    private EditText carNumberEditText;
    private String selectedCarModel;
    private int buttonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Intent intent = getIntent();
        int slotNumber = intent.getIntExtra("slotNumber", -1); // Get slot number from intent

        seekBar = findViewById(R.id.seekBar);
        costTextView = findViewById(R.id.costTextView);
        timeTextView = findViewById(R.id.timeTextView);
        GPay = findViewById(R.id.button_gpay);
        PPe = findViewById(R.id.button_phone);
        bookSlotButton = findViewById(R.id.button2);
        carModelSpinner = findViewById(R.id.spinner);
        carNumberEditText = findViewById(R.id.editTextTextPersonName3);

        // Populate the spinner with car models
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.car_models_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carModelSpinner.setAdapter(adapter);

        carModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCarModel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCarModel = null;
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("ParkingSlotPrefs", Context.MODE_PRIVATE);

        seekBar.setMax(120); // Set the maximum value for the SeekBar in minutes

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedTime = progress;
                int hours = selectedTime / 60; // Calculate the hours
                int minutes = selectedTime % 60; // Calculate the remaining minutes
                timeTextView.setText("Selected Time: " + hours + " hour(s) " + minutes + " minute(s)");

                int cost = calculateCost(hours); // Calculate the cost based on the selected time
                costTextView.setText("Cost: Rs. " + cost);
                costText = cost;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Empty method body
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Empty method body
            }
        });

        bookSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCarModel = carModelSpinner.getSelectedItem().toString();

                String carModel = selectedCarModel;
                String carNumber = carNumberEditText.getText().toString();

                if (!isValidCarNumber(carNumber)) {
                    carNumberEditText.setError("Invalid car number format");
                    return;
                }

                // Pass the slot number and other details to BookingDetails
                Intent intent = new Intent(RegistrationActivity.this, BookingDetails.class);
                intent.putExtra("slotNumber", slotNumber);
                intent.putExtra("carModel", selectedCarModel);
                intent.putExtra("carNumber", carNumber);
                intent.putExtra("Cost", costText);
                startActivity(intent);


                // Pass result back to Park activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("slotNumber", slotNumber);
                resultIntent.putExtra("bookingDuration", selectedTime);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        GPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegistrationActivity.this, "Paying through GPay", Toast.LENGTH_SHORT).show();
            }
        });

        PPe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegistrationActivity.this, "Paying through PhonePe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int calculateCost(int hours) {
        if (hours < 1) {
            return 15;
        } else if (hours == 1) {
            return 25;
        } else if (hours == 1 && selectedTime >= 30) {
            return 35;
        } else if (hours == 2) {
            return 50;
        } else {
            return 0; // Handle other cases as per your requirement
        }
    }


    private boolean isValidCarNumber(String carNumber) {
        // Assuming the car number format is alphanumeric with specific length
        // Modify the regex as per the actual format you want to validate
        String regex = "^[A-Z]{2}[0-9]{2}[A-Z]{1}[0-9]{4}$";
        return !TextUtils.isEmpty(carNumber) && carNumber.matches(regex);
    }

    @Override
    public void onBackPressed() {
        // Set result as cancelled to indicate no slot was registered
        Intent resultIntent = new Intent();
        resultIntent.putExtra("slotNumber", getIntent().getIntExtra("slotNumber", -1)); // Return slot number
        setResult(RESULT_CANCELED, resultIntent);
        super.onBackPressed();
        finish();
    }
}
