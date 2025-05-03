package com.covid.covid19stats.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "covid_reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "iso", nullable = false, length = 10)
    private String iso;
    
    @Column(name = "province", length = 100, nullable = false)
    private String province;
    
    public Report() {
        this.province = "N/A";
    }
    
    @Column(name = "date", nullable = true)
    private LocalDate date;
    
    @Column(name = "confirmed", nullable = false)
    private Integer confirmed = 0;
    
    @Column(name = "deaths", nullable = false)
    private Integer deaths = 0;
    
    @Column(name = "recovered", nullable = false)
    private Integer recovered = 0;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        if (province == null || province.trim().isEmpty() || "UUUU".equals(province)) {
            this.province = "Nacional";
        } else {
            this.province = province.length() > 100 ? province.substring(0, 100) : province;
        }
    }



    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }

    public Integer getRecovered() {
        return recovered;
    }

    public void setRecovered(Integer recovered) {
        this.recovered = recovered;
    }

    public void setBothDates(LocalDate date) {
        this.date = date;
    }

    
    @Override
    public String toString() {
        return "Report{" +
               "id=" + id +
               ", iso='" + iso + '\'' +
               ", province='" + province + '\'' +
               ", date=" + date +
               ", confirmed=" + confirmed +
               ", deaths=" + deaths +
               ", recovered=" + recovered +
               '}';
    }
}