package com.example.bankidroid;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        // Set up recycler view
        final RecyclerView rcv = findViewById(R.id.rcv_transactions);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(layoutManager);

        // Get contents
        // Note: this is probably a bad solution, but it gets the job done
        ArrayList<String> contents =
                getIntent().getStringArrayListExtra(MainActivity.TRANSACTIONS_STRING);

        String[] dataset = new String[contents.size() + 1];
        // If I don't do this the first transaction doesn't appear
        dataset[0] = "";

        for (int i = 0; i < contents.size(); i++) {
            dataset[i+1] = (String)contents.toArray()[i];
        }

        TransactionsAdapter adapter = new TransactionsAdapter(dataset); // what do here

        rcv.setAdapter(adapter);

    }
}

class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder> {
    private String[] dataset;
    static class TransactionsViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TransactionsViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    TransactionsAdapter(String[] d) {
        dataset = d;
    }

    // should be nonnull but that doesn't work
    @Override
    public TransactionsAdapter.TransactionsViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        TextView v = (TextView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new TransactionsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {
        holder.textView.setText(dataset[position]);
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }
}