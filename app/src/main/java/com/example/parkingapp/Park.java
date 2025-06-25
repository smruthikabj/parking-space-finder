package com.example.parkingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class Park extends AppCompatActivity implements View.OnClickListener {
    private static final String PREFS_NAME = "ParkingSlotsPrefs";
    private static final String KEY_SLOT_PREFIX = "slot_";
    private static final String KEY_TIME_PREFIX = "time_";
    private static final long SLOT_DURATION = 2 * 60 * 60 * 1000; // 2 hours in milliseconds
    private static final int REGISTRATION_REQUEST_CODE = 1001;
    private Button[] slotsButtons;
    private SharedPreferences sharedPreferences;
    private Map<Integer, Runnable> runnableMap = new HashMap<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize buttons
        slotsButtons = new Button[]{
                findViewById(R.id.slot1Button),
                findViewById(R.id.slot2Button),
                findViewById(R.id.slot3Button),
                findViewById(R.id.slot4Button),
                findViewById(R.id.slot5Button),
                findViewById(R.id.slot6Button),
                findViewById(R.id.slot7Button),
                findViewById(R.id.slot8Button),
                findViewById(R.id.slot9Button),
                findViewById(R.id.slot10Button),
                findViewById(R.id.slot11Button),
                findViewById(R.id.slot12Button)
        };

        // Set click listeners for buttons
        for (Button button : slotsButtons) {
            button.setOnClickListener(this);
        }

        // Restore the state of the buttons
        restoreButtonState();

        // Initialize and set click listener for the logout button
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        int buttonId = button.getId();
        int slotNumber = getSlotNumberFromButtonId(buttonId);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        boolean isButtonRegistered = sharedPreferences.getBoolean(KEY_SLOT_PREFIX + buttonId, false);
        long bookingTime = sharedPreferences.getLong(KEY_TIME_PREFIX + buttonId, 0);

        // Check if the slot is already registered and still within booked time
        if (isButtonRegistered && System.currentTimeMillis() - bookingTime < SLOT_DURATION) {
            // Slot is still within the booked time duration, show message or take action as needed
            // You might want to show a message here indicating the slot is still booked
            return; // Exit onClick method without registering the slot again
        }

        // Register the button
        editor.putBoolean(KEY_SLOT_PREFIX + buttonId, true);
        long currentTime = System.currentTimeMillis();
        editor.putLong(KEY_TIME_PREFIX + buttonId, currentTime);
        editor.apply();

        // Navigate to RegistrationActivity
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("slotNumber",slotNumber); // Pass slot number to RegistrationActivity
        startActivityForResult(intent, REGISTRATION_REQUEST_CODE);

        // Schedule the slot to become available after 2 hours
        scheduleSlotAvailability(buttonId, currentTime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTRATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Slot successfully registered, update UI or take necessary action
                int slotNumber = data.getIntExtra("slotNumber", -1);
                int buttonId = getButtonIdFromSlotNumber(slotNumber);
                // Example: Update button UI to indicate slot is now booked
                Button button = findViewById(buttonId);
                button.setEnabled(false);
                button.setBackgroundColor(Color.RED);
                button.setText("Slot Full");
            } // Slot registration cancelled, handle UI update if needed
            int slotNumber = data.getIntExtra("slotNumber", -1);
            if (slotNumber != -1) {
                // Enable the corresponding slot button again
                int buttonId = getButtonIdFromSlotNumber(slotNumber);
                Button button = findViewById(buttonId);
                button.setEnabled(true); // Reset button appearance
                button.setText("Slot " + buttonId); // Adjust text as needed
                Toast.makeText(this, "Slot registration cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void restoreButtonState() {
        long currentTime = System.currentTimeMillis();
        for (Button button : slotsButtons) {
            int buttonId = button.getId();
            boolean isButtonRegistered = sharedPreferences.getBoolean(KEY_SLOT_PREFIX + buttonId, false);
            long bookingTime = sharedPreferences.getLong(KEY_TIME_PREFIX + buttonId, 0);

            if (isButtonRegistered && System.currentTimeMillis() - bookingTime < SLOT_DURATION) {
                // Slot is still within the booked time duration, update UI accordingly
                button.setEnabled(false);
                button.setBackgroundColor(Color.RED);
                button.setText("Slot Full");

                // Schedule the slot to become available
                scheduleSlotAvailability(buttonId, bookingTime);
            } else {
                // Slot duration has passed, make it available
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_SLOT_PREFIX + buttonId, false);
                editor.putLong(KEY_TIME_PREFIX + buttonId, 0);
                editor.apply();
            }
        }
    }

    private void scheduleSlotAvailability(final int buttonId, long bookingTime) {
        long delay = SLOT_DURATION - (System.currentTimeMillis() - bookingTime);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_SLOT_PREFIX + buttonId, false);
                editor.putLong(KEY_TIME_PREFIX + buttonId, 0);
                editor.apply();

                runnableMap.remove(buttonId);
            }
        };
        handler.postDelayed(runnable, delay);
        runnableMap.put(buttonId, runnable);
    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate back to MainActivity
        Intent intent = new Intent(Park.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private int getSlotNumberFromButtonId(int buttonId) {
        switch (buttonId) {
            case R.id.slot1Button:
                return 1;
            case R.id.slot2Button:
                return 2;
            case R.id.slot3Button:
                return 3;
            case R.id.slot4Button:
                return 4;
            case R.id.slot5Button:
                return 5;
            case R.id.slot6Button:
                return 6;
            case R.id.slot7Button:
                return 7;
            case R.id.slot8Button:
                return 8;
            case R.id.slot9Button:
                return 9;
            case R.id.slot10Button:
                return 10;
            case R.id.slot11Button:
                return 11;
            case R.id.slot12Button:
                return 12;
            default:
                return -1;
        }
    }

    private int getButtonIdFromSlotNumber(int slotNumber) {
        switch (slotNumber) {
            case 1:
                return R.id.slot1Button;
            case 2:
                return R.id.slot2Button;
            case 3:
                return R.id.slot3Button;
            case 4:
                return R.id.slot4Button;
            case 5:
                return R.id.slot5Button;
            case 6:
                return R.id.slot6Button;
            case 7:
                return R.id.slot7Button;
            case 8:
                return R.id.slot8Button;
            case 9:
                return R.id.slot9Button;
            case 10:
                return R.id.slot10Button;
            case 11:
                return R.id.slot11Button;
            case 12:
                return R.id.slot12Button;
            default:
                return -1;
        }
    }
}