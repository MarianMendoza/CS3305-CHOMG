package com.example.chomg.userinterface;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.example.chomg.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new FragmentHome())
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.menu_itemHome) {
                selectedFragment = new FragmentHome();
            }  else if (item.getItemId() == R.id.menu_itemSettings) {
                selectedFragment = new FragmentSettings();
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
