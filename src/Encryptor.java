import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Encryptor {

    private static int iter = 9; //Кількість ітерацій шифрування дати ліцензії

    //Закодовує ліцензійну дату
    public static String encrypt(String encryptData) {

        String encryptedData = Base64.getEncoder().encodeToString(encryptData.getBytes());
        for (int i = 0; i < iter; i++) {
            encryptedData = Base64.getEncoder().encodeToString(encryptedData.getBytes());
        }
        return encryptedData;
    }

    //Декодує ліцензійну дату
    public static String decrypt(String decryptData) {

        try {
            String decrypted = new String(Base64.getDecoder().decode(decryptData));
            for (int i = 0; i < iter; i++) {
                decrypted = new String(Base64.getDecoder().decode(decrypted));
            }
            return decrypted;
        } catch (Exception e) {
//            e.printStackTrace();
            return dateAndTime();
        }
    }

    //Повертає сьогоднішню дату
    public static String dateAndTime() {
        Calendar c = Calendar.getInstance();
        String curentTime = String.format("%tT", c); //format hh:mm:ss
        //String currentDate = String.format("%ta %td.%tm.%ty", c, c, c, c);  //format Fr dd.MM.yyyy
        String currentDate = String.format("%td.%tm.%tY", c, c, c);  //format dd.MM.yyyy
        return currentDate; //format dd.MM.yyyy
    }

    //Порівнює сьогоднішню дату з датою ліцензії
    public static boolean compareDates(String psDate1, String psDate2){
        SimpleDateFormat dateFormat = new SimpleDateFormat ("dd.MM.yyyy");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateFormat.parse(psDate1);
            date2 = dateFormat.parse(psDate2);
        } catch (ParseException e) {
            //Error of a date
            //e.printStackTrace();
            return false;
        }

        if(date2.after(date1)) {
            return true;
        } else {
            return false;
        }
    }
}
