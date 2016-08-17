package com.example.lokit.shush;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class addTask extends AppCompatActivity {
    private RadioGroup rg;
    private RadioButton rb;
    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        System.out.print("YO");
        addListenerOnButton();
    }

    public void addListenerOnButton() {

        rg = (RadioGroup)findViewById(R.id.radioGroup);
        b = (Button)findViewById(R.id.createtask);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sID = rg.getCheckedRadioButtonId();
                rb = (RadioButton) findViewById(sID);
                String selected = rb.getText().toString();

                Intent intent = new Intent(addTask.this, fillLoc.class);
                intent.putExtra("eventtype",selected);
                addTask.this.startActivity(intent);
                addTask.this.finish();
            }
        });
    }
}
