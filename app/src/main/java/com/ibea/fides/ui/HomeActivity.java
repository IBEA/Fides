package com.ibea.fides.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
            Intent intent = new Intent(mContext, ShiftsCreateActivity.class);
            startActivity(intent);
        }
        if(v == mButton_Dirty_Logout) {
            mUniversal.logout(mContext);
        }
    }
}
