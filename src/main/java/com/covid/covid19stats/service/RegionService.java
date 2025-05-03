/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.covid.covid19stats.service;
import com.covid.covid19stats.model.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.covid.covid19stats.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author rodol
 */
@Service
public class RegionService {
    
   private static final String API_URL = "https://covid-19-statistics.p.rapidapi.com/regions";
   private static final Logger logger = LogManager.getLogger(RegionService.class);
   
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RegionRepository regionRepository;

     public void fetchAndSaveRegions() {
        try {
           
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", "cd21a4ae79mshfd69918880cb8acp1dad3djsn376033f678b5");
            headers.set("X-RapidAPI-Host", "covid-19-statistics.p.rapidapi.com");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JSONObject json = new JSONObject(response.getBody());
            JSONArray dataArray = json.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject regionJson = dataArray.getJSONObject(i);

                Region region = new Region();
                region.setName(regionJson.getString("name"));
                region.setIso(regionJson.getString("iso"));
                region.setProvince(regionJson.optString("province", null));

                regionRepository.save(region);
            }

              logger.info("Regiones Guardadas");

        } catch (Exception e) {
            logger.info(" Error consuming regions API: " + e.getMessage());
        }
    }
    
    
    
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    public void saveRegion(Region region) {
        regionRepository.save(region);
    }
}