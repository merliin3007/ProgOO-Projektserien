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
}
