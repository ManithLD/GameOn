package com.example.gameon;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class workoutAdapter extends FirestoreRecyclerAdapter<firebasemodel, workoutAdapter.workoutViewHolder> {

    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    public workoutAdapter(@NonNull FirestoreRecyclerOptions<firebasemodel> options, Context context) {
        super(options);
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull workoutViewHolder holder, int position, @NonNull firebasemodel model) {

        ImageView popupButton = holder.itemView.findViewById(R.id.menuButton);

        int colourcode = getRandomColor();
        holder.workout.setBackgroundColor(holder.itemView.getResources().getColor(colourcode, null));

        holder.workoutTitle.setText(model.getTitle());
        holder.workoutContent.setText(model.getContent());

        String docId = getSnapshots().getSnapshot(position).getId();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open activitity
                Intent intent = new Intent(view.getContext(), noteDetailsActivity.class);
                intent.putExtra("title", model.getTitle());
                intent.putExtra("content", model.getContent());
                intent.putExtra("workoutId", docId);

                view.getContext().startActivity(intent);
                //Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.setGravity(Gravity.END);
                popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                        Intent intent = new Intent(view.getContext(), editWorkoutActivity.class);
                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("content", model.getContent());
                        intent.putExtra("workoutId", docId);
                        view.getContext().startActivity(intent);
                        return false;
                    }
                });

                popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                        //Toast.makeText(context, "Deleted! (lying)", Toast.LENGTH_SHORT).show();
                        DocumentReference documentReference = firebaseFirestore
                                .collection("workouts").document(firebaseUser.getUid())
                                .collection("myWorkouts").document(docId);
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to Delete!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }

    private int getRandomColor() {
        List<Integer> codes = new ArrayList<>();
        codes.add(R.color.ivory);
        codes.add(R.color.lightgreen);
        codes.add(R.color.skyblue);
        codes.add(R.color.pink);
        codes.add(R.color.lightpink);
        codes.add(R.color.weirdpink);
        codes.add(R.color.ligthergreen);
        codes.add(R.color.lightorange);
        codes.add(R.color.lightpurple);
        codes.add(R.color.light_salmon);
        codes.add(R.color.light_goldenrod_yellow);
        codes.add(R.color.light_coral);
        codes.add(R.color.light_cyan);
        codes.add(R.color.light_grey);
        codes.add(R.color.light_steel_blue);
        codes.add(R.color.light_slate_gray);
        codes.add(R.color.light_sea_green);

        Random random = new Random();
        int number = random.nextInt(codes.size());
        return codes.get(number);
    }

    @NonNull
    @Override
    public workoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_layout, parent, false);
        return new workoutViewHolder(view);
    }

    public class workoutViewHolder extends RecyclerView.ViewHolder {
        private TextView workoutTitle;
        private TextView workoutContent;
        LinearLayout workout;
        public workoutViewHolder(@NonNull View itemView) {
            super(itemView);

            workoutTitle = itemView.findViewById(R.id.workoutTitle);
            workoutContent = itemView.findViewById(R.id.workoutContent);
            workout = itemView.findViewById(R.id.workout);
        }
    }
}