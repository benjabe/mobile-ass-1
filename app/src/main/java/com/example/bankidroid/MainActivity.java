package com.example.bankidroid;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int CENTS_MIN = 9000;      // random starting money bounds
    private static final int CENTS_MAX = 11000;     // ^
    public static final int RESULT_CODE_TRANSFER = 0;

    public static final String BALANCE_STRING = "BALANCE";
    public static final String TRANSFER_AMOUNT_STRING = "TRANSFER_AMOUNT";
    public static final String TRANSFER_RECIPIENT_STRING = "TRANSFER_RECIPIENT";
    public static final String TRANSACTIONS_STRING = "TRANSACTIONS";

    Money balance;
    TextView lblBalance;

    ArrayList<String> transactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the activity components
        lblBalance = findViewById(R.id.lbl_balance);
        Button btnTransactions = findViewById(R.id.btn_transactions);
        Button btnTransfer = findViewById(R.id.btn_transfer);

        // Randomise account balance
        Random r = new Random();
        balance = new Money(0, 0);
        final int cents = r.nextInt(CENTS_MAX - CENTS_MIN) + CENTS_MIN + 1;
        transfer(new Money(0, -cents), "You");
        lblBalance.setText(balance.toString());

        // Handle transactions button
        btnTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to transactions activity
                Intent intent = new Intent(getApplicationContext(), TransactionsActivity.class);

                intent.putExtra(TRANSACTIONS_STRING, transactions);
                startActivity(intent);
            }
        });

        // Handle transfer button
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to transfer activity
                Intent intent = new Intent(getApplicationContext(), TransferActivity.class);
                intent.putExtra(BALANCE_STRING, balance.toString());
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != RESULT_CODE_TRANSFER) {
            return;
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        // Perform transfer according to data from TransferActivity
        transfer(
                new Money(data.getStringExtra(TRANSFER_AMOUNT_STRING)),
                data.getStringExtra(TRANSFER_RECIPIENT_STRING)
        );
    }

    // Transfer money to a recipient and add the transaction to the list of transactions
    protected void transfer(Money transferAmount, String recipient) {
        balance.subtract(transferAmount);
        lblBalance.setText(balance.toString());

        transactions.add(
                new Transaction(new Date(), recipient, transferAmount, balance).toString()
        );
    }
}

class Money {
    private int euros;
    private int cents;

    /**
     * Constructs a Money object from euro and cent values.
     * @param e Euros.
     * @param c Cents.
     */
    Money(int e, int c) {
        euros = e;
        euros += c / 100;
        cents = c % 100;
    }

    /**
     * Constructs a Money object from a string.
     * @param s The string containing values in the format "euros.cents" or "euros,cents".
     */
    Money(String s) {
        String[] parts = s.split("[.,]");
        if (parts.length > 0) {
            if (!parts[0].isEmpty()) {
                euros = Integer.parseInt(parts[0]);
            }
        }
        if (parts.length > 1) {
            if (!parts[1].isEmpty()) {
                if (parts[1].length() == 1) {
                    cents = Integer.parseInt(parts[1]) * 10;
                } else if (parts[1].length() == 2) {
                    cents = Integer.parseInt(parts[1]);
                }
            }
        }
    }

    //@androidx.annotation.NonNull this should be here, but apparently it's not even a thing
    @Override
    public String toString() {
        return euros + "." + (cents < 10 ? "0" : "") +cents;
    }

    /**
     * Adds money
     * @param m The amount of money to add.
     */
    public void add(Money m) {
        euros += m.euros;
        cents += m.cents;
        if (cents > 99) {
            cents -= 100;
            euros += 1;
        }

        if (cents < 0) {
            cents += 100;
            euros -= 1;
        }
    }

    /**
     * Subtracts money.
     * @param m The amount of money to subtract
     */
    void subtract(Money m) {
        euros -= m.euros;
        cents -= m.cents;
        if (cents < 0) {
            cents += 100;
            euros -= 1;
        }

        if (cents > 99) {
            cents -= 100;
            euros += 1;
        }
    }

    /**
     * Checks if the object has values between (inclusive) those given by min and max.
     * @param min The minimum value (inclusive).
     * @param max The maximum value (inclusive).
     * @return Returns true if the values are between (inclusive) the min and max.
     */
    boolean isBetween(Money min, Money max) {
        boolean between = true;

        if (euros < min.euros) {
            between = false;
        } else if (euros == min.euros && cents < min.cents) {
            between = false;
        }

        if (euros > max.euros) {
            between = false;
        } else if (euros == max.euros && cents > max.cents) {
            between = false;
        }

        return between;
    }
}

class Transaction {
    private Date date;
    private String recipient;
    private Money amount;
    private Money balanceAfterTransaction;

    Transaction(Date d, String r, Money a, Money b) {
        date = d;
        recipient = r;
        amount = a;
        balanceAfterTransaction = b;
    }

    // @androidx.annotation.NonNull
    @Override
    public String toString() {
        return date + " | " + recipient + " | " + amount + " | " + balanceAfterTransaction;
    }
}
