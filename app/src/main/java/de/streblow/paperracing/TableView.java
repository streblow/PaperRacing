package de.streblow.paperracing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by streblow on 11.12.2017.
 */

public class TableView extends View {

    private Context mAppContext;

    private float textsize;
    private int[] columnWidths;
    private int tableWidth;
    private int tableHeight;
    private int rowHeight;
    private String header;
    private String data;
    private String currentplayer;
    private String[] headerCells;
    private String[] dataCells;

    private float mScaleFactor;

    private int mode = 0;
    private static int NONE = 0;
    private static int DRAG = 1;
    private float startX = 0.0f;
    private float startY = 0.0f;
    private float translateX = 0.0f;
    private float translateY = 0.0f;
    private float previousTranslateX = 0.0f;
    private float previousTranslateY = 0.0f;
    private boolean dragged = false;

    public TableView(Context context) {
        super(context);
        init(context);
    }

    public TableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mAppContext = context;
    }

    public void updateResources(String header, String data, String currentplayer, Float textsize) {
        this.header = header;
        this.data = data;
        this.currentplayer = currentplayer;
        headerCells = header.split(";");
        dataCells = data.split(";", -1);
        if (dataCells.length == 1)
            dataCells = new String[0];
        Paint paint = new Paint();
        Typeface tf = Typeface.createFromAsset(mAppContext.getAssets(), "fonts/HandWritten.ttf");
        paint.setTypeface(tf);
        Rect rect = new Rect();
        paint.setShadowLayer(2.0f, 1.0f, 1.0f, Color.DKGRAY);
        paint.setTextSize(textsize);
        paint.setStrokeWidth(1.0f);
        columnWidths = new int[headerCells.length];
        tableWidth = 0;
        tableHeight = 0;
        rowHeight = 0;
        for (int j = 0; j < headerCells.length; j++) {
            columnWidths[j] = 0;
            String cell = " " + headerCells[j] + " ";
            paint.getTextBounds(cell, 0, cell.length(), rect);
            if (columnWidths[j] < rect.width())
                columnWidths[j] = rect.width();
            if (rowHeight < rect.height())
                rowHeight = rect.height();
            for (int i = 0; i < dataCells.length / headerCells.length; i++) {
                cell = "W" + dataCells[i * headerCells.length + j] + "W";
                paint.getTextBounds(cell, 0, cell.length(), rect);
                if (columnWidths[j] < rect.width())
                    columnWidths[j] = rect.width();
                if (rowHeight < rect.height())
                    rowHeight = rect.height();
            }
            tableWidth += columnWidths[j];
        }
        rowHeight = (rowHeight * 1500) / 1000; // increase height by factor 1.5
        tableHeight = rowHeight * (1 + dataCells.length / headerCells.length);
        this.textsize = paint.getTextSize();
        // if the table is smaller than current width, zoom in (scale)
        if (getWidth() > tableWidth)
            mScaleFactor = (float)getWidth() / (float)tableWidth;
        // if the table is wider than current width, zoom out (shrink)
        // shrink max to 150% of tableWidth, scroll otherwise
        else
            mScaleFactor = Math.max((float)getWidth() / (float)tableWidth, 0.666f);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!dragged)
            updateResources(header, data, currentplayer, textsize);
        /* canvas-drawing code */
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.translate(translateX / mScaleFactor, translateY / mScaleFactor);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        // draw table content, don't take care of the header, it'll be drawn later
        Typeface tf = Typeface.createFromAsset(mAppContext.getAssets(), "fonts/HandWritten.ttf");
        paint.setTypeface(tf);
        paint.setTextSize(textsize);
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.FILL);
        int x;
        paint.setColor(Color.BLUE);
        paint.clearShadowLayer();
        paint.setTextSize(textsize - 5.0f);
        for (int i = 0; i < dataCells.length / headerCells.length; i++) {
            x = 0;
            for (int j = 0; j < headerCells.length; j++) {
                if (dataCells[i * headerCells.length].substring(0, Math.min(currentplayer.length(), dataCells[i * headerCells.length].length())).equalsIgnoreCase(currentplayer)) {
                    int color = paint.getColor();
                    paint.setColor(Color.MAGENTA);
                    canvas.drawText(" " + dataCells[i * headerCells.length + j],
                            x, rowHeight + (i + 1) * rowHeight - (rowHeight * 250) / 1000, paint);
                    paint.setColor(color);
                } else
                    canvas.drawText(" " + dataCells[i * headerCells.length + j],
                        x, rowHeight + (i + 1) * rowHeight - (rowHeight * 250) / 1000, paint);
                x += columnWidths[j];
            }
        }
        canvas.restore();
        // draw table header (fixed to the top of the view)
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.translate(translateX / mScaleFactor, 0);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(0, 0, tableWidth, rowHeight, paint);
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(2.0f, 1.0f, 1.0f, Color.DKGRAY);
        paint.setTextSize(textsize);
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.FILL);
        x = 0;
        for (int i = 0; i < headerCells.length; i++) {
            canvas.drawText(" " + headerCells[i], x, rowHeight - (rowHeight * 250) / 1000, paint);
            x += columnWidths[i];
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                //amount for each coordinates. This works even when we are translating the first time because the initial
                //values for these two variables is zero.
                startX = event.getX() - previousTranslateX;
                startY = event.getY() - previousTranslateY;
                break;
            case MotionEvent.ACTION_MOVE:
                translateX = event.getX() - startX;
                translateY = event.getY() - startY;
                if (tableWidth * mScaleFactor > getWidth()) {
                    if (translateX > 0)
                        translateX = 0;
                    if (translateX + tableWidth * mScaleFactor < getWidth())
                        translateX = getWidth() - tableWidth * mScaleFactor;
                } else {
                    if (translateX < 0) {
                        translateX = 0;
                    }
                    if (translateX + tableWidth * mScaleFactor > getWidth()) {
                        translateX = getWidth() - tableWidth * mScaleFactor;
                    }
                }
                if (tableHeight * mScaleFactor > getHeight()) {
                    if (translateY > 0)
                        translateY = 0;
                    if (translateY + tableHeight * mScaleFactor < getHeight())
                        translateY = getHeight() - tableHeight * mScaleFactor;
                } else {
                    if (translateY < 0) {
                        translateY = 0;
                    }
                    if (translateY + tableHeight * mScaleFactor > getHeight()) {
                        translateY = getHeight() - tableHeight * mScaleFactor;
                    }
                }
                if (translateX > 0)
                    translateX = 0;
                if (translateY > 0)
                    translateY = 0;
                //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2) +
                        Math.pow(event.getY() - (startY + previousTranslateY), 2));
                if (distance > 0) {
                    dragged = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                dragged = false;
                //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                //previousTranslate
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = DRAG;
                //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                //and previousTranslateY when the second finger goes up
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;
        }
        if (mode == DRAG && dragged) {
            invalidate();
        }
        return true;
    }

}
