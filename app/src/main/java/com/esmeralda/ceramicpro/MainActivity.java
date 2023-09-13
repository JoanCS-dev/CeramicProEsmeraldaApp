package com.esmeralda.ceramicpro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.esmeralda.ceramicpro.ui.account.AccountFragment;
import com.esmeralda.ceramicpro.ui.appointment.AppointmentFragment;
import com.esmeralda.ceramicpro.ui.home.HomeFragment;
import com.esmeralda.ceramicpro.ui.subscriptions.SubscriptionsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.esmeralda.ceramicpro.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences cookies;
    private String strToken, fullName, strCode;
    private ActivityMainBinding binding;
    private View fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ReplacedFragment(new HomeFragment());

        binding.navView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.navigation_home){
                ReplacedFragment(new HomeFragment());
            }
            else if(item.getItemId() == R.id.navigation_appointment){
                ReplacedFragment(new AppointmentFragment());
            }
            else if(item.getItemId() == R.id.navigation_subscriptions){
                ReplacedFragment(new SubscriptionsFragment());
            }
            else if(item.getItemId() == R.id.navigation_account){
                ReplacedFragment(new AccountFragment());
            }
            return true;
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_subscriptions, R.id.navigation_appointment, R.id.navigation_membership, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);*/



        Init();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void Init(){
        ValidateSession();
    }

    private void ReplacedFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,fragment);
        fragmentTransaction.commit();
    }



    private void ValidateSession(){
        cookies = getSharedPreferences("SHA_CST_DB", MODE_PRIVATE);
        strToken = cookies.getString("strToken", "");
        fullName = cookies.getString("fullName", "");

        if(strToken.equals("") && fullName.equals("")){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}