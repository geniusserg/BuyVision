package com.hse.buyvision;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {

    private int id = 0;
    private TextView history_text;
    private ImageView history_image;
    private DBWrapper dbWrapper;
    private ItemModel item;
    private Button next_button;
    private Button prev_button;
    private Button exit_button;
    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dbWrapper = new DBWrapper(new DBHelper(getApplicationContext()));
        viewModel = new ViewModel(dbWrapper);
        history_text = findViewById(R.id.history_text);
        history_image = findViewById(R.id.history_view);
        //prev_button = findViewbyId(R.id.prev_button);
        next_button = findViewById(R.id.next_button);
        //exit_button = findViewbyId(R.id.exit_button);
        next_button.setOnClickListener(v -> {
            updateUI(viewModel.getNext());
        });
        updateUI(viewModel.getNext());

    }
    public void updateUI(ItemModel item){
        System.out.println("UpdateUI"+item.text);
        if (item == null){
            String noTextError = "Текст не был распознан";
            history_text.setText(noTextError);
            Speech.vocalise(noTextError);
        }
        else{
            String text = "[" + item.date.getMonth() + "]\n" + item.text;
            System.out.println(item.text);
            history_text.setText(text);
            history_image.setImageBitmap(BitmapFactory.decodeFile(item.photo));

            Speech.vocalise(text);
        }
    }


}