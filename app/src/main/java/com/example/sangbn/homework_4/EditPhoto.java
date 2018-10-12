package com.example.sangbn.homework_4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class EditPhoto extends AppCompatActivity {

    public PhotoEditor mPhotoEditor = null;
    public String photoPath = "";
    Float Latitude = 0.0f;
    Float Longitude = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            photoPath = extras.getString("photoPath");
            Longitude = extras.getFloat("longitude");
            Latitude = extras.getFloat("latitude");
        }
        PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);
        mPhotoEditorView.getSource().setImageURI(Uri.parse(photoPath));

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();
    }

    public void brushDrawing(View v) {
        mPhotoEditor.setBrushDrawingMode(true);
    }

    public void brushEraser(View v) {
        mPhotoEditor.brushEraser();
    }

    public void filterEffect(View v) {
        final ArrayList effect = new ArrayList();
        effect.add(PhotoFilter.AUTO_FIX.toString());
        effect.add(PhotoFilter.BLACK_WHITE.toString());
        effect.add(PhotoFilter.BRIGHTNESS.toString());
        effect.add(PhotoFilter.CONTRAST.toString());
        effect.add(PhotoFilter.CROSS_PROCESS.toString());
        effect.add(PhotoFilter.DOCUMENTARY.toString());
        effect.add(PhotoFilter.FISH_EYE.toString());
        effect.add(PhotoFilter.SEPIA.toString());
        effect.add(PhotoFilter.TINT.toString());
        effect.add(PhotoFilter.SHARPEN.toString());

        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);

        for (int x = 0; x < effect.size(); x++) {

            ImageView image = new ImageView(this);
            image.setBackgroundResource(R.drawable.coursesicon10);
            TextView text = new TextView(this);
            text.setText(effect.get(x).toString());
            text.setTextSize(10);
            text.setTextColor(Color.RED);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            final String eff = effect.get(x).toString();
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPhotoEditor.setFilterEffect(PhotoFilter.valueOf(eff));
                }
            });
            LinearLayout linearLayout2 = new LinearLayout(this);
            linearLayout2.setOrientation(LinearLayout.VERTICAL);
            linearLayout2.addView(text);
            linearLayout2.addView(image);
            linearLayout1.addView(linearLayout2);
        }

    }

    public void save(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

        mPhotoEditor.saveAsFile(photoPath, new PhotoEditor.OnSaveListener() {
            @Override
            public void onSuccess(@NonNull String imagePath) {
                PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);
                mPhotoEditorView.getSource().setImageResource(0);
                mPhotoEditorView.getSource().setImageURI(Uri.parse(imagePath));
                Log.e("PhotoEditor", "Image Saved Successfully");
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("PhotoEditor", "Failed to save Image");
            }
        });
    }

    public void viewMap(View v) {
        Intent i = new Intent(this, MapBox.class);
        i.putExtra("latitude", Latitude);
        i.putExtra("longitude", Longitude);
        startActivity(i);
    }
}
