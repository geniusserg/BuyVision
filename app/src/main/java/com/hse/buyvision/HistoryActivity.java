package com.hse.buyvision;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

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
        /*
        history_text = findViewbyId(R.id.history_text);
        history_image = findViewbyId(R.id.history_image);
        prev_button = findViewbyId(R.id.prev_button);
        next_button = findViewbyId(R.id.next_button);
        exit_button = findViewbyId(R.id.exit_button);
        next_button.setOnClickListener(v -> {
            updateUI(viewModel.getNext());
        });
        updateUI(viewModel.getNext());
        */
    }
    public void updateUI(ItemModel item){
        if (item == null){
            String noTextError = "Текст не был распознан";
            history_text.setText(noTextError);
            Speech.vocalise(noTextError);
        }
        else{
            String text = "[" + item.date.getMonth() + "]\n" + item.text;
            history_text.setText(text);
            history_image.setImageBitmap(BitmapFactory.decodeFile(item.photo));
            Speech.vocalise(text);
        }
    }


}