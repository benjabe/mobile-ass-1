package com.example.bankidroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class TransferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        // Get balance from intent
        final String balanceString = getIntent().getStringExtra(MainActivity.BALANCE_STRING);
        final Money balance = new Money(balanceString);

        // Get the activity components
        final Spinner spnRecipient = findViewById(R.id.spn_recipient);
        final EditText txtAmount = findViewById(R.id.txt_amount);
        final TextView lblAmountCheck = findViewById(R.id.lbl_amount_check);
        final Button btnPay = findViewById(R.id.btn_pay);

        // The button should not be enabled until a recipient and valid amount are entered
        btnPay.setEnabled(false);

        // Handle the amount input
        txtAmount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!txtAmount.getText().toString().isEmpty()) {
                    Log.println(0, "..", "..");
                    // Check to see if the value is within bounds,
                    Money transferValue = new Money(txtAmount.getText().toString());

                    if (!transferValue.isBetween(new Money(0, 1), balance)) {
                        // Not within bounds, output warning
                        lblAmountCheck.setText(R.string.out_of_bounds);
                        btnPay.setEnabled(false);
                    } else {
                        // Within bounds, transaction can be completed
                        lblAmountCheck.setText("");
                        btnPay.setEnabled(true);
                    }
                }
                return false;
            }
        });

        // Handle the pay button
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass back the values and finish the activity
                Intent intent = new Intent(TransferActivity.this, MainActivity.class);
                intent.putExtra(
                        MainActivity.TRANSFER_AMOUNT_STRING,
                        txtAmount.getText().toString()
                );
                intent.putExtra(
                        MainActivity.TRANSFER_RECIPIENT_STRING,
                        spnRecipient.getSelectedItem().toString()
                );
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
