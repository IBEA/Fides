package com.ibea.fides.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by N8Home on 1/30/17.
 */

public class ProfileForOrganizationFragment extends Fragment implements View.OnClickListener {

    private String TAG = "ProfileForOrg";
    private Organization mOrganization;

    // image storage reference variables
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference mImageRef;

    @Bind(R.id.imageView_orgPic) ImageView mOrgPic;
    @Bind(R.id.textView_orgName) TextView mOrgName;
    @Bind(R.id.textView_orgAddress) TextView mOrgAddress;
    @Bind(R.id.textView_orgAddressLineTwo) TextView mOrgAddressLineTwo;
    @Bind(R.id.textView_orgWebsite) TextView mOrgWebsite;
    @Bind(R.id.textView_orgDescription) TextView mOrgDescription;
    @Bind(R.id.progressBar_ImageLoading) ProgressBar mImageProgressBar;


    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public static ProfileForOrganizationFragment newInstance(Organization organization) {
        ProfileForOrganizationFragment fragment = new ProfileForOrganizationFragment();

        Log.d("Pass check: ", organization.getName());
        Bundle bundle = new Bundle();
        bundle.putParcelable("organization", Parcels.wrap(organization));
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("organization")){
            Log.d(TAG, "Found intent");
            mOrganization = Parcels.unwrap(intent.getParcelableExtra("organization"));
        }else{
            Log.d(TAG, "Used bundle");
            mOrganization = Parcels.unwrap(getArguments().getParcelable("organization"));
        }
    }

    private class ImageLoadedCallbacker implements Callback {
        ProgressBar progressBar;

        public ImageLoadedCallbacker(ProgressBar progBar) {
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organization_profile, container, false);
        ButterKnife.bind(this, view);

        String orgId = mOrganization.getPushId();
        // assign image storage reference variables
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://fides-6faeb.appspot.com");
        mImageRef = mStorageRef.child("images/" + orgId + ".jpg");

        mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                mOrgPic.setVisibility(View.GONE);
                mImageProgressBar.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(uri)
                        .placeholder(R.drawable.avatar_blank)
                        .resize(450, 400)
                        .centerCrop()
                        .transform(new CircleTransform())
                        .into(mOrgPic, new ImageLoadedCallbacker(mImageProgressBar) {
                            @Override
                            public void onSuccess() {
                                if(this.progressBar != null) {
                                    this.progressBar.setVisibility(View.GONE);
                                    mOrgPic.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        mOrgName.setText(mOrganization.getName());
        mOrgAddress.setText(mOrganization.getStreetAddress());
        mOrgAddressLineTwo.setText(mOrganization.getCityAddress() + ", " + mOrganization.getStateAddress() + ", " + mOrganization.getZipcode());
        mOrgWebsite.setText(mOrganization.getUrl());
        mOrgDescription.setText(mOrganization.getDescription());

        mOrgWebsite.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == mOrgWebsite) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mOrgWebsite.getText().toString()));
            try {
                startActivity(webIntent);
            }
            catch (ActivityNotFoundException e) {
                Log.e(String.valueOf(getActivity()), e.getMessage());
            }
        }
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
