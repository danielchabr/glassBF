package com.bibliofair.glass;

/**
 * Created by damell on 23/03/14.
 */
public class ISBNConvertor {

    private static String CheckDigits = new String("0123456789X0");

    ///////////////////////////////// Main /////////////////////
    public static void main(String[] args) {
        String ISBN = new String();
        if (args.length==1) {
            ISBN = args[0];
            if (ISBN.length()==10)
                System.out.println(ISBN1013(ISBN));
            else if (ISBN.length()==13)
                System.out.println(ISBN1310(ISBN));
            else ISBN = "Invalid ISBN";
        }
    }

    /////////////// Change a character to its integer value ///////
    static int CharToInt(char a) {
        return Character.getNumericValue(a);
    }

    ////////////////////// Convert ISBN-13 to ISBN-10 //////
    public static String ISBN1310(String ISBN) {
        String s9;
        int i, n, v;
        boolean ErrorOccurred;
        ErrorOccurred = false;
        s9 = ISBN.substring(3, 12);
        n = 0;
        for (i=0; i<9; i++) {
            if (!ErrorOccurred) {
                v = CharToInt(s9.charAt(i));
                if (v==-1) ErrorOccurred = true;
                else n = n + (10 - i) * v;
            }
        }
        if (ErrorOccurred) return "ERROR";
        else {
            n = 11 - (n % 11);
            return s9 + CheckDigits.substring(n, n+1);
        }
    }

    ////////////////////// Convert ISBN-10 to ISBN-13 //////
    public static String ISBN1013(String ISBN) {
        String s12;
        int i, n, v;
        boolean ErrorOccurred;
        ErrorOccurred = false;
        s12 = "978" + ISBN.substring(0, 9);
        n = 0;
        for (i=0; i<12; i++) {
            if (!ErrorOccurred) {
                v = CharToInt(s12.charAt(i));
                if (v==-1) ErrorOccurred = true;
                else {
                    if ((i % 2)==0) n = n + v;
                    else n = n + 3*v;
                }
            }
        }
        if (ErrorOccurred) return "ERROR";
        else {
            n = n % 10;
            if (n!=0) n = 10 - n;
            return s12 + CheckDigits.substring(n, n+1);
        }
    }
}