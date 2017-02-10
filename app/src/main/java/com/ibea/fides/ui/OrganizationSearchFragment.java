package com.ibea.fides.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrganizationSearchFragment extends Fragment implements View.OnClickListener{
    @Bind(R.id.searchView_Organization)SearchView mSearchView_Organization;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    public OrganizationSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organization_search, container, false);
        ButterKnife.bind(this, view);

        mSearchView_Organization.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == mSearchView_Organization){
            fetchOrganizationsIds();
        }
    }

    public void fetchOrganizationsIds(){

    }

    public void fetchOrganization(){

    }
}
