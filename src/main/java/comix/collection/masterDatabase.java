package comix.collection;
import comix.model.*;
import java.util.HashMap;
import java.util.List;

import comix.search.*;

public class masterDatabase {
    private HashMap<String,Comix> comix;
    private comixSearch comixSearcher; 



    public masterDatabase(){
        this.comix = new HashMap<>();
    }
    public HashMap<String, Comix> getComix() {
        return comix;
    }

    public void setComixSearcher(comixSearch comixSearcher) {
        this.comixSearcher = comixSearcher;
    }

    public List<Comix> doSearch(String term){
        return this.comixSearcher.doSearch(term, this.comix.values());
    }
}
