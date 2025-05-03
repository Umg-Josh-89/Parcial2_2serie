package com.covid.covid19stats.service;

import com.covid.covid19stats.model.Report;
import com.covid.covid19stats.repository.ReportRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

@Service
public class ReportService {
    private static final Logger logger = LogManager.getLogger(ReportService.class);
    private static final String API_URL = "https://covid-19-statistics.p.rapidapi.com/reports";
    
    private final RestTemplate restTemplate;
    private final ReportRepository reportRepository;
    private final String reportDate;


    @Autowired
    public ReportService(RestTemplate restTemplate, 
                        ReportRepository reportRepository,
                        @Value("${covid.report.date}") String reportDate) {
        this.restTemplate = restTemplate;
        this.reportRepository = reportRepository;
        this.reportDate = reportDate;

    }

    @Transactional
    public void fetchAndSaveReports(String iso) {
        LocalDate fecha = LocalDate.parse(reportDate);
        
        if (reportRepository.existsByDateAndIso(fecha, iso)) {
            logger.info("Reporte existente {} on {}", iso, fecha);
            return;
        }

        try {
            JSONArray reportsData = fetchCovidDataFromApi(iso);
            processAndSaveReports(reportsData);
            logger.info("Datos Guardados {} en {}", iso, fecha);
        } catch (Exception e) {
            logger.error("Error fetching data from API", e);
            throw new ReportProcessingException("Failed to fetch and save reports", e);
        }
    }
    public boolean reportAlreadyExists(String iso, LocalDate fecha) {
    return reportRepository.existsByDateAndIso(fecha,iso);
    }


    private JSONArray fetchCovidDataFromApi(String iso) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "cd21a4ae79mshfd69918880cb8acp1dad3djsn376033f678b5");
        headers.set("X-RapidAPI-Host", "covid-19-statistics.p.rapidapi.com");

        String apiUrl = String.format("%s?iso=%s&date=%s", API_URL, iso, reportDate);
        ResponseEntity<String> response = restTemplate.exchange(
            apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        return new JSONObject(response.getBody()).getJSONArray("data");
    }

    private void processAndSaveReports(JSONArray dataArray) {
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject reportJson = dataArray.getJSONObject(i);
            Report report = createReportFromJson(reportJson);
            reportRepository.save(report);
            logger.debug("Saving report: {}", report);
        }
    }

    private Report createReportFromJson(JSONObject reportJson) {
        Report report = new Report();
        report.setIso(reportJson.getJSONObject("region").getString("iso"));
        report.setDate(LocalDate.parse(reportJson.getString("date")));
        report.setConfirmed(reportJson.optInt("confirmed", 0));
        report.setDeaths(reportJson.optInt("deaths", 0));
        report.setRecovered(reportJson.optInt("recovered", 0));
        report.setProvince(determineProvinceName(reportJson));
        return report;
    }

    private String determineProvinceName(JSONObject reportJson) {
        String province = reportJson.optString("province", null);
        
        if (province == null || province.isEmpty() || "UUUU".equals(province)) {
            province = reportJson.getJSONObject("region").optString("province");
            if (province == null || province.isEmpty()) {
                province = reportJson.optString("city", "N/A");
            }
        }
        
        return province;
    }

    public TreeMap<String, Report> getReportsByDateAndIso(LocalDate date, String iso) {
        List<Report> reports = reportRepository.findByDateAndIso(date, iso);
        TreeMap<String, Report> uniqueReports = new TreeMap<>();
        
        reports.forEach(report -> {
            String provinceKey = report.getProvince() != null ? report.getProvince() : "GENERAL";
            uniqueReports.putIfAbsent(provinceKey, report);
        });

        return uniqueReports;
    }

public void exportGroupedReports(LocalDate date, String iso, String filename) {
    TreeMap<String, Report> reports = getReportsByDateAndIso(date, iso);

    System.out.println("Casos por provincia el " + date + " para ISO: " + iso);
    reports.forEach((provincia, report) -> {
        System.out.println("Provincia: " + provincia);
        System.out.println("  Confirmados: " + report.getConfirmed());
        System.out.println("  Muertes: " + report.getDeaths());
        System.out.println("  Recuperados: " + report.getRecovered());
        System.out.println("----------------------------");
    });

}

    public static class ReportProcessingException extends RuntimeException {
        public ReportProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}