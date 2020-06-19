package com.arjit.demo.coronavirustracker.controller;

import com.arjit.demo.coronavirustracker.models.LocationStats;
import com.arjit.demo.coronavirustracker.models.RecoveryStats;
import com.arjit.demo.coronavirustracker.services.CoronaVirusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.LongStream;

@Controller
public class ThymeleafController {

    @Autowired
    private CoronaVirusService coronaVirusService;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> stats = coronaVirusService.getAllStats();

        Long totalReportedCases = stats.stream()
                .mapToLong(stat -> stat.getLatestTotalCases())
                .sum();

        Long totalDeaths = stats.stream()
                .mapToLong(stat -> stat.getLatestTotalDeath())
                .sum();

        List<RecoveryStats> allRecoveryStats = coronaVirusService.getAllRecoveryStats();

        Long totalRecovered = allRecoveryStats.stream()
                .mapToLong(stat -> stat.getLatestTotalRecovery())
                .sum();

        //total confirmed deaths

        //total recovered
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("totalDeaths",totalDeaths);
        model.addAttribute("totalRecovered",totalRecovered);
        model.addAttribute("allStats", stats);
        return "home";
    }

}
