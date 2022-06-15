package utility;

public class Console {
    private Console() {}

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
            //  Handle any exceptions.
            // Not now
        }
    }
}