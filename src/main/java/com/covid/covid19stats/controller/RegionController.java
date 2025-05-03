
package com.covid.covid19stats.controller;
import  com.covid.covid19stats.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author rodol
 */

@RestController
public class RegionController {
            @Autowired
    private RegionService regionService;

    @GetMapping("/regions/load")
    public String loadRegions() {
        regionService.fetchAndSaveRegions();
        return "Regions loaded successfully.";
    }
}