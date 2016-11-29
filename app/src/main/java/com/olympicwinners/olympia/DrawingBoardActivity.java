package com.olympicwinners.olympia;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.UUID;

public class DrawingBoardActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawingView mDrawView;
    private ImageButton mIvCurrPaint;
    private float mSmallBrush, mMediumBrush, mLargeBrush;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_board);
        //get brush sizes from resources
        mSmallBrush = getResources().getInteger(R.integer.small_size);
        mMediumBrush = getResources().getInteger(R.integer.medium_size);
        mLargeBrush = getResources().getInteger(R.integer.large_size);
        //get drawing view's instance
        mDrawView = (DrawingView) findViewById(R.id.drawing);
        //set current color view as selected
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        if (paintLayout != null) {
            mIvCurrPaint = (ImageButton) paintLayout.getChildAt(0);
            if (mIvCurrPaint != null) {
                mIvCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            }
        }
        //set click listeners on new drawing button
        ImageButton btnNew = (ImageButton) findViewById(R.id.btn_new);
        if (btnNew != null) {
            btnNew.setOnClickListener(this);
        }
        ImageButton btnDraw = (ImageButton) findViewById(R.id.btn_draw);
        if (btnDraw != null) {
            btnDraw.setOnClickListener(this);
        }
        ImageButton btnErase = (ImageButton) findViewById(R.id.btn_erase);
        if (btnErase != null) {
            btnErase.setOnClickListener(this);
        }
        ImageButton btnSave = (ImageButton) findViewById(R.id.btn_save);
        if (btnSave != null) {
            btnSave.setOnClickListener(this);
        }
        ImageButton btnFill = (ImageButton) findViewById(R.id.btn_fill);
        if (btnFill != null) {
            btnFill.setOnClickListener(this);
        }
        //set initial brush size
        mDrawView.setBrushSize(mMediumBrush);
    }
    //use chosen color
    public void paintClicked(View view) {
        //set erase as false (if set previously as true)
        mDrawView.setErase(false);
        //reset brush size (if some other option was selected previously)
        mDrawView.setBrushSize(mDrawView.getLastBrushSize());
        //update color
        if (view != mIvCurrPaint) {
            //get the clicked image button
            ImageButton ivColorPallet = (ImageButton) view;
            //get color from tag
            String color = view.getTag().toString();
            //set new brush color
            mDrawView.setColor(color);
            //set current button's background as pressed button
            ivColorPallet.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            //set previous button's background as unpressed button
            mIvCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            //set current view as this button view
            mIvCurrPaint = (ImageButton) view;
        }
    }
    //use chosen patten
    public void patternClicked(View view) {
        //set erase as false (if set previously as true)
        mDrawView.setErase(false);
        //reset brush size (if some other option was selected previously)
        mDrawView.setBrushSize(mDrawView.getLastBrushSize());
        //update pattern
        if (view != mIvCurrPaint) {
            //get the clicked image button
            ImageButton ivPatten = (ImageButton) view;
            //set patten to brush
            mDrawView.setPattern(view.getTag().toString());
            //set current button's background as pressed button
            ivPatten.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            //set previous button's background as unpressed button
            mIvCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            //set current view as this button view
            mIvCurrPaint = (ImageButton) view;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_new:
                //new button
                setNewCanvas();
                break;
            case R.id.btn_draw:
                //draw button clicked
                setBrushSize();
                break;
            case R.id.btn_erase:
                //switch to erase - choose size
                switchToEraseMode();
                break;
            case R.id.btn_save:
                //save drawing
                saveDrawing();
                break;
            case R.id.btn_fill:
                //fill button
                mDrawView.fillColor();
                break;
            default:
                break;
        }
    }
    private void setNewCanvas() {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New drawing");
        newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mDrawView.startNew();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }
    private void setBrushSize() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle("Brush size:");
        brushDialog.setContentView(R.layout.brush_chooser);
        ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setErase(false);
                mDrawView.setBrushSize(mSmallBrush);
                mDrawView.setLastBrushSize(mSmallBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setErase(false);
                mDrawView.setBrushSize(mMediumBrush);
                mDrawView.setLastBrushSize(mMediumBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setErase(false);
                mDrawView.setBrushSize(mLargeBrush);
                mDrawView.setLastBrushSize(mLargeBrush);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }
    private void switchToEraseMode() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle("Eraser size:");
        brushDialog.setContentView(R.layout.brush_chooser);
        ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setErase(true);
                mDrawView.setBrushSize(mSmallBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setErase(true);
                mDrawView.setBrushSize(mMediumBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setErase(true);
                mDrawView.setBrushSize(mLargeBrush);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }
    //save drawing to gallery
    private void saveDrawing() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //save drawing
                mDrawView.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), mDrawView.getDrawingCache(),
                        UUID.randomUUID().toString() + ".png", "drawing");
                if (imgSaved != null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                } else {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                mDrawView.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.show();
    }
}