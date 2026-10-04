package comix.collection;
import comix.model.*;
import java.util.HashMap;

public class masterDatabase {
    private HashMap<String,Comix> comix;


    public masterDatabase(){
        this.comix = new HashMap<>();
    }
    public HashMap<String, Comix> getComix() {
        return comix;
    }
}
