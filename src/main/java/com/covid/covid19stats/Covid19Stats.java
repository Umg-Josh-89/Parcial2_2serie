package com.covid.covid19stats;

import com.covid.covid19stats.model.Report;
import com.covid.covid19stats.service.ReportService;
import java.time.LocalDate;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
public class Covid19Stats {
    public static void main(String[] args) {
        SpringApplication.run(Covid19Stats.class, args);
    }
}

@Component
class Covid19Runner implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(Covid19Runner.class);

    @Value("${covid19stats.default-iso}")
    private String defaultIso;

    @Value("${covid.report.date}")
    private String reportDateString;

    private final ReportService reportService;

    public Covid19Runner(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void run(String... args) throws Exception {
        LocalDate fecha = LocalDate.parse(reportDateString);
        

        if (reportService.reportAlreadyExists(defaultIso, fecha)) {
            logger.info("CONSULTA EXISTENTE, SE CANCELA EJECUCION DE HILO.", defaultIso, fecha);
            return;
        }
        
        logger.info("\n REPORTE COVID 19\n", defaultIso);
        
        try {

            TreeMap<String, Report> groupedReports = reportService.getReportsByDateAndIso(fecha, defaultIso);
            

  
            
        } catch (Exception e) {
        logger.error("\n ERROR EJECUTANDO EL HILO\n", e);
        throw new RuntimeException("ERROR EN EL HILO, CONTACTE A SOPORTE", e);
    }
    }
}