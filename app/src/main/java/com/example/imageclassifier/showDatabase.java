package com.example.imageclassifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class showDatabase extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");
    private MyAdapter adapter;
    private ArrayList<Model> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_database);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView.clearFocus();    // in some devices by default cursor will be there in search bar
        // before we click on it so it clear that cursor

        // this list is for Database without search
        list = new ArrayList<>();

        adapter = new MyAdapter(this,list);

        recyclerView.setAdapter(adapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String searchText = newText.trim();

                if (searchText.isEmpty()) {
                    // clear the query to show all data from the database
                    Query query = root.orderByKey();
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // clear current list
                            list.clear();

                            // add all data from snapshot to list
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Model model = dataSnapshot.getValue(Model.class);
                                list.add(model);
                            }

                            // reverse list to show newest data first
                            Collections.reverse(list);

                            // update adapter
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
//                     create query to search Firebase for matching data

//                    To search for the partial matches across all the children nodes of the root node, you can use the orderByValue() method instead of orderByChild().
//                    This will order the results based on the values of each child node rather than a specific child node.
//                    To implement this in your code, you need to change your query to the following:
                    Query query = root.orderByChild("NoPlate").startAt(searchText).endAt(searchText + "\uf8ff");
                    list.clear();
                    // as our searchView is on TextListener so whenever i clicked on  searchView or typed any thing that can be a letter
                    // so whole code under it will re run so instead of clearing list in each query which clears the results of previous
                    // query that we dont want so we clear it on the first go means whenever we write anything in search box
                    // it will clear first all list and then add ALL THE DATA MATCHED FROM ALL QUERIES IN LIST
                    // add listener for query
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // clear current list
//                            list.clear();

                            // add matching data to list
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Model model = dataSnapshot.getValue(Model.class);
                                list.add(model);
                            }

                            // reverse list to show newest data first
                            Collections.reverse(list);

                            // update adapter
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Query query2 = root.orderByChild("Driver").startAt(searchText).endAt(searchText + "\uf8ff");

                    // add listener for query
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // clear current list
//                            list.clear();
                            // no need to clear list otherwise it clears the output of query1 as it comes after query 1

                            // add matching data to list
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Model model = dataSnapshot.getValue(Model.class);
                                list.add(model);
                            }

                            // reverse list to show newest data first
                            Collections.reverse(list);

                            // update adapter
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Query query3 = root.orderByChild("Status").startAt(searchText).endAt(searchText + "\uf8ff");

                    // add listener for query
                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // clear current list
//                            list.clear();
                            // no need to clear list otherwise it clears the output of query1 as it comes after query 1

                            // add matching data to list
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Model model = dataSnapshot.getValue(Model.class);
                                list.add(model);
                            }

                            // reverse list to show newest data first
                            Collections.reverse(list);

                            // update adapter
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Query query4 = root.orderByChild("Date").startAt(searchText).endAt(searchText + "\uf8ff");

                    // add listener for query
                    query4.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // clear current list
//                            list.clear();
                            // no need to clear list otherwise it clears the output of query1 as it comes after query 1

                            // add matching data to list
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Model model = dataSnapshot.getValue(Model.class);
                                list.add(model);
                            }

                            // reverse list to show newest data first
                            Collections.reverse(list);

                            // update adapter
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Query query5 = root.orderByChild("Time").startAt(searchText).endAt(searchText + "\uf8ff");

                    // add listener for query
                    query5.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // clear current list
//                            list.clear();
                            // no need to clear list otherwise it clears the output of query1 as it comes after query 1

                            // add matching data to list
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Model model = dataSnapshot.getValue(Model.class);
                                list.add(model);
                            }

                            // reverse list to show newest data first
                            Collections.reverse(list);

                            // update adapter
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                return true;
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Model model = dataSnapshot.getValue(Model.class);
                    list.add(model);
                }
                Collections.reverse(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}