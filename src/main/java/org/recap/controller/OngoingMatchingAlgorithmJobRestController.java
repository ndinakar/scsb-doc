package org.recap.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.matchingalgorithm.service.MatchingBibInfoDetailService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.service.OngoingMatchingAlgorithmService;
import org.recap.util.DateUtil;
import org.recap.util.OngoingMatchingAlgorithmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rajeshbabuk on 20/4/17.
 */
@RestController
@RequestMapping("/ongoingMatchingAlgorithmService")
public class OngoingMatchingAlgorithmJobRestController {

    private static final Logger logger = LoggerFactory.getLogger(OngoingMatchingAlgorithmJobRestController.class);

    @Autowired
    private OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Autowired
    private MatchingBibInfoDetailService matchingBibInfoDetailService;

    @Autowired
    DateUtil dateUtil;

    @Value("${" + PropertyKeyConstants.MATCHING_ALGORITHM_BIBINFO_BATCHSIZE + "}")
    private String batchSize;

    @Autowired
    private OngoingMatchingAlgorithmService ongoingMatchingAlgorithmService;

    @PostMapping(value = "/ongoingMatchingAlgorithmJob", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String startMatchingAlgorithmJob(@RequestBody SolrIndexRequest solrIndexRequest) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date date = solrIndexRequest.getCreatedDate();
        solrIndexRequest.setFromDate(new SimpleDateFormat(ScsbConstants.ONGOING_MATCHING_DATE_FORMAT).format(date));
        solrIndexRequest.setMaProcessType(ScsbConstants.ONGOING_MA_BOTH_GROUPING_CGD_PROCESS);
        solrIndexRequest.setMatchBy(ScsbConstants.FROM_DATE);
        solrIndexRequest.setIncludeMaQualifier(true);
        solrIndexRequest.setIndexBibsForOngoingMa(true);
        String status = "";
        Integer rows = Integer.valueOf(batchSize);
        try {
            status = processGroupingForOngoingMatchingAlgorithm(solrIndexRequest, rows);
            status = processCgdUpdatesForOngoingMatchingAlgorithm(solrIndexRequest, rows);
            if (ScsbCommonConstants.SUCCESS.equalsIgnoreCase(status)) {
                status = matchingBibInfoDetailService.populateMatchingBibInfo(dateUtil.getFromDate(date), dateUtil.getToDate(date));
            }
        } catch (Exception e) {
            logger.error("Exception : {0}", e);
        }
        stopWatch.stop();
        logger.info("Total Time taken to complete Ongoing Matching Algorithm : {}", stopWatch.getTotalTimeSeconds());
        return status;
    }

    private String processGroupingForOngoingMatchingAlgorithm(SolrIndexRequest solrIndexRequest, Integer rows) throws Exception {
        StopWatch stopWatchForGrouping = new StopWatch();
        stopWatchForGrouping.start();
        String status = ongoingMatchingAlgorithmUtil.fetchUpdatedRecordsAndStartGroupingProcessBasedOnCriteria(solrIndexRequest, rows);
        stopWatchForGrouping.stop();
        logger.info("{}{}{}", ScsbConstants.TOTAL_TIME_TAKEN, "for Grouping Bibs: ", stopWatchForGrouping.getTotalTimeSeconds());
        return status;
    }

    private String processCgdUpdatesForOngoingMatchingAlgorithm(SolrIndexRequest solrIndexRequest, Integer rows) throws Exception {
        StopWatch stopWatchForCgdProcess = new StopWatch();
        stopWatchForCgdProcess.start();
        String status = ongoingMatchingAlgorithmUtil.fetchUpdatedRecordsAndStartCgdUpdateProcessBasedOnCriteria(solrIndexRequest, rows);
        stopWatchForCgdProcess.stop();
        logger.info("{}{}{}", ScsbConstants.TOTAL_TIME_TAKEN, "for Updating CGD : ", stopWatchForCgdProcess.getTotalTimeSeconds());
        return status;
    }

    @GetMapping("/generateCGDRoundTripReport")
    public String generateCGDRoundTripReport() throws Exception {
        return ongoingMatchingAlgorithmService.generateCGDRoundTripReport();
    }
}
