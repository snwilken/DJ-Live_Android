package com.djlive.djlive;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static com.djlive.djlive.DJLiveActivity.backgroundImages;


/**
 * Fetches image url and updates playlist session background color
 * @author Summer Wilken
 * @see <a href="http://tinyurl.com/jxh7yl6">Formula for finding closest matching RGB value</a>
 */

public class BitmapGeneratorTask extends AsyncTask<String, Void, Bitmap> {
    private final static String TAG = BitmapGeneratorTask.class.getSimpleName();
    private PlaylistSessionActivity caller;

    /**
     * Constructor takes a back reference to the PlaylistSessionActivity
     * @param caller
     */
    BitmapGeneratorTask(PlaylistSessionActivity caller) {
        this.caller = caller;
    }

    protected Bitmap doInBackground(String... imageURLs) {

        try {

            Log.d(TAG, "Print Params" + imageURLs[0]);
            URL url = new URL(imageURLs[0]);
            Bitmap image = android.graphics.BitmapFactory.decodeStream(url
                    .openConnection()
                    .getInputStream());

            Log.d(TAG, "Returned image");

            caller.setBitmap(image);

            return image;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Gets results and sets background colors
     * @param result
     */
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            getDominantColorFromAlbum(result);
        } else {
            Log.d(TAG, "Resets the background image.");
            caller.setBarColors(R.drawable.dj_live_background_header,
                    ContextCompat.getColor(caller,R.color.colorPrimary),
                    ContextCompat.getColor(caller,R.color.colorDarkPrimary));
        }
    }

    /**
     * Returns the dominant color from the
     * @return
     */
    private void getDominantColorFromAlbum(Bitmap result) {
        Palette palette;

        if (result != null) {
            palette = Palette.from(result).generate();
        }

        /**
         * Finds the most dominant color in an image
         * @see <a href="https://goo.gl/WqWmQc">Android Palette Swatch documentation</a>
         */
        Palette.from(result).generate(new Palette.PaletteAsyncListener() {

            public void onGenerated(Palette p) {

                //local variables
                Palette.Swatch vibrantColor = p.getVibrantSwatch();
                Palette.Swatch dominantColor = p.getDominantSwatch();
                int[] colors = null;

                if (vibrantColor != null) {

                    colors = getClosestMatchingBackground(vibrantColor.getRgb());

                    caller.setBarColors(colors[0], colors[1], colors[2]);

                } else if (dominantColor != null) {

                    colors = getClosestMatchingBackground(dominantColor.getRgb());

                    caller.setBarColors(colors[0], colors[1], colors[2]);

                //default
                } else {

                    caller.setBarColors(R.drawable.dj_live_background_header,
                            ContextCompat.getColor(caller,R.color.colorPrimary),
                            ContextCompat.getColor(caller,R.color.colorDarkPrimary));
                }
            }

            /**
             * Finds the closest matching background color and returns appropriate image
             * Use the formula from StackOverflow
             * @see <a href="http://tinyurl.com/jxh7yl6">How to find closest matching color</a>
             * @param dominantColor
             * @return
             */
            public int[] getClosestMatchingBackground(int dominantColor) {
                int red   = Color.red(dominantColor);
                int green = Color.green(dominantColor);
                int blue  = Color.blue(dominantColor);
                double imageDiff = 0;
                double minImageDiff = 100000000; //ridiculously large number
                int[] closestImageIDandRGB = new int[3]; //stores bitmap, primary color, and dark color

                //iterates through all of the rgb values in the set of images to find the
                //closest matching background
                for (Map.Entry<Integer, String[]> entry : backgroundImages.entrySet()) {

                    int hexColor     = Color.parseColor(entry.getValue()[0]); //parses color string
                    int hexColorDark = Color.parseColor(entry.getValue()[1]); //parses color string
                    int redTemp      = Color.red(hexColor);
                    int greenTemp    = Color.green(hexColor);
                    int blueTemp     = Color.blue(hexColor);

                    //returns the difference between album image primary color and background image
                    //primary color
                    imageDiff = Math.sqrt(Math.pow((red - redTemp), 2)
                            + Math.pow((blue - blueTemp), 2)
                            + Math.pow((green - greenTemp), 2));

                    //if image diff is less than min image diff then save as new minimum
                    if (imageDiff < minImageDiff) {

                        minImageDiff = imageDiff; //sets new minimum image diff
                        closestImageIDandRGB[0] = entry.getKey(); //stores drawable id
                        closestImageIDandRGB[1] = hexColor; //stores primary rgb int value
                        closestImageIDandRGB[2] = hexColorDark; //stores dark rgb int value
                    }
                }

                return closestImageIDandRGB; //returns an array with drawable id, primary rgb,
                                             // and dark rgb
            }
        });
    }
}

