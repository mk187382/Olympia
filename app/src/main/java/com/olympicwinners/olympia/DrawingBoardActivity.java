package com.olympicwinners.olympia;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
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
    private boolean mIsBound = false;
    private MusicService mServ;
    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 1;

    private float mSmallBrush, mMediumBrush, mLargeBrush;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_board);

        doBindService();
        MusicService mServ = new MusicService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        //mServ.resumeMusic();
        mSmallBrush = getResources().getInteger(R.integer.small_size);
        mMediumBrush = getResources().getInteger(R.integer.medium_size);
        mLargeBrush = getResources().getInteger(R.integer.large_size);

        mDrawView = (DrawingView) findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        if (paintLayout != null) {
            mIvCurrPaint = (ImageButton) paintLayout.getChildAt(0);
            if (mIvCurrPaint != null) {
                mIvCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            }
        }
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
        mDrawView.setBrushSize(mMediumBrush);
    }

    private ServiceConnection Scon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                Scon, MoodMenu.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    public void paintClicked(View view) {

        mDrawView.setErase(false);

        mDrawView.setBrushSize(mDrawView.getLastBrushSize());

        if (view != mIvCurrPaint) {
            ImageButton ivColorPallet = (ImageButton) view;
            String color = view.getTag().toString();
            mDrawView.setColor(color);
            ivColorPallet.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            mIvCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            mIvCurrPaint = (ImageButton) view;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveDrawing();
            } else {
                Toast.makeText(this,
                        "External write permission has not been granted, cannot saved images",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_new:
                setNewCanvas();
                break;
            case R.id.btn_draw:
                setBrushSize();
                break;
            case R.id.btn_erase:
                switchToEraseMode();
                break;
            case R.id.btn_save:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        saveDrawing();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(this,
                                    "External storage permission required to save images",
                                    Toast.LENGTH_SHORT).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_EXTERNAL_STORAGE_RESULT);
                    }
                } else {
                    saveDrawing();
                }
                break;

            case R.id.btn_fill:
                mDrawView.fillColor();
                break;
            default:
                break;
        }
    }

    public void onPause() {
        super.onPause();
        mServ.pauseMusic();
        doUnbindService();
    }


    private void setNewCanvas() {
        final int widthOfNewBitmap = mDrawView.getWidth();
        final int heightOfNewBitmap = mDrawView.getHeight();
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New drawing");
        newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mDrawView.startNew(widthOfNewBitmap,heightOfNewBitmap);
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


    private void saveDrawing() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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
                            "Image could not be saved.", Toast.LENGTH_SHORT);
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