package com.covid.covid19stats.service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.covid.covid19stats.model.Province;
import com.covid.covid19stats.repository.ProvinceRepository;
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
public class ProvinceService {

    private static final String API_URL = "https://covid-19-statistics.p.rapidapi.com/provinces";
    private static final Logger logger = LogManager.getLogger(ProvinceService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvinceRepository provinceRepository;

    public void fetchAndSaveProvinces(String iso) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", "cd21a4ae79mshfd69918880cb8acp1dad3djsn376033f678b5");
            headers.set("X-RapidAPI-Host", "covid-19-statistics.p.rapidapi.com");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String finalUrl = API_URL + "?iso=" + iso;

            ResponseEntity<String> response = restTemplate.exchange(
                    finalUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JSONObject json = new JSONObject(response.getBody());
            JSONArray dataArray = json.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject provinceJson = dataArray.getJSONObject(i);

                String provinceName = provinceJson.optString("province", "").trim();
                
       
                if (provinceName.isEmpty()) {
                    provinceName = "Guatemala";
                }

                Province province = new Province();
                province.setName(provinceName);
                province.setIso(iso);

                provinceRepository.save(province);
            }

            logger.info("Provincias Guardadas");


        } catch (Exception e) {
            logger.info("Error consuming provinces API: " + e.getMessage());

            e.printStackTrace();
        }
    }

    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    public void saveProvince(Province province) {
        provinceRepository.save(province);
    }
}
