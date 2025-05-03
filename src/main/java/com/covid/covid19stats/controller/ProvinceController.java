/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.covid.covid19stats.controller;
import com.covid.covid19stats.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
/**
 *
 * @author rodol
 */
@RestController
public class ProvinceController {
    @Autowired
    private ProvinceService provinceService;

    @GetMapping("/provinces/load/{iso}")
    public String loadProvinces(@PathVariable String iso) {
        provinceService.fetchAndSaveProvinces(iso);
        return "Provinces loaded successfully.";
    }
}