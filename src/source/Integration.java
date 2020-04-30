package source;

import java.util.concurrent.ThreadLocalRandom;

public class Integration {
    private int id;
    private String ksId;

    public Integration(){
        id= ThreadLocalRandom.current().nextInt(0,1000);
    }
    public Integration(int _id, String _ksId){
        this.id = _id;
        this.ksId = _ksId;
    }

    public String getKsId() {
        return ksId;
    }

    public int getId() {
        return id;
    }
}
