package com.ibea.fides.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.button_Dirty) Button mButton_Dirty;
    @Bind(R.id.button_Dirty_Logout) Button mButton_Dirty_Logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mButton_Dirty.setOnClickListener(this);
        mButton_Dirty_Logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(v == mButton_Dirty){
            Intent intent = new Intent(mContext, ShiftsTestingActivity.class);
            startActivity(intent);
        }
        if(v == mButton_Dirty_Logout) {
            mUniversal.logout(mContext);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        else if (id == R.id.action_shifts){
            Intent intent = new Intent(HomeActivity.this, ShiftsCreateActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
