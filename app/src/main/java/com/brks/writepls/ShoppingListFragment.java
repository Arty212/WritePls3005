package com.brks.writepls;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingListFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private RecyclerView shoppingRecyclerView;
    private ShoppingRecyclerViewAdapter shoppingRecyclerViewAdapter;
    private static List<ShoppingElement> lstToBuy = new ArrayList<>();

    Button addBtn;
    EditText editText;
    Button clearList;

    public ShoppingListFragment() {
// Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("shoppingList");


        addBtn = v.findViewById(R.id.addElement);
        editText = v.findViewById(R.id.editToBuyElement);
        clearList = v.findViewById(R.id.clearListToBuyBtn);

        shoppingRecyclerView = v.findViewById(R.id.shopping_recyclerView);
        shoppingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        shoppingRecyclerViewAdapter = new ShoppingRecyclerViewAdapter(getContext(), lstToBuy);
        shoppingRecyclerView.setAdapter(shoppingRecyclerViewAdapter);

        sortList();

        shoppingRecyclerViewAdapter.setOnItemClickListener(new ShoppingRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onStatusClick(int position) {
                if(lstToBuy.get(position).isStatus()){

                    changeShoppElement(position,false);
                    lstToBuy.get(position).setStatus(false);

                }else {
                    changeShoppElement(position,true);
                    lstToBuy.get(position).setStatus(true);

                }
// updateList();
                shoppingRecyclerViewAdapter.notifyDataSetChanged();
                sortList();

// updateList();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               lstToBuy.clear();
                addShoppingElement();
            }
        });
        clearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
            }
        });

        lstToBuy.clear();
        updateList();
        sortList();

        return v;
    }

    private void updateList() {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                lstToBuy.add(dataSnapshot.getValue(ShoppingElement.class));
                shoppingRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ShoppingElement shoppingElement = dataSnapshot.getValue(ShoppingElement.class);
                int index = getItemIndex(shoppingElement);
                lstToBuy.set(index, shoppingElement);
                shoppingRecyclerViewAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                ShoppingElement shoppingElement = dataSnapshot.getValue(ShoppingElement.class);
                int index = getItemIndex(shoppingElement);
                lstToBuy.remove(index);
                shoppingRecyclerViewAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private int getItemIndex(ShoppingElement shoppingElement){

        int index

                = -1;
        for(int i = 0; i < lstToBuy.size(); i++){
            if(lstToBuy.get(i).getKey().equals(shoppingElement.getKey())) {
                index = i;
                break;
            }
        }

        return index;
    }
    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        switch (item.getItemId()){
            case 0:
                removeShoppingElement(item.getGroupId());

                break;

        }

        return super.onContextItemSelected(item);
    }
    private void addShoppingElement(){
        if(!editText.getText().toString().equals("")) {


            String id = myRef.child(mAuth.getCurrentUser().getUid()).push().getKey();

            ShoppingElement newShoppingElement = new ShoppingElement(true, editText.getText().toString(), id);

            Map<String, Object> shopValue = newShoppingElement.toMap();

            Map<String, Object> shoppingElement = new HashMap<>();
            shoppingElement.put(id, shopValue);

            myRef.updateChildren(shoppingElement);

            editText.setText(null);

            updateList();
           shoppingRecyclerViewAdapter.notifyDataSetChanged();
          //  sortList();
          //  lstToBuy.clear();


        }
    }
    private void clearList(){
        for(int i = lstToBuy.size() - 1; i > -1; i--)removeShoppingElement(i);


    }
    private void removeShoppingElement(int position){
        shoppingRecyclerViewAdapter.mData.get(position).setStatus(true);
        lstToBuy.get(position).setStatus(true);
        myRef.child(lstToBuy.get(position).getKey()).removeValue();
    }

    public static void sortList(){
        Collections.sort(lstToBuy,new CompareSh());
    }
    private void changeShoppElement(int position,boolean status){
        ShoppingElement shoppingElement = lstToBuy.get(position);

        shoppingElement.setStatus(status);

        Map<String,Object> shopValue = shoppingElement.toMap();

        Map<String,Object> newShoppingElement = new HashMap<>();

        newShoppingElement.put(shoppingElement.getKey(),shopValue);

        myRef.updateChildren(newShoppingElement);

    }
}