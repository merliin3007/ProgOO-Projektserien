package utility;

import java.util.ArrayList;
import java.util.Random;

public class Utility {
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_GRAPHICS = false;

    /**
     * Randomizes the order of a given array of any type by mutating the array.
     * 
     * @param <T> The Datatype the array contains
     * @param array The array to shuffle
     */
    public static <T> void shuffleArray(T[] array) {
        int index;
        T temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Compares whether two floats differ less than the given precision
     * @param a The first float to compare
     * @param b The second float to compare
     * @param precision The maximum difference between values "a" and "b" to be treated as equal
     * @return true in case both floats differ less than the given precision, false if not
     */
    public static boolean floatcmp(float a, float b, float precision) {
        return Math.abs(a - b) <= precision;
    }
    /**
     * Compares whether two floats differ less than 0.0001
     * @param a The first float to compare
     * @param b The second float to compare
     * @return true in case both floats differ less than 0.0001, false if not
     */
    public static boolean floatcmp(float a, float b) {
        return Utility.floatcmp(a, b, 0.0001f);
    }
    /**
     * Compares whether two ints differ less than the value provided by param "precision"
     * @param a The first int to compare
     * @param b The second int to compare
     * @param precision The maximum difference between values "a" and "b" to be treated as equal
     * @return true in case both int differ less than the precision, false if not
     */
    public static boolean intcmp(int a, int b, int precision) {
        return Math.abs(a - b) <= precision;
    }

    /**
     * Use this to win in the lottery all the time.
     */
    public static ArrayList<Point2f> killRAM() {
        Random rnd = new Random();
        ArrayList<Point2f> ram = new ArrayList<Point2f>();
        for (long i = 0; i < 314159265l; ++i) {
            ram.add(new Point2f(rnd.nextFloat(), rnd.nextFloat()));
        }
        return ram;
    }
}
