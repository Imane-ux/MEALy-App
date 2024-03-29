package com.example.mealyapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CookHomePage extends Fragment {

    FirebaseAuth mAuth;
    Cook cook;
    ArrayList<Complaint> complaints;
    int daysOfBanLeft = 0;
    //boolean permanentlyBanned = false;
    ConstraintLayout mainLayout, bannedLayout;
    TextView bannedText;

    private RecyclerView recyclerView0;
    private RecyclerView recyclerView1;
    private RecyclerView.LayoutManager layoutManager0;
    private RecyclerView.LayoutManager layoutManager1;
    private DatabaseReference ref0;
    private DatabaseReference dr;
    private DatabaseReference refc;
    private FirebaseRecyclerAdapter<Meal, MyViewHolder1> madapter;
    private FirebaseRecyclerAdapter<Meal, MyViewHolder2> adapter1;

    public CookHomePage(Context context) {}

    public CookHomePage() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cook_home_page, container, false);

        mAuth = FirebaseAuth.getInstance();
        complaints = new ArrayList<>();

        ImageButton profileButton = view.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fragmentContainer, new CookProfileFragment()).addToBackStack("CookProfileFragment").commit();
            }
        });

        Button btn2= view.findViewById(R.id.logOut2_cook);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {logout(); }
        });

        Button btn= view.findViewById(R.id.logOut_cook);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        ImageButton btnAdd= view.findViewById(R.id.imageBtn3);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new AddMealFragment()).commit();

            }
        });

        Button btn1= view.findViewById(R.id.vRequests);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new CooksRequests()).commit();
            }
        });

        mainLayout = view.findViewById(R.id.main_layout);
        bannedLayout = view.findViewById(R.id.banned_layout);
        bannedText = view.findViewById(R.id.banned_text);


        checkBan(mAuth.getCurrentUser().getUid());


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView0= getActivity().findViewById(R.id.recyclerViewcook2);
        recyclerView0.setHasFixedSize(true);
        ref0= FirebaseDatabase.getInstance().getReference("Meals").child(mAuth.getUid());
        layoutManager0= new LinearLayoutManager(getActivity());
        recyclerView0.setLayoutManager(layoutManager0);

        refc= FirebaseDatabase.getInstance().getReference("Meals To Clients");

        dr = FirebaseDatabase.getInstance().getReference("Offered Meals").child(mAuth.getUid());
        recyclerView1= getActivity().findViewById(R.id.recyclerViewcook1);
        recyclerView1.setHasFixedSize(true);
        layoutManager1= new LinearLayoutManager(getActivity());
        recyclerView1.setLayoutManager(layoutManager1);

    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView= view.findViewById(R.id.recyclerViewcook2);
        recyclerView.setHasFixedSize(true);
        //ref= FirebaseDatabase.getInstance().getReference().child("Meals").child(mAuth.getUid());
        layoutManager= new LinearLayoutManager(getActivity());
        //recyclerView.setLayoutManager(layoutManager);
        //adapter.notifyDataSetChanged();
        madapter =  new MealsAdapter(getContext(), meals);
        recyclerView.setLayoutManager(layoutManager);
        //adapter.startListening();
        recyclerView.setAdapter(madapter);
        //madapter.notifyDataSetChanged();
    }*/

    @Override
    public void onStart() {
        super.onStart();

        // creating firebase recycler for only the menu not the current offered meals
        FirebaseRecyclerOptions<Meal> options = new FirebaseRecyclerOptions.Builder<Meal>()
                .setQuery(ref0, Meal.class).build();
        madapter = new FirebaseRecyclerAdapter<Meal, MyViewHolder1>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder1 holder, int position, @NonNull Meal model) {

                final String mealID= getRef(position).getKey();
                DatabaseReference re = ref0.child(mealID);

                holder.itemName.setText(model.getName());
                holder.itemType.setText(model.getType());
                holder.itemCuisine.setText(model.getCuisine());
                holder.itemIngredient.setText(model.getIngredients());
                holder.itemAllergens.setText(model.getAllergens());
                holder.itemPrice.setText(model.getPrice());
                holder.itemDescription.setText(model.getDescription());
                inOfferedList = false;
                holder.del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //DatabaseReference re= ref0.child(mealID);
                        // if meal not in offered meals list, then:
                        if (inOfferedList == false) {
                            re.removeValue();
                            //refc.child(mealID).removeValue();
                            Toast.makeText(getActivity(), "Meal deleted", Toast.LENGTH_LONG).show();
                        }  else {
                            Toast.makeText(getActivity(), "You need to delete meal from Currently Offered Meals list first", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        re.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                inOfferedList =true;
                                Meal meal= snapshot.getValue(Meal.class);
                                String id = dr.push().getKey();
                                refc.child(id).setValue(meal);
                                dr.child(id).setValue(meal);

//                                if (inOfferedList == false){
//                                    inOfferedList = true;
//                                    Meal meal= snapshot.getValue(Meal.class);
//                                    String id = dr.push().getKey();
//                                    dr.child(id).setValue(meal);
//                                } else {
//                                    Toast.makeText(getActivity(), "Already added to Currently Offered Meals", Toast.LENGTH_LONG).show();
//                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_view, parent, false);
                CookHomePage.MyViewHolder1 vh1 = new MyViewHolder1(view);
                return vh1;
            }
        };
        madapter.startListening();
        recyclerView0.setAdapter(madapter);

        // creating the firebase recycler view for the currently offered meal
        FirebaseRecyclerOptions<Meal> options1 = new FirebaseRecyclerOptions.Builder<Meal>()
                .setQuery(dr, Meal.class).build();
        adapter1= new FirebaseRecyclerAdapter<Meal, MyViewHolder2>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder2 holder, int position, @NonNull Meal model) {

                final String off_mealID= getRef(position).getKey();
                DatabaseReference re= dr.child(off_mealID);

                holder.itemName.setText(model.getName());
                holder.itemType.setText(model.getType());
                holder.itemCuisine.setText(model.getCuisine());
                holder.itemIngredient.setText(model.getIngredients());
                holder.itemAllergens.setText(model.getAllergens());
                holder.itemPrice.setText(model.getPrice());
                holder.itemDescription.setText(model.getDescription());
                holder.inOfferedList = true;
                holder.itemRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inOfferedList = false;
                        re.removeValue();
                        refc.child(off_mealID).removeValue();
                        Toast.makeText(getActivity(), "Meal removed from the currently offered meals' list", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offeredmeal_view, parent, false);
                CookHomePage.MyViewHolder2 vh = new MyViewHolder2(view);
                return vh;
            }
        };
        adapter1.startListening();
        recyclerView1.setAdapter(adapter1);

    }


    @Override
    public void onStop() {
        super.onStop();
        madapter.stopListening();
        adapter1.stopListening();
    }

    public void logout()
    {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getActivity(), "logged out", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager= getParentFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, new StartFragment()).addToBackStack(null).commit();
    }

    public void checkBan(String cOOKuid){
        DatabaseReference newr;
        newr = FirebaseDatabase.getInstance().getReference("user").child(cOOKuid);

        newr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String permanentBan2 = String.valueOf(snapshot.child("permanentBan").getValue());
                //String daysOfTempoBAn = String.valueOf(snapshot.child("daysOfTemporaryBan").getValue());
                String isbanned= String.valueOf(snapshot.child("banned").getValue());
                //Boolean permanentBan2 = ( Boolean) snapshot.child("permanentBan").getValue();
                if (permanentBan2 == "true"){
                    bannedText.setText("Banned permanently!");
                    bannedText.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.INVISIBLE);
                    bannedLayout.setVisibility(View.VISIBLE);
                }
                else if (isbanned == "true"){
                    bannedText.setText("Suspended for 15 days!");
                    bannedText.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.INVISIBLE);
                    bannedLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static boolean inOfferedList;

    public static class MyViewHolder1 extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView itemType;
        public TextView itemCuisine;
        public TextView itemIngredient;
        public TextView itemAllergens;
        public TextView itemDescription;
        public TextView itemPrice;
        ImageButton del;
        ImageButton add;

        public MyViewHolder1(View itemView) {
            super(itemView);
            itemName= itemView.findViewById(R.id.mealName0);
            itemType= itemView.findViewById(R.id.mealType);
            itemCuisine= itemView.findViewById(R.id.cuisineType);
            itemIngredient=  itemView.findViewById(R.id.listIngredients);
            itemAllergens= itemView.findViewById(R.id.allergensID);
            itemDescription= itemView.findViewById(R.id.mealDescriptionId);
            itemPrice=  itemView.findViewById(R.id.priceMealCook);
            del= itemView.findViewById(R.id.imgDelButton);
            add= itemView.findViewById(R.id.imgPlusButton);
            inOfferedList = false;
        }
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView itemType;
        public TextView itemCuisine;
        public TextView itemIngredient;
        public TextView itemAllergens;
        public TextView itemDescription;
        public TextView itemPrice;
        ImageButton itemRemove;
        public boolean inOfferedList;

        public MyViewHolder2(View itemView) {
            super(itemView);
            itemName= itemView.findViewById(R.id.mealName1);
            itemType= itemView.findViewById(R.id.mealType1);
            itemCuisine= itemView.findViewById(R.id.cuisineType1);
            itemIngredient=  itemView.findViewById(R.id.listIngredients1);
            itemAllergens= itemView.findViewById(R.id.allergensID1);
            itemDescription= itemView.findViewById(R.id.mealDescriptionId1);
            itemPrice= itemView.findViewById(R.id.priceMealCook2);
            itemRemove= itemView.findViewById(R.id.imgREMButton1);
        }
    }
}