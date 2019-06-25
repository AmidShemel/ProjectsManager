import java.io.*;

import static javafx.application.Platform.exit;

public class SaveData {

    public static String licLimit;

    // Серіалізує дані
    public static void serialize() throws Exception{
        ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(new File("lic.dll")));

        objOut.writeObject(licLimit);
        objOut.close();
    }

    //Десеріалізує дані
    public static void deSerialize(){
        //String deSerData = Encryptor.encrypt(Encryptor.dateAndTime());
        try {
            ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(new File("lic.dll")));
            licLimit = (String) objIn.readObject();
            objIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        checkLicence();
    }

    private static void checkLicence(){
        if(Encryptor.compareDates(Encryptor.dateAndTime(), Encryptor.decrypt(SaveData.licLimit))){
            System.out.println("License OK");
        } else {
            System.out.println("License Error");
            if(Dialog.confirmBox("Ліцензія продукту закінчилась\nБажаєте ввести ліцензійний ключ?", "Помилка ліцензії")){
                Dialog.largeInput();
                Dialog.infoBox("Перезапустіть програму", "Restart");
                exit();
               // System.exit(0);
            } else {
                System.out.println("quit");
                System.exit(-1);
            }
        }
    }

}
