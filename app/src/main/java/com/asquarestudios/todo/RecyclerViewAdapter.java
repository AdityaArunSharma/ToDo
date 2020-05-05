package com.asquarestudios.todo;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.asquarestudios.todo.Model.NewItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {
    private Context context;
    private List<NewItem> list;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    public static final String TAG = "RecyclerView Adapter";
    private String username;

    public RecyclerViewAdapter(Context context, List<NewItem> list,String username) {
        this.context = context;
        this.list = list;
        this.username=username;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, final int position)
    {
         NewItem item  = list.get(position);
         holder.tittle.setText(item.getTittle());
         holder.date.setText(item.getDate());
         String p = item.getPriority();
         if(p.equals("high"))
         {
             holder.priority.setText("High Priority");
             holder.priority.setBackgroundColor(Color.RED);
         }
         else if(p.equals("medium"))
         {
             holder.priority.setText("Medium Priority");
             holder.priority.setBackgroundColor(Color.YELLOW);
         }
         else //low
         {
             holder.priority.setText("Low Priority");
             holder.priority.setBackgroundColor(Color.GREEN);
         }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tittle,date,priority;
        public ImageView edit,delete,maximize;

        public ViewHolder(@NonNull View itemView,Context ctx)
        {
            super(itemView);
            context=ctx;
            tittle = itemView.findViewById(R.id.listrow_tittle_textView);
            date = itemView.findViewById(R.id.listrow_date_textView);
            priority = itemView.findViewById(R.id.priority_textView);

            edit     = itemView.findViewById(R.id.listrow_edit_imageView);
            delete   = itemView.findViewById(R.id.listrow_delete_imageView);
            maximize = itemView.findViewById(R.id.maximize);

            delete.setOnClickListener(this);
            edit.setOnClickListener(this);
            maximize.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.listrow_delete_imageView)
            {
                deleteOptionClicked(getAdapterPosition());
            }
            if(view.getId()==R.id.listrow_edit_imageView)
            {
                editOptionClicked(getAdapterPosition());
            }
            if(view.getId()==R.id.maximize)
            {
                maximizeButtonClicked(getAdapterPosition());
            }
        }

        private void maximizeButtonClicked(int i)
        {
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.detailed, null);

            final TextView tittle,task;

            tittle = view.findViewById(R.id.tittle);
            task = view.findViewById(R.id.task);

            tittle.setText(list.get(i).getTittle());
            task.setText(list.get(i).getTask());

            builder.setView(view);
            dialog = builder.create();
            dialog.show();
        }

        String id;
        private void editOptionClicked(final int adapterPosition)
        {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.add_item, null);

            final EditText date,tittle,task,priority;
            Button save_button;
            Button high,medium,low;
            date = view.findViewById(R.id.date_editText);
            tittle = view.findViewById(R.id.tittle_editText);
            task = view.findViewById(R.id.task_editText);
            priority = view.findViewById(R.id.priority_textView);

            high = view.findViewById(R.id.highPriority_button);
            medium = view.findViewById(R.id.MediumPriority_button);
            low = view.findViewById(R.id.LowPriority_button);
            save_button = view.findViewById(R.id.save_button);

            final NewItem item = list.get(adapterPosition);


            date.setText( item.getDate());
            tittle.setText(item.getTittle());
            task.setText(item.getTask());

            String p = item.getPriority();
            if(p.equals("high"))
            {
                high.setBackgroundColor(Color.GREEN);
            }
            else if(p.equals("medium"))
            {
                medium.setBackgroundColor(Color.GREEN);
            }
            else //low
            {
                low.setBackgroundColor(Color.GREEN);
            }

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    NewItem newItem = new NewItem();
                    newItem.setTittle(tittle.getText().toString());
                    newItem.setTask(task.getText().toString());
                    newItem.setDate(date.getText().toString());
                    newItem.setPriority("low");

                    if(!newItem.getTittle().isEmpty())
                    {
                        updateData(newItem,adapterPosition);
                    }

                }
            });



        }

        private void updateData(NewItem newItem,int adapterPosition)
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,Object> map = new HashMap<>();
            map.put("tittle",newItem.getTittle());
            map.put("task",newItem.getTask());
            map.put("date",newItem.getDate());
            map.put("priority",newItem.getPriority());
            db.collection(username).document(list.get(adapterPosition).getTittle().toLowerCase()).update(map);
            list.set(adapterPosition,newItem);
            notifyDataSetChanged();
            dialog.dismiss();
        }

        private void deleteOptionClicked(final int adapterPosition)
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(username.toLowerCase()).document(list.get(adapterPosition).getTittle().toLowerCase()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    list.remove(adapterPosition);
                    notifyDataSetChanged();
                    Log.d(TAG, "onStart onSuccess: deleted");

                }
            });
        }
    }
}