package com.covid.covid19stats.config;

import com.covid.covid19stats.model.ExecutedReport;
import com.covid.covid19stats.model.Report;
import com.covid.covid19stats.repository.ReportRepository;
import com.covid.covid19stats.repository.ExecutedReportRepository;
import com.covid.covid19stats.service.RegionService;
import com.covid.covid19stats.service.ProvinceService;
import com.covid.covid19stats.service.ReportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.TreeMap;

@Component
public class AutomaticThread {

    private static final Logger logger = LogManager.getLogger(AutomaticThread.class);

    @Autowired
    private RegionService regionService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ExecutedReportRepository executedReportRepository;

    @Value("${covid19stats.default-iso}")
    private String defaultIso;

    @Value("${covid.report.date}")
    private String reportDateString;

 
private static boolean alreadyRun = false;
@Scheduled(initialDelay = 15000, fixedDelay = Long.MAX_VALUE)
public void autoRunCovidDataLoad() {
    LocalDate reportDate = LocalDate.parse(reportDateString);

    if (executedReportRepository.findByExecutionDateAndCountryIso(reportDate, defaultIso).isPresent()) {
        logger.info("EXITO {} en {}", defaultIso, reportDate);
        return;
    }

    try {
        regionService.fetchAndSaveRegions();
        provinceService.fetchAndSaveProvinces(defaultIso);
        reportService.fetchAndSaveReports(defaultIso);

        // NUEVO: Imprimir los casos por provincia en consola
        reportService.exportGroupedReports(reportDate, defaultIso, "reporte_provincias.txt");

        ExecutedReport executed = new ExecutedReport();
        executed.setExecutionDate(reportDate);
        executed.setCountryIso(defaultIso);
        executed.setSuccess(true);
        executedReportRepository.save(executed);

        logger.info("REPORTE PREVIAMENTE GENERADO");
    } catch (Exception e) {
        logger.error("Error durante la ejecución para {}: {}", defaultIso, e.getMessage());

        ExecutedReport executed = new ExecutedReport();
        executed.setExecutionDate(reportDate);
        executed.setCountryIso(defaultIso);
        executed.setSuccess(false);
        executedReportRepository.save(executed);
    }
}

}
