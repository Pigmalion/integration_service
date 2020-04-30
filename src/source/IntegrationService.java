package source;

import javafx.util.Pair;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntegrationService implements IntegrationsService {
    private KsNormalizeClient normalizeClient;
    private KsIntegrationsDao integrationDao;

    public IntegrationService() {
        this.normalizeClient = new KsNormalizeClient();
    }

    public void setIntegrationDao(KsIntegrationsDao integrationDao) {
        this.integrationDao = integrationDao;
    }

    @Override
    public void insertIntegration(String ksId, String data) {

        try {

            String normalizedKsId = normalizeClient.normalize(ksId);
            this.integrationDao.insert(normalizedKsId,data);

        } catch (IOException e) {
            System.out.println("Error in insert");
            e.printStackTrace();
        }

    }

    @Override
    public List<Integration> fetchIntegrationByKsId(String ksId) {
//        I am assuming that the DB might have old and new records,
//        since it IS possible to insert a new record before full DB migration

            List<Integration> list;
            list = integrationDao.fetchByKsId(ksId);

                try {
                    String normalizedKsID = normalizeClient.normalize(ksId);
                    if(!normalizedKsID.equals(ksId)) {

                        list.addAll(integrationDao.fetchByKsId(normalizedKsID));

                        /*
                         * This is optional in case the update will not create a serious regression in
                         * performance. I am assuming that the update is fast and done with proper query and not with a loop.
                         * If update IS heavy, we can store this old ksId in global (within service) array and then update this array in migrate(),
                         * this will minimize the amount of records we receive in fetchAll()
                         */
                        integrationDao.updateKsId(ksId,normalizedKsID);
                    }

                } catch (IOException e) {
                    System.out.println("Error in normalize"+e.toString());
                    e.printStackTrace();
                }

            return list;
    }

    @Override
    public int migrate() {
        int rowsEffected=0;

        List<Integration> fullIntegrationsList = integrationDao.fetchAll(); // it would be better to do it by batches since the data could be too large for the heap
        Set<Pair<String,String>> setOfKsIds= new HashSet<>(); // a set of old ksIds which will prevent unnecessary iterations
        fullIntegrationsList.stream().map(Integration::getKsId).forEach(originalKsId -> {
            try {
                String normalizedKsId = normalizeClient.normalize(originalKsId);
                if (!normalizedKsId.equals(originalKsId)) {
                    setOfKsIds.add(new Pair<>(originalKsId, normalizedKsId));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        for(Pair<String,String> p : setOfKsIds){
            rowsEffected+= integrationDao.updateKsId(p.getKey(),p.getValue());
        }

        return rowsEffected;
    }
}
