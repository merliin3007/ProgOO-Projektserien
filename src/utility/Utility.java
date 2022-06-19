package utility;

import java.util.Random;

public class Utility {
    public static <T> void shuffleArray(T[] array)
    {
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
     * @param precision The maximum difference between both floats
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

    public static boolean intcmp(int a, int b, int precision) {
        return Math.abs(a - b) <= precision;
    }
}
