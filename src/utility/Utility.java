package utility;

import java.util.Random;

public class Utility {
    public static void shuffleArray(byte[][] array)
    {
        int index;
        byte[] temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}
