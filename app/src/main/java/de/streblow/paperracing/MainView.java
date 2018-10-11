package de.streblow.paperracing;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by streblow on 27.10.17.
 */

public class MainView extends View {

    public Circuit circuit;
    public Game game;

    public int gridSize;
    public int sizeWidth;
    public int sizeHeight;
    public int marginWidth;
    public int marginHeight;
    public float zoomCar;
    public int numCheckpoints;

    public int paperColor;
    public int gridColor;
    public int titleColor;

    public int animationDuration;

    private Bitmap bm;
    private int bm_w;
    private int bm_h;
    private Bitmap car;

    private Context mAppContext;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor;
    public float maxZoomFactor;
    private int mode = 0;
    private static int NONE = 0;
    private static int DRAG = 1;
    private static int ZOOM = 2;
    private float startX = 0.0f;
    private float startY = 0.0f;
    private float translateX = 0.0f;
    private float translateY = 0.0f;
    private float previousTranslateX = 0.0f;
    private float previousTranslateY = 0.0f;
    private boolean dragged = false;
    public boolean firstRun = true;
    public boolean buttonHidden;

    public MainView(Context context) {
        super(context);
        init(context);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        Game dummy = new Game();
        init(context,
            new int[] {Player.COM, Player.COM, Player.COM},
            new String[] {dummy.nameOfFavoritePlayer, "2", "3"});
    }

    public void init(Context context, int[] types, String[] names) {
        // here some constant properties which should be customizable in settings
        gridSize = 40;
        sizeWidth = 20;
        sizeHeight = 20;
        marginWidth = 4;
        marginHeight = 4;
        zoomCar = 1.5f;
        maxZoomFactor = 6.0f;
        numCheckpoints = 12;

        mAppContext = context;

        paperColor = Color.rgb(255,245,220);
        gridColor = Color.rgb(149,194,229);
        titleColor = Color.BLUE;

        animationDuration = 500;

        circuit = new Circuit(sizeWidth, sizeHeight, gridSize, numCheckpoints);
        game = new Game();
        game.circuit = circuit;
        game.newgame(types, names);
        updateResources();

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        startX = 0.0f;
        startY = 0.0f;
        translateX = 0.0f;
        translateY = 0.0f;
        previousTranslateX = 0.0f;
        previousTranslateY = 0.0f;
        mScaleFactor = 1.0f;
    }

    public void updateResources() {
        bm_w = (sizeWidth + 2 * marginWidth - 1) * gridSize;
        bm_h = (sizeHeight + 2 * marginHeight - 1) * gridSize;
        bm = Bitmap.createBitmap(bm_w, bm_h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawColor(paperColor);
        Paint paint = new Paint();
        paint.setColor(gridColor);
        for (int x = 0; x < sizeWidth + 2 * marginWidth; x++)
            canvas.drawLine((float)(x * gridSize), 0.0f ,
                    (float)(x * gridSize), (float)((sizeHeight + 2 * marginHeight - 1) * gridSize), paint);
        for (int y = 0; y < sizeHeight + 2 * marginHeight; y++)
            canvas.drawLine(0.0f ,(float)(y * gridSize),
                    (float)((sizeWidth + 2 * marginWidth - 1) * gridSize), (float)(y * gridSize), paint);
        circuit.drawCircuit(bm);
        car = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        Matrix matrix = new Matrix();
        float sx = zoomCar * (float)(gridSize) / (float)(car.getWidth());
        float sy = zoomCar * (float)(gridSize) / (float)(car.getHeight());
        matrix.postScale(sx, sy);
        Bitmap car_zoomed = Bitmap.createBitmap(
                car, 0, 0, car.getWidth(), car.getHeight(), matrix, false);
        car = car_zoomed;
    }

    public Bitmap colorBitmap(Bitmap bitmap, int color) {
        int [] pixels = new int [bitmap.getHeight() * bitmap.getWidth()];
        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());
        for(int i = 0; i < pixels.length; i++)
        {
            if(pixels[i] == Color.WHITE)
                pixels[i] = color;
        }
        newBitmap.setPixels(pixels,0, bitmap.getWidth(),0, 0,
                bitmap.getWidth(), bitmap.getHeight());
        return newBitmap;
    }

    // 0 degrees (radian 0) is up, clockwise angle
    public void drawCarBitmap(Canvas canvas, int grid_x, int grid_y, int color, float radian) {
        Bitmap car_colorized = colorBitmap(car, color);
        Matrix matrix = new Matrix();
        matrix.postRotate(radian / 3.1416f * 180.0f);
        Bitmap car_rotated = Bitmap.createBitmap(car_colorized, 0, 0, car_colorized.getWidth(), car_colorized.getHeight(), matrix, true);
        float x = (float)(grid_x * gridSize) - (float)car_rotated.getWidth() / 2.0f;
        float y = (float)(grid_y * gridSize) - (float)car_rotated.getHeight() / 2.0f;
        canvas.drawBitmap(car_rotated, x, y, null);
    }

    public void drawCircle(Canvas canvas, int x, int y, int color) {
        int s = (int)gridSize / 3;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(color);
        canvas.drawCircle(x * gridSize, y * gridSize,
            s, paint);
    }

    public void fillCircle(Canvas canvas, int x, int y, int color, int player) {
        int s = (int)(gridSize / 4) - player;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(color);
        canvas.drawCircle(x * gridSize, y * gridSize,
            s, paint);
    }

    public void drawLine(Canvas canvas, int x1, int y1, int x2, int y2, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawLine(x1 * gridSize,y1 * gridSize,
            x2 * gridSize, y2 * gridSize, paint);
    }

    public void drawCars(Canvas canvas) {
        Car car;
        int color;
        int l;
        int x1;
        int y1;
        int x2;
        int y2;
        int sx;
        int sy;
        int i;
        int j;
        for (j = 0; j < game.getplayercount(); j++) {
            car = game.getplayer(j).getcar();
            color = car.getcolor();
            l = car.getturns();
            sx = car.getstartx();
            sy = car.getstarty();
            x1 = sx;
            y1 = sy;
            double radian = 0.0;
            for (i = 0; i <= l; i++) {
                x2 = x1 + car.gethistory(i).getx();
                y2 = y1 + car.gethistory(i).gety();
                drawLine(canvas, x1, y1, x2, y2, color);
                if ((x2 != x1) || (y2 != y1)) { // car moved, update radian
                    if ((x2 - x1) > 0) // right up or down
                        if ((y2 - y1) <= 0) // up
                            radian = Math.PI / 2 - Math.atan((double)(y1 - y2) / (double)(x2 - x1));
                        else // down
                            radian = Math.PI / 2 + Math.atan((double)(y2 - y1) / (double)(x2 - x1));
                    else if ((x2 - x1) < 0) // left up or down
                        if ((y2 - y1) <= 0) // up
                            radian = Math.PI * 1.5 + Math.atan((double)(y1 - y2) / (double)(x1 - x2));
                        else // down
                            radian = Math.PI * 1.5 - Math.atan((double)(y2 - y1) / (double)(x1 - x2));
                    else // up or down
                        if ((y2 - y1) <= 0) // up
                            radian = 0.0;
                        else // down
                            radian = Math.PI;
                }
                if (i < l)
                    fillCircle(canvas, x2, y2, color, j);
                else {
                    drawCarBitmap(canvas, x2, y2, color, (float)radian);
                }
                x1 = x2;
                y1 = y2;
            }
        }
    }

    public void drawCursor(Canvas canvas) {
        if (game.currentplayer().type() == Player.HUM) {
            int green = 0xFF00AA00;
            int red = 0xFFAA0000;
            Car car = game.currentplayer().getcar();
            int x = car.getx() + car.getvector().getx();
            int y = car.gety() + car.getvector().gety();
            drawCircle(canvas, x, y, circuit.getTerrain(x, y) > 0 ? green : red); //SAME
            drawCircle(canvas, x - 1, y, circuit.getTerrain(x - 1, y) > 0 ? green : red); //LEFT
            drawCircle(canvas, x, y - 1, circuit.getTerrain(x, y - 1) > 0 ? green : red); //UP
            drawCircle(canvas, x + 1, y, circuit.getTerrain(x + 1, y) > 0 ? green : red); //RIGHT
            drawCircle(canvas, x, y + 1, circuit.getTerrain(x, y + 1) > 0 ? green : red);  //DOWN
            drawCircle(canvas, x - 1, y - 1, circuit.getTerrain(x - 1, y - 1) > 0 ? green : red); //UPLEFT
            drawCircle(canvas, x - 1, y + 1, circuit.getTerrain(x - 1, y + 1) > 0 ? green : red); //DOWNLEFT
            drawCircle(canvas, x + 1, y - 1, circuit.getTerrain(x + 1, y - 1) > 0 ? green : red); //UPRIGHT
            drawCircle(canvas, x + 1, y + 1, circuit.getTerrain(x + 1, y + 1) > 0 ? green : red); //DOWNRIGHT
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mScaleFactor == 0.0f) {
            float minXScale = (float)getWidth() / (float)bm_w;
            float minYScale = (float)getHeight() / (float)bm_h;
            mScaleFactor = Math.max(Math.min(minXScale, minYScale), Math.min(mScaleFactor, maxZoomFactor));
        }
        /* canvas-drawing code */
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.translate(translateX / mScaleFactor, translateY / mScaleFactor);
        canvas.drawARGB(255,128,128,128);
        canvas.drawBitmap(bm, 0, 0, null);
        drawCars(canvas);
        if (!game.finished() && !firstRun)
            drawCursor(canvas);
        canvas.restore();
        // view-drawing code: draw text unzoomed and centered
        Paint paint = new Paint();
        Typeface tf = Typeface.createFromAsset(mAppContext.getAssets(), "fonts/HandWritten.ttf");
        paint.setTypeface(tf);
        Rect rect = new Rect();
        paint.setShadowLayer(2.0f, 1.0f, 1.0f, Color.DKGRAY);
        paint.setTextSize(80.0f);
        paint.setStrokeWidth(1.0f);
        String nameHeight = "";
        for (int i = 0; i < game.getplayercount(); i++)
            nameHeight += game.player[i].getname();
        paint.getTextBounds(nameHeight, 0, nameHeight.length(), rect);
        while (rect.height() + rect.height() / 2 > getHeight() / 4 / game.getplayercount() && paint.getTextSize() > 10.0f) {
            paint.setTextSize(Math.max(10.0f, paint.getTextSize() - 5.0f));
            paint.getTextBounds(nameHeight, 0, nameHeight.length(), rect);
        }
        int maxHeight = rect.height() + rect.height() / 2;
        int maxWidthBase = 0;
        int indexMaxWidthBase = 0;
        String nameWidth = "";
        for (int i = 0; i < game.getplayercount(); i++) {
            nameWidth = "xx" + game.player[i].getname();
            paint.getTextBounds(nameWidth, 0, nameWidth.length(), rect);
            if (rect.width() > maxWidthBase) {
                maxWidthBase = rect.width();
                indexMaxWidthBase = i;
            }
        }
        nameWidth = "xx" + game.player[indexMaxWidthBase].getname();
        paint.getTextBounds(nameWidth, 0, nameWidth.length(), rect);
        while (rect.width() > getWidth() / 4 && paint.getTextSize() > 10.0f) {
            paint.setTextSize(Math.max(10.0f, paint.getTextSize() - 5.0f));
            paint.getTextBounds(nameWidth, 0, nameWidth.length(), rect);
        }
        int maxWidth = rect.width();
        paint.getTextBounds("xx", 0, "xx".length(), rect);
        int spacerWidth = rect.width();
        paint.getTextBounds(nameHeight, 0, nameHeight.length(), rect);
        int newNameHeight = rect.height();
        if (newNameHeight + newNameHeight / 2 < maxHeight)
            maxHeight = newNameHeight + newNameHeight / 2;
        if (!firstRun/* && !game.finished()*/) {
            for (int i = 0; i < game.getplayercount(); i++) {
                int currentturn = game.getplayer(i).getcar().getturns();
                int level = game.getplayer(i).recursiondepth;
                String status = "(" + level + ") - " + currentturn;
                if (game.getplayer(i).getcar().fault[currentturn])
                    status += " " + getResources().getString(R.string.crashed);
                // print travelled totals (debug information
                //Double d = game.getplayer(i).travelledtotal() * 360.0 / 2.0 / Math.PI;
                //status += " " + String.format("%.1f", d) + "°";
                paint.setColor(game.getplayer(i).getcar().getcolor());
                canvas.drawText(game.getplayer(i).getname(), spacerWidth, (i + 1) * maxHeight, paint);
                canvas.drawText(status, spacerWidth + maxWidth, (i + 1) * maxHeight, paint);
            }
        }
        if (firstRun) {
            paint.setColor(titleColor);
            paint.setShadowLayer(4.0f, 3.0f, 3.0f, Color.DKGRAY);
            paint.setTextSize(80.0f);
            paint.setStrokeWidth(6.0f);
            String text1 = getResources().getString(R.string.firstrun_1);
            String text2 = getResources().getString(R.string.firstrun_2);
            rect = new Rect();
            paint.getTextBounds(text1, 0, text1.length(), rect);
            while (rect.width() > getWidth() && paint.getTextSize() > 10.0f) {
                paint.setTextSize(Math.max(10.0f, paint.getTextSize() - 5.0f));
                paint.getTextBounds(text1, 0, text1.length(), rect);
            }
            while (rect.height() > getHeight() / 4 && paint.getTextSize() > 10.0f) {
                paint.setTextSize(Math.max(10.0f, paint.getTextSize() - 5.0f));
                paint.getTextBounds(text1, 0, text1.length(), rect);
            }
            paint.getTextBounds(text2, 0, text2.length(), rect);
            while (rect.width() > getWidth() && paint.getTextSize() > 10.0f) {
                paint.setTextSize(Math.max(10.0f, paint.getTextSize() - 5.0f));
                paint.getTextBounds(text2, 0, text2.length(), rect);
            }
            paint.getTextBounds(text1, 0, text1.length(), rect);
            canvas.drawText(text1, (getWidth() - rect.width()) / 2, getHeight() / 4, paint);
            paint.getTextBounds(text2, 0, text2.length(), rect);
            canvas.drawText(text2, (getWidth() - rect.width()) / 2, getHeight() / 4 + rect.height(), paint);
            int y = getHeight() / 4 + rect.height();
            paint.setTextSize(Math.max(10.0f, paint.getTextSize() - 10.0f));
            paint.setStrokeWidth(6.0f);
            String text3 = getResources().getString(R.string.firstrun_3);
            paint.getTextBounds(text3, 0, text3.length(), rect);
            while (rect.width() > getWidth() && paint.getTextSize() > 10.0f) {
                paint.setTextSize(Math.max(10.0f, paint.getTextSize() - 2.0f));
                paint.getTextBounds(text3, 0, text3.length(), rect);
            }
            y += rect.height();
            paint.getTextBounds(text3, 0, text3.length(), rect);
            canvas.drawText(text3, (getWidth() - rect.width()) / 2, y + rect.height(), paint);
        }
        if (game.finished()) {
            if (!buttonHidden) {
                ((MainActivity)mAppContext).findViewById(R.id.imageButton1).setVisibility(View.INVISIBLE);
                ((MainActivity)mAppContext).findViewById(R.id.imageButton2).setVisibility(View.INVISIBLE);
                ((MainActivity)mAppContext).findViewById(R.id.imageButton3).setVisibility(View.INVISIBLE);
                ((MainActivity)mAppContext).findViewById(R.id.imageButton4).setVisibility(View.INVISIBLE);
                ((MainActivity)mAppContext).findViewById(R.id.imageButton5).setVisibility(View.INVISIBLE);
                ((MainActivity)mAppContext).findViewById(R.id.imageButton6).setVisibility(View.INVISIBLE);
                ((MainActivity)mAppContext).findViewById(R.id.imageButton7).setVisibility(View.INVISIBLE);
                ((MainActivity)mAppContext).findViewById(R.id.imageButton8).setVisibility(View.INVISIBLE);
                ((MainActivity)mAppContext).findViewById(R.id.imageButton9).setVisibility(View.INVISIBLE);
                ((MainActivity)mAppContext).findViewById(R.id.imageButtonUndo).setVisibility(View.INVISIBLE);
                buttonHidden = true;
            }
            paint.setColor(game.player[game.winner].getcar().getcolor());
            paint.setShadowLayer(3.0f, 2.0f, 2.0f, Color.DKGRAY);
            paint.setTextSize(80.0f);
            paint.setStrokeWidth(6.0f);
            String text1 = getResources().getString(R.string.finished_1);
            String text2 = game.player[game.winner].getname() + " " + getResources().getString(R.string.finished_2);
            rect = new Rect();
            paint.getTextBounds(text1, 0, text1.length(), rect);
            while (rect.width() > getWidth()) {
                paint.setTextSize(paint.getTextSize() - 5.0f);
                paint.getTextBounds(text1, 0, text1.length(), rect);
            }
            while (rect.height() > getHeight() / 3 && paint.getTextSize() > 10.0f) {
                paint.setTextSize(Math.max(10.0f, paint.getTextSize() - 5.0f));
                paint.getTextBounds(text1, 0, text1.length(), rect);
            }
            paint.getTextBounds(text2, 0, text2.length(), rect);
            while (rect.width() > getWidth()) {
                paint.setTextSize(paint.getTextSize() - 5.0f);
                paint.getTextBounds(text2, 0, text2.length(), rect);
            }
            paint.getTextBounds(text1, 0, text1.length(), rect);
            canvas.drawText(text1, (getWidth() - rect.width()) / 2, getHeight() / 3, paint);
            paint.getTextBounds(text2, 0, text2.length(), rect);
            canvas.drawText(text2, (getWidth() - rect.width()) / 2, getHeight() / 3 + rect.height(), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                //amount for each coordinates This works even when we are translating the first time because the initial
                //values for these two variables is zero.
                startX = event.getX() - previousTranslateX;
                startY = event.getY() - previousTranslateY;
                break;
            case MotionEvent.ACTION_MOVE:
                translateX = event.getX() - startX;
                translateY = event.getY() - startY;
                if (bm_w * mScaleFactor > getWidth()) {
                    if (translateX > 0)
                        translateX = 0;
                    if (translateX + bm_w * mScaleFactor < getWidth())
                        translateX = getWidth() - bm_w * mScaleFactor;
                } else {
                    if (translateX < 0) {
                        translateX = 0;
                    }
                    if (translateX + bm_w * mScaleFactor > getWidth()) {
                        translateX = getWidth() - bm_w * mScaleFactor;
                    }
                }
                if (bm_h * mScaleFactor > getHeight()) {
                    if (translateY > 0)
                        translateY = 0;
                    if (translateY + bm_h * mScaleFactor < getHeight())
                        translateY = getHeight() - bm_h * mScaleFactor;
                } else {
                    if (translateY < 0) {
                        translateY = 0;
                    }
                    if (translateY + bm_h * mScaleFactor > getHeight()) {
                        translateY = getHeight() - bm_h * mScaleFactor;
                    }
                }
                //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2) +
                        Math.pow(event.getY() - (startY + previousTranslateY), 2));
                if (distance > 0) {
                    dragged = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
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
        mScaleDetector.onTouchEvent(event);
        if ((mode == DRAG && dragged) || mode == ZOOM) {
            invalidate();
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            float minXScale = (float)getWidth() / (float)bm_w;
            float minYScale = (float)getHeight() / (float)bm_h;
            mScaleFactor = Math.max(Math.min(minXScale, minYScale), Math.min(mScaleFactor, maxZoomFactor));
            invalidate();
            return true;
        }
    }

}