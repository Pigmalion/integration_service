package test.source;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import source.Integration;
import source.IntegrationService;
import source.KsIntegrationsDao;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * IntegrationService Tester.
 *
 * @author <Nathan Borik>
 * @version 1.0
 * @since <pre>Apr 29, 2020</pre>
 */
public class IntegrationServiceTest {
    private IntegrationService classUnderTest;
    private KsIntegrationsDao integrationsDaoMocked;


    @Before
    public void before() throws Exception {
        classUnderTest = new IntegrationService();
        integrationsDaoMocked = mock(KsIntegrationsDao.class);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: insertIntegration(String ksId, String data)
     */
    @Test
    public void testInsertIntegration() throws Exception {

        Exception exception = assertThrows(Exception.class, () -> {
            classUnderTest.insertIntegration("123", "data");
        });

        assertTrue("integration DAO was not provided ",exception.toString().startsWith("java.lang.NullPointerException"));

        classUnderTest.setIntegrationDao(integrationsDaoMocked);

        classUnderTest.insertIntegration("123", "data");
    }

    /**
     * Method: fetchIntegrationByKsId(String ksId)
     */
    @Test
    public void testFetchIntegrationByKsId() throws Exception {
        List<Integration> mockList1 = new ArrayList<>();
        List<Integration> mockList2 = new ArrayList<>();
        classUnderTest.setIntegrationDao(integrationsDaoMocked);
        for(int i =0; i<5; i++ ){
            mockList1.add(new Integration());
            if(i!=0)
                mockList2.add(new Integration());
        }
        //both old  ksId and new ksId exist in the DB
        when(integrationsDaoMocked.fetchByKsId("123")).thenReturn(new ArrayList<Integration>(mockList1));
        when(integrationsDaoMocked.fetchByKsId("123-Normalized")).thenReturn(mockList2);
        List<Integration> resultList = classUnderTest.fetchIntegrationByKsId("123");
        assertEquals(9,resultList.size());

        // old ksId does not exist and new ksId exist in the DB
        when(integrationsDaoMocked.fetchByKsId("123")).thenReturn(new ArrayList<Integration>());
        resultList = classUnderTest.fetchIntegrationByKsId("123");
        assertEquals(4,resultList.size());

        // old ksId does exist and new ksId does not yet exist in the DB
        when(integrationsDaoMocked.fetchByKsId("123")).thenReturn(new ArrayList<Integration>(mockList1));
        when(integrationsDaoMocked.fetchByKsId("123-Normalized")).thenReturn(new ArrayList<Integration>());
        resultList = classUnderTest.fetchIntegrationByKsId("123");
        assertEquals(5,resultList.size());

        // both ids do not exist in db
        when(integrationsDaoMocked.fetchByKsId("123")).thenReturn(new ArrayList<Integration>());
        when(integrationsDaoMocked.fetchByKsId("123-Normalized")).thenReturn(new ArrayList<Integration>());
        resultList = classUnderTest.fetchIntegrationByKsId("123");
        assertEquals(0,resultList.size());

        // ksId is already normalized
        when(integrationsDaoMocked.fetchByKsId("123-Normalized")).thenReturn(new ArrayList<Integration>(mockList1));
        resultList = classUnderTest.fetchIntegrationByKsId("123-Normalized");
        assertEquals(5,resultList.size());

    }

    /**
     * Method: migrate()
     */
    @Test
    public void testMigrate() throws Exception {
        classUnderTest.setIntegrationDao(integrationsDaoMocked);
 int rowsEffected =0;
        ArrayList<Integration> mockDB = createMockList(10,"half-normalized");
        when(integrationsDaoMocked.fetchAll()).thenReturn(mockDB);
        when(integrationsDaoMocked.updateKsId("ksId","ksId-Normalized")).thenReturn(5);
        rowsEffected = classUnderTest.migrate();
        assertEquals("5 Rows should be effected", 5, rowsEffected);

         mockDB = createMockList(5, "not-normalized");
        when(integrationsDaoMocked.fetchAll()).thenReturn(mockDB);
        when(integrationsDaoMocked.updateKsId("ksId0-id","ksId0-id-Normalized")).thenReturn(1);
        when(integrationsDaoMocked.updateKsId("ksId1-id","ksId1-id-Normalized")).thenReturn(1);
        when(integrationsDaoMocked.updateKsId("ksId2-id","ksId2-id-Normalized")).thenReturn(1);
        when(integrationsDaoMocked.updateKsId("ksId3-id","ksId3-id-Normalized")).thenReturn(1);
        when(integrationsDaoMocked.updateKsId("ksId4-id","ksId4-id-Normalized")).thenReturn(1);

        rowsEffected = classUnderTest.migrate();

        assertEquals("5 Rows should be effected", 5, rowsEffected);

        mockDB = createMockList(5, "full-normalized");
        when(integrationsDaoMocked.fetchAll()).thenReturn(mockDB);
        when(integrationsDaoMocked.updateKsId("ksId-Normalized","ksId-Normalized")).thenReturn(5);

        rowsEffected = classUnderTest.migrate();

        assertEquals("5 Rows should be effected", 0, rowsEffected);


    }

    private ArrayList<Integration> createMockList(int length,String type) {
        ArrayList<Integration> list = new ArrayList<>();
        String suffix ="-Normalized";

        for (int i=0; i<length;i++){
            if( (i/2)%2 == 0 && type.equals("half-normalized")) {
                suffix="";
            } else if(type.equals("not-normalized")){
                suffix=i+"-id";
            }
            list.add(new Integration(i,"ksId"+suffix));
        }
        return list;
    }


} 
