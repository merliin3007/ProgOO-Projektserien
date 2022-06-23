package utility;

import java.util.Random;

/**
 * A class with some faaaancy utility functions
 */
public class Console {

    /** U shall not create an instance of this class. */
    private Console() { }

    /**
     * Some weird operating system decided to use 'cls' instead of 'clear'. Shame.
     */
    public final static void clear()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.toLowerCase().contains("windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            // Handle any exceptions.
            // Not now
        }
    }
}