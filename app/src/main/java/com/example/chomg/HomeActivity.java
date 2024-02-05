package com.example.chomg;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new ExampleFragment1())
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.menu_itemHome) {
                selectedFragment = new ExampleFragment1();
            } else if (item.getItemId() == R.id.menu_itemAccount) {
                // Replace with the fragment for the "Account" menu item
            } else if (item.getItemId() == R.id.menu_itemSettings) {
                // Replace with the fragment for the "Settings" menu item
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
                return true;
            } else {
                return false;
            }
        });
    }
}
