package com.shaki1093.subhunter;

/*import androidx.appcompat.app.AppCompatActivity;*/

import android.app.Activity;
import android.view.MotionEvent;
import android.view.Window;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;
import java.util.Random;

public class SubHunter extends Activity {

    //Variables
    int numberHorizontalPixel;
    int numberVerticalPixel;
    int blockSize;
    int gridWidth = 40;
    int gridHeight;
    int subHorizontalPosition = -100;
    int subVerticalPosition = -100;
    int shotTaken;
    int distanceFromSub;
    float horizontalTouch;
    float verticalTouch;
    boolean hit = false;
    boolean debugging = true;

    //Objects
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //This will get the current device resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Initialize the size base variable
        //based on the screen resolution
        numberHorizontalPixel = size.x;
        numberVerticalPixel = size.y;
        blockSize = numberHorizontalPixel / gridWidth;
        gridHeight = numberVerticalPixel / blockSize;

        //Initialize all object and ready for drawing
        blankBitmap = Bitmap.createBitmap(numberHorizontalPixel, numberVerticalPixel, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        paint = new Paint();

        //This will set the drawing on android as the view for the app
        setContentView(gameView);

        //This is where the magic happens
        Log.d("Debugging", "In onCreate");
        newGame();
        draw();
    }

    /*
    This code will execute when a new
    game needs to be started. It will
    happen when the app is first started
    and after the player wins the game.
     */

    void newGame(){
        Random random = new Random();
        subHorizontalPosition = random.nextInt(gridWidth);
        subVerticalPosition = random.nextInt(gridHeight);
        shotTaken = 0;

        Log.d("Debugging", "In newGame");
    }

    /*
    Here we will do all the drawing.
    The grid lines, the HUD,
    the touch indicator and the
    "BOOM" when a sub is hit
     */

    void draw(){
        gameView.setImageBitmap(blankBitmap);
        canvas.drawColor(Color.argb(255, 255, 255, 255));

        //Change the paint color to black
        paint.setColor(Color.argb(255, 0, 0, 0));

        //This will draw the vertical lines of the grid
        for (int i = 0; i < gridWidth ; i++) {
            canvas.drawLine(blockSize * i, 0, blockSize * i,
                    numberVerticalPixel, paint);
        }

        //This will dra the horizontal lines
        for (int i = 0; i < gridHeight; i++) {
            canvas.drawLine(0, blockSize * i, numberHorizontalPixel,
                    blockSize * i, paint);
        }
        //Draw the player shot
        canvas.drawRect(horizontalTouch * blockSize, verticalTouch * blockSize,
                (horizontalTouch * blockSize) + blockSize, (verticalTouch * blockSize) +
                        blockSize, paint);

        //This will do the Re size text for score and distance text
        paint.setTextSize(blockSize * 2);
        paint.setColor(Color.argb(255, 0, 0, 255));
       canvas.drawText(
                "Shot Take: " + shotTaken +
                        " Distance: " + distanceFromSub, blockSize, blockSize * 1.75f, paint
        );
        Log.d("Debugging", "In draw");
        printDebuggingText();
    }

    /*
    This part will handle that the player
    has tapped the screen
     */

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        Log.d("Debugging", "In onTouchEvent");
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
            takeShot(motionEvent.getX(), motionEvent.getY());
        }
        return true;
    }

    /*
    The code here will execute when
    the player taps the screen it will
    calculate the distance from the sub
    and determine a hit or miss
     */

    void takeShot(float touchX, float touchY){
        Log.d("Debugging", "In takeShot");
        shotTaken++;

        //convert the float screen coordinate
        //into int grid coordinate
        horizontalTouch = (int)touchX / blockSize;
        verticalTouch = (int)touchY / blockSize;

        //Did the shot hit?
        hit = horizontalTouch == subHorizontalPosition && verticalTouch == subVerticalPosition;

        //How far away horizontal and vertical
        //was the shot from the sub
        int horizontalGap = (int)horizontalTouch - subHorizontalPosition;
        int verticalGap = (int)verticalTouch - subVerticalPosition;

        //Use pythagoras theorem to get the
        //distance travelled in a straight line
        distanceFromSub = (int)Math.sqrt(((horizontalGap * horizontalGap) +
                (verticalGap * verticalGap)));

        //if there is a hit call boom
        if (hit){
            boom();
        }
        else {
            draw();
        }
    }

    //This code says "BOOM"
    void boom(){
        gameView.setImageBitmap(blankBitmap);

        //Wipe screen with red color
        canvas.drawColor(Color.argb(255, 255, 0, 0));

        //Draw a text
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(blockSize * 10);
        canvas.drawText("BOOM!", blockSize * 4, blockSize * 14, paint);

        //Draw a prompt text to restart the game
        paint.setTextSize(blockSize * 2);
        canvas.drawText("Take the shot to start again", blockSize * 8, blockSize * 18,
                paint);

        //Start a new game
        newGame();
    }

    //Debug
    void printDebuggingText(){
        paint.setTextSize(blockSize);

        //This is to test if everything work accordingly
       /* canvas.drawText("numberHorizontalPixels = "
                        + numberHorizontalPixel,
                50, blockSize * 3, paint);
        canvas.drawText("numberVerticalPixels = "
                        + numberVerticalPixel,
                50, blockSize * 4, paint);
        canvas.drawText("blockSize = " + blockSize,
                50, blockSize * 5, paint);
        canvas.drawText("gridWidth = " + gridWidth,
                50, blockSize * 6, paint);
        canvas.drawText("gridHeight = " + gridHeight,
                50, blockSize * 7, paint);
        canvas.drawText("horizontalTouched = " +
                        horizontalTouch, 50,
                blockSize * 8, paint);
        canvas.drawText("verticalTouched = " +
                        verticalTouch, 50,
                blockSize * 9, paint);
        canvas.drawText("subHorizontalPosition = " +
                        subHorizontalPosition, 50,
                blockSize * 10, paint);
        canvas.drawText("subVerticalPosition = " +
                        subVerticalPosition, 50,
                blockSize * 11, paint);
        canvas.drawText("hit = " + hit,
                50, blockSize * 12, paint);
        canvas.drawText("shotsTaken = " +
                        shotTaken,
                50, blockSize * 13, paint);
        canvas.drawText("debugging = " + debugging,
                50, blockSize * 14, paint);
*/

        Log.d("numberHorizontalPixels",
                "" + numberHorizontalPixel);
        Log.d("numberVerticalPixels",
                "" + numberVerticalPixel);
        Log.d("blockSize", "" + blockSize);
        Log.d("gridWidth", "" + gridWidth);
        Log.d("gridHeight", "" + gridHeight);
        Log.d("horizontalTouched",
                "" + horizontalTouch);
        Log.d("verticalTouched",
                "" + verticalTouch);
        Log.d("subHorizontalPosition",
                "" + subHorizontalPosition);
        Log.d("subVerticalPosition",
                "" + subVerticalPosition);
        Log.d("hit", "" + hit);
        Log.d("shotsTaken", "" + shotTaken);
        Log.d("debugging", "" + debugging);
        Log.d("distanceFromSub",
                "" + distanceFromSub);
    }
}