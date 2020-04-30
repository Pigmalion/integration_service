package source;

import java.util.List;

public class KsIntegrationsDao implements IntegrationsDao {
    @Override
    public int insert(String ksId, String data) {
        return 0;
    }

    @Override
    public int update(int id, String ksId, String data) {
        return 0;
    }

    @Override
    public int updateKsId(String oldKsId, String newKsId) {
        return 0;
    }

    @Override
    public Integration fetchById(int id) {
        return null;
    }

    @Override
    public List<Integration> fetchByKsId(String ksId) {
        return null;
    }

    @Override
    public List<Integration> fetchAll() {
        return null;
    }
}
