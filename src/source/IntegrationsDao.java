package source;

import java.util.List;

public interface IntegrationsDao {
    int insert (String ksId,String data);
    int update(int id,String ksId, String data);
    int updateKsId(String oldKsId, String newKsId);
    Integration fetchById(int id);
    List<Integration> fetchByKsId(String ksId);
    List<Integration> fetchAll();

}
