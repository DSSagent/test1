package comix.collection;
import comix.model.*;

import java.util.HashSet;

public class PersonalCollection {

    private HashSet<Comix> comix;

    public PersonalCollection(){
        this.comix = new HashSet<>();
    }

    public HashSet<Comix> getComix() {
        return comix;
    }

}
