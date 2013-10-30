package sthrm2ex;

public class Log {
    public static void log (String message) {
        System.out.println(message);
    }
    
    public static void loge (String message, Exception e) {
        System.out.println(message);
        System.out.println(e);
    }
    public static void printArray(Object [] s) {   
        if (s == null || s.length == 0)   
            System.out.println("Empty array");   
        for (int i = 0; i < s.length; i++)   
            System.out.println(i+"  "+s[i]);   
   }  
}

