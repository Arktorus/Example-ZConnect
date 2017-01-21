package com.zconnect.login.zconnect;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProductsTab extends Fragment {


    private RecyclerView mProductList;
    private DatabaseReference mDatabase;

    public ProductsTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_store_room, container, false);

        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_store_room);
        mProductList = (RecyclerView) view.findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(new LinearLayoutManager(getContext()));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ZConnect/storeroom");

//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), AddProduct.class);
//                startActivity(intent);
//            }
//        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Product,ProductsTab.ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductsTab.ProductViewHolder>(

                Product.class,
                R.layout.products_row,
                ProductsTab.ProductViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(ProductsTab.ProductViewHolder viewHolder, Product model, int position) {
                viewHolder.defaultSwitch(model.getKey());
                viewHolder.setSwitch(model.getKey());
                viewHolder.setProductName(model.getProductName());
                viewHolder.setProductDesc(model.getProductDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());

            }
        };
        mProductList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        private DatabaseReference ReserveReference;
        View mView;
        private Switch mReserve;
        private TextView ReserveStatus;
        private FirebaseAuth mAuth;
        String [] keyList;
        String ReservedUid;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void defaultSwitch(final String key)
        {
            ReserveStatus = (TextView) mView.findViewById(R.id.switch1);
            mReserve = (Switch) mView.findViewById(R.id.switch1);
            ReserveReference = FirebaseDatabase.getInstance().getReference().child("ZConnect/Users");
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            final String userId = user.getUid();
            // Toast.makeText(mView.getContext(), userId, Toast.LENGTH_SHORT).show();

            ReserveReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ReservedUid = (String)dataSnapshot.child(userId +"/Reserved").getValue();
                    // Toast.makeText(mView.getContext(), ReservedUid, Toast.LENGTH_SHORT).show();

//                    if(ReservedUid == null)
//                        ReservedUid = "   ";

                    keyList = ReservedUid.split(" ");

                    // Toast.makeText(mView.getContext(), ReservedUid, Toast.LENGTH_SHORT).show();
                    //check the current state before we display the screen
                    List<String> list = new ArrayList<String>(Arrays.asList(keyList));
                    if (list.contains(key))
                    {
                        mReserve.setChecked(true);
                        //Toast.makeText(mView.getContext(), "Contains Id", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mReserve.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        public void setSwitch(final String key)
        {
            ReserveStatus = (TextView) mView.findViewById(R.id.switch1);
            mReserve = (Switch) mView.findViewById(R.id.switch1);
            //set the switch to ON

            //attach a listener to check for changes in state
            ReserveReference = FirebaseDatabase.getInstance().getReference().child("ZConnect/Users");
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            final String userId = user.getUid();
            //Toast.makeText(mView.getContext(), userId, Toast.LENGTH_SHORT).show();

            ReserveReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ReservedUid = (String)dataSnapshot.child(userId +"/Reserved").getValue();
                    //Toast.makeText(mView.getContext(), ReservedUid, Toast.LENGTH_SHORT).show();

                    if(ReservedUid == null)
                        ReservedUid = "   ";

                    keyList = ReservedUid.split(" ");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            mReserve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                    if(isChecked){
                        ReserveStatus.setText("Product Reserved");
                        //Toast.makeText(mView.getContext(), key, Toast.LENGTH_SHORT).show();
                        List<String> list = new ArrayList<String>(Arrays.asList(keyList));
                        if (!list.contains(key))
                            list.add(key);

                        ReservedUid = TextUtils.join(" ", list);

                        //Toast.makeText(mView.getContext(),ReservedUid, Toast.LENGTH_SHORT).show();
                        DatabaseReference newPost = ReserveReference.child(userId);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("Reserved", ReservedUid);
                        newPost.updateChildren(childUpdates);



                    } else{
                        ReserveStatus.setText("Reserve Now");
                        List<String> list = new ArrayList<String>(Arrays.asList(keyList));
//                        //remove
                        list.remove(key);

                        ReservedUid = TextUtils.join(" ", list);
                        DatabaseReference newPost = ReserveReference.child(userId);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("Reserved", ReservedUid);
                        newPost.updateChildren(childUpdates);
                    }


                }
            });




        };


        public void setProductName(String productName){

            TextView post_name = (TextView) mView.findViewById(R.id.productName);
            post_name.setText(productName);

        }

        public void setProductDesc(String productDesc){

            TextView post_desc = (TextView) mView.findViewById(R.id.productDescription);
            post_desc.setText(productDesc);

        }

        public void setImage(Context ctx, String image){


            ImageView post_image = (ImageView) mView.findViewById(R.id.postImg);
            Picasso.with(ctx).load(image).into(post_image);


        }

    }

}