package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.recap.BaseTestCaseUT4;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.controller.SolrIndexController;
import org.recap.matchingalgorithm.MatchScoreReport;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.MatchingAlgorithmReportDataDetailsRepository;
import org.recap.repository.jpa.MatchingAlgorithmReportDetailRepository;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Anitha on 10/10/20.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SolrTemplate.class,SolrClient.class})
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
public class MatchingAlgorithmUtilUT extends BaseTestCaseUT4 {

    @InjectMocks
    MatchingAlgorithmUtil mockMatchingAlgorithmUtil;

    @Mock
    private SolrQueryBuilder solrQueryBuilder;

    @Mock
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    MatchingAlgorithmReportDataDetailsRepository matchingAlgorithmReportDataDetailsRepository;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    CommonUtil commonUtil;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    MatchingAlgorithmReportDetailRepository matchingAlgorithmReportDetailRepository;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    SolrIndexController bibItemIndexExecutorService;

    @Mock
    MatchingAlgorithmReportDataEntity matchingAlgorithmReportDataEntity;

    @Mock
    SolrQuery solrQuery;

    @Mock
    Collection<BibliographicEntity> bibliographicEntities;

    @Mock
    EntityManager entityManager;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(mockMatchingAlgorithmUtil,"matchingHeaderValueLength",8000);
        ReflectionTestUtils.setField(commonUtil,"institutionDetailsRepository",institutionDetailsRepository);
    }

    @Test
    public void updateBibForMatchingIdentifier() throws Exception {
        List<Integer> bibIdList=new ArrayList<>();
        bibIdList.add(1);
        List<BibliographicEntity> bibliographicEntityList=new ArrayList<>();
        bibliographicEntityList.add(bibliographicEntity);
        Mockito.when(bibliographicEntity.getMatchingIdentity()).thenReturn("");
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntityList);
        Optional<Set<Integer>> id= mockMatchingAlgorithmUtil.updateBibForMatchingIdentifier(bibIdList);
        assertNotNull(id);
    }

    @Test
    public void updateBibsForMatchingIdentifier() throws Exception {
        List<Integer> bibIdList=new ArrayList<>();
        bibIdList.add(1);
        List<BibliographicEntity> bibliographicEntityList=new ArrayList<>();
        bibliographicEntityList.add(bibliographicEntity);
        Mockito.when(bibliographicEntity.getMatchingIdentity()).thenReturn("");
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntityList);
        Optional<Map<Integer,BibliographicEntity>> id= mockMatchingAlgorithmUtil.updateBibsForMatchingIdentifier(bibliographicEntityList,1);
        assertNotNull(id);
    }

    @Test
    public void getReportDataEntity() throws Exception {
        List<MatchingAlgorithmReportDataEntity> reportDataEntities=new ArrayList<>();
        reportDataEntities.add(matchingAlgorithmReportDataEntity);
        String headerValues = new String(new char[10005]);
        mockMatchingAlgorithmUtil.getReportDataEntity("headerName",headerValues,reportDataEntities);
        mockMatchingAlgorithmUtil.getTitleToMatch("an an an an an");
    }

    @Test
    public void getBibIdAndBibEntityMap() throws Exception {
        Set<String> bibIdsList=new HashSet<>();
        bibIdsList.add("1");
        List<BibliographicEntity> bibliographicEntityList=new ArrayList<>();
        bibliographicEntityList.add(bibliographicEntity);
        Mockito.when(bibliographicEntity.getId()).thenReturn(1);
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntityList);
        mockMatchingAlgorithmUtil.getBibIdAndBibEntityMap(bibIdsList);
    }

    @Test
    public void groupCGDForExistingEntries() throws Exception {
        Map<Boolean,List<BibliographicEntity>> partionedByMatchingIdentity=new HashMap<>();
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        partionedByMatchingIdentity.put(false,bibliographicEntities);
        ReflectionTestUtils.invokeMethod(mockMatchingAlgorithmUtil,"groupCGDForExistingEntries",1,partionedByMatchingIdentity);
    }

    @Test
    public void extractBibIdsFromReportDataEntities() throws Exception {
        List<ReportDataEntity> reportDataEntities=new ArrayList<>();
        Set<String> result=mockMatchingAlgorithmUtil.extractBibIdsFromReportDataEntities(reportDataEntities);
        assertNotNull(result);
    }

    @Test
    public void extractBibIdsFromMatchScoreReports() throws Exception {
        List<MatchScoreReport> reportDataEntities=new ArrayList<>();
        Set<Integer> result=mockMatchingAlgorithmUtil.extractBibIdsFromMatchScoreReports(reportDataEntities);
        assertNotNull(result);
    }

    @Test
    public void getSingleMatchBibsAndSaveReport() throws Exception {
        Map<String, Set<Integer>> criteriaMap = getStringSetMap();
        Set<String> criteriaValueSet =new HashSet<>();
        String[] criteriaValueList={"1","2","3"};
        Map<Integer, MatchingBibEntity> bibEntityMap=getIntegerMatchingBibEntityMap();
        StringBuilder matchPointValue=new StringBuilder();
        String[] matchpoints={ScsbCommonConstants.MATCH_POINT_FIELD_OCLC,ScsbCommonConstants.MATCH_POINT_FIELD_ISSN,ScsbCommonConstants.MATCH_POINT_FIELD_LCCN,""};
        for (String re:matchpoints){
            Set<Integer> getBibIdsForCriteriaValue=mockMatchingAlgorithmUtil.getBibIdsForCriteriaValue(criteriaMap,criteriaValueSet,re,re,criteriaValueList,bibEntityMap,matchPointValue);
        }
        List<Integer> bibIds = Arrays.asList(4,5,6);
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        matchingBibEntities.addAll(Arrays.asList(getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,1,"PUL","Middleware for SCSB"),getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,2,"CUL","Middleware for SCSB"),getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,3,"NYPL","Middleware for SCSB")));
        Mockito.when(matchingBibDetailsRepository.getSingleMatchBibIdsBasedOnMatching(Mockito.anyString())).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getBibEntityBasedOnBibIds(Mockito.anyList())).thenReturn(matchingBibEntities);
        Map countsMap= mockMatchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(1,ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, getStringIntegerMap(),1);
        assertNotNull(countsMap);
    }

    @Test
    public void getMatchingMatchPointsEntity() throws Exception {
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        SolrClient solrClient=PowerMockito.mock(SolrClient.class);
        ReflectionTestUtils.setField(mockMatchingAlgorithmUtil,"solrTemplate",mocksolrTemplate1);
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        QueryResponse queryResponse= Mockito.mock(QueryResponse.class);

        List<FacetField> facetFields=new ArrayList<>();
        FacetField facetField=new FacetField(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN);
        facetField.add(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,93930);
        FacetField facetField1=new FacetField(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN);
        facetField.add(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,93931);
        facetFields.add(facetField);
        facetFields.add(facetField1);
        Mockito.when(queryResponse.getFacetFields()).thenReturn(facetFields);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        List<MatchingMatchPointsEntity> countsMap= mockMatchingAlgorithmUtil.getMatchingMatchPointsEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN);
        assertNotNull(countsMap);
    }

    @Test
    public void updateMonographicSetRecords() {
        List<Integer> nonMonographRecordNums=Arrays.asList(1,2,3);
        List<MatchingAlgorithmReportDataEntity> reportDataEntitiesToUpdate=new ArrayList<>();
        MatchingAlgorithmReportDataEntity reportDataEntity = new MatchingAlgorithmReportDataEntity();
        reportDataEntity.setHeaderName("Test");
        reportDataEntity.setHeaderValue("Test");
        reportDataEntitiesToUpdate.add(reportDataEntity);
        List<MatchingMatchPointsEntity> matchingMatchPointsEntities=new ArrayList<>();
        MatchingMatchPointsEntity matchingMatchPointsEntity=new MatchingMatchPointsEntity();
        matchingMatchPointsEntity.setCriteriaValue("test");
        matchingMatchPointsEntities.add(matchingMatchPointsEntity);
        List<MatchingAlgorithmReportEntity> reportEntities=new ArrayList<>();
        MatchingAlgorithmReportEntity reportEntity=new MatchingAlgorithmReportEntity();
        reportEntities.add(reportEntity);
        Collection headerValues=new ArrayList();
        headerValues.add("test");
        ReflectionTestUtils.setField(mockMatchingAlgorithmUtil,"matchingHeaderValueLength",3);
        MatchingAlgorithmReportDataEntity reportDataEntityEmpty=mockMatchingAlgorithmUtil.getReportDataEntityForCollectionValues(Arrays.asList(""),"test");
        MatchingAlgorithmReportDataEntity reportDataEntity1=mockMatchingAlgorithmUtil.getReportDataEntityForCollectionValues(headerValues,"test");
        Mockito.when(matchingAlgorithmReportDetailRepository.findByIdIn(Mockito.anyList())).thenReturn(reportEntities);
        Mockito.when(matchingAlgorithmReportDataDetailsRepository.getReportDataEntityByRecordNumIn(Mockito.anyList(),Mockito.anyString())).thenReturn(reportDataEntitiesToUpdate);
        mockMatchingAlgorithmUtil.updateMonographicSetRecords(nonMonographRecordNums,1);
        mockMatchingAlgorithmUtil.saveMatchingMatchPointEntities(matchingMatchPointsEntities);
        mockMatchingAlgorithmUtil.updateExceptionRecords(Arrays.asList(1,2,3),1);
        assertNotNull(reportDataEntityEmpty);
        assertNotNull(reportDataEntity1);
    }

    @Test
    public void getbibIdAndBibMap() throws Exception {
        Set<Integer> bibIdsList=new HashSet<>();
        Map<Integer, BibliographicEntity> bibliographicEntityMap=mockMatchingAlgorithmUtil.getbibIdAndBibMap(bibIdsList);
        assertNotNull(bibliographicEntityMap);
    }

    @Test
    public void saveGroupedBibsToDb() throws Exception {
        mockMatchingAlgorithmUtil.saveGroupedBibsToDb(bibliographicEntities);
    }

    @Test
    public void getBibIdsToRemoveMatchingIdsInSolr() throws Exception {
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        ReflectionTestUtils.setField(mockMatchingAlgorithmUtil, "solrTemplate", mocksolrTemplate1);
        SolrClient solrClient = PowerMockito.mock(SolrClient.class);
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        QueryResponse queryResponse = Mockito.mock(QueryResponse.class);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        SolrDocumentList solrDocumentList = getSolrDocuments();
        solrDocumentList.setNumFound(1);
        Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
        Mockito.when(solrQueryBuilder.solrQueryToFetchMatchedRecords()).thenReturn(solrQuery);
        Set<Integer> bibIdsList=mockMatchingAlgorithmUtil.getBibIdsToRemoveMatchingIdsInSolr();
        assertNotNull(bibIdsList);
    }

    @Test
    public void populateAndSaveReportEntity() throws Exception {
        List<Integer> bibIds = Arrays.asList(1,2,3);
        Set<Integer> bibIdSet = new HashSet<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = getIntegerMatchingBibEntityMap();
        Map countsMap= mockMatchingAlgorithmUtil.populateAndSaveReportEntity(bibIdSet,matchingBibEntityMap, ScsbCommonConstants.OCLC_CRITERIA, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,
               "2939384", "883939",getStringIntegerMap(),1);
        assertNotNull(countsMap);
    }

   @Test
    public void populateMatchingCounter() throws Exception {
        Mockito.when(solrQueryBuilder.buildSolrQueryForCGDReports(Mockito.anyString(),Mockito.anyString())).thenReturn(new SolrQuery());
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        ReflectionTestUtils.setField(mockMatchingAlgorithmUtil, "solrTemplate", mocksolrTemplate1);
        SolrClient solrClient = PowerMockito.mock(SolrClient.class);
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        QueryResponse queryResponse = Mockito.mock(QueryResponse.class);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        List<String> allInstitutionCodeExceptSupportInstitution=Arrays.asList(ScsbCommonConstants.COLUMBIA,ScsbCommonConstants.PRINCETON,ScsbCommonConstants.NYPL);
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(allInstitutionCodeExceptSupportInstitution);
        SolrDocumentList solrDocumentList = getSolrDocuments();
        solrDocumentList.setNumFound(1);
        Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
        mockMatchingAlgorithmUtil.populateMatchingCounter();
        mockMatchingAlgorithmUtil.saveCGDUpdatedSummaryReport("test");
        assertNotNull(solrDocumentList);
    }

    @Test
    public void processPendingMatchingBibs() throws Exception {
        String[] matchpoints={ScsbCommonConstants.MATCH_POINT_FIELD_OCLC,ScsbCommonConstants.MATCH_POINT_FIELD_ISSN,ScsbCommonConstants.MATCH_POINT_FIELD_LCCN,ScsbCommonConstants.MATCH_POINT_FIELD_ISBN};
        for (String re:matchpoints) {
            SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
            SolrClient solrClient = PowerMockito.mock(SolrClient.class);
            QueryResponse queryResponse = Mockito.mock(QueryResponse.class);
            SolrDocumentList solrDocumentList = getSolrDocuments();
            List<MatchingBibEntity> bibEntities = new ArrayList<>();
            bibEntities.addAll(Arrays.asList(getMatchingBibEntity(re, 1, "PUL", "Middleware for 1"), getMatchingBibEntity(re, 2, "CUL", "Middleware for 2"), getMatchingBibEntity(re, 3, "NYPL", "Middleware for 3")));
            ReflectionTestUtils.setField(mockMatchingAlgorithmUtil, "solrTemplate", mocksolrTemplate1);
            PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
            Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
            Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
            Mockito.when(solrQueryBuilder.solrQueryForOngoingMatching(re, Arrays.asList("129393"))).thenReturn("test");
            Mockito.when(matchingBibDetailsRepository.findByMatchingAndBibIdIn(Mockito.anyString(), Mockito.anyList())).thenReturn(bibEntities);
            List<Integer> bibIds = Arrays.asList(4, 5, 6);
            Set<Integer> bibIdSet = new HashSet<>();
            bibIdSet.addAll(bibIds);
            List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
            matchingBibEntities.addAll(Arrays.asList(getMatchingBibEntity(re, 1, "PUL", "Middleware for SCSB"), getMatchingBibEntity(re, 2, "CUL", "Middleware for ReCAP"), getMatchingBibEntity(re, 3, "NYPL", "Middleware for ReCAP")));
            Map countsMap = mockMatchingAlgorithmUtil.processPendingMatchingBibs(matchingBibEntities, bibIdSet, getStringIntegerMap());
            assertNotNull(countsMap);
        }
    }

    private Map<String, Integer> getStringIntegerMap() {
        Map<String, Integer> matchingAlgoMap = new HashMap<>();
        matchingAlgoMap.put("pulMatchingCount", 1);
        matchingAlgoMap.put("culMatchingCount", 2);
        matchingAlgoMap.put("nyplMatchingCount", 3);
        return matchingAlgoMap;
    }

    private SolrDocumentList getSolrDocuments() {
        SolrDocumentList solrDocumentList =new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField(ScsbCommonConstants.BIB_ID,Integer.valueOf(1));
        SolrDocument solrDocument1 = new SolrDocument();
        solrDocument1.setField(ScsbCommonConstants.BIB_ID,Integer.valueOf(2));
        SolrDocument solrDocument2 = new SolrDocument();
        solrDocument2.setField(ScsbCommonConstants.BIB_ID,Integer.valueOf(3));
        solrDocumentList.add(0,solrDocument);
        solrDocumentList.add(1,solrDocument1);
        solrDocumentList.add(2,solrDocument2);
        solrDocumentList.setNumFound(4);
        return solrDocumentList;
    }

    private Map<Integer, MatchingBibEntity> getIntegerMatchingBibEntityMap() {
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        matchingBibEntityMap.put(1, getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,1,"PUL","Middleware for SCSB"));
        matchingBibEntityMap.put(2, getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,2,"CUL","Middleware for SCSB"));
        matchingBibEntityMap.put(3, getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,3,"NYPL","Middleware for SCSB"));
        return matchingBibEntityMap;
    }

    private Map<String, Set<Integer>> getStringSetMap() {
        Set<Integer> criteria=new HashSet<>();
        criteria.add(1);
        criteria.add(2);
        criteria.add(3);
        Map<String, Set<Integer>> criteriaMap=new HashMap<>();
        criteriaMap.put("1",criteria);
        criteriaMap.put("2",criteria);
        criteriaMap.put("3",criteria);
        return criteriaMap;
    }

    private MatchingBibEntity getMatchingBibEntity(String matching,Integer bib,String inst,String title) {
        MatchingBibEntity matchingBibEntity=new MatchingBibEntity();
        matchingBibEntity.setMatching(matching);
        matchingBibEntity.setBibId(bib);
        matchingBibEntity.setId(10);
        matchingBibEntity.setOwningInstitution(inst);
        matchingBibEntity.setOwningInstBibId("N1029");
        matchingBibEntity.setTitle(title);
        matchingBibEntity.setOclc("129393");
        matchingBibEntity.setIsbn("93930");
        matchingBibEntity.setIssn("12283");
        matchingBibEntity.setLccn("039329");
        matchingBibEntity.setMaterialType("monograph");
        matchingBibEntity.setRoot("31");
        matchingBibEntity.setStatus(ScsbConstants.PENDING);
        return matchingBibEntity;
    }


}
