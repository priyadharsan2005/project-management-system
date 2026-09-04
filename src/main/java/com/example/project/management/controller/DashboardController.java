
package com.example.project.management.controller;

import com.example.project.management.dto.DashboardDTO;
import com.example.project.management.service.DashboardService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    // =========================================================
    // GET DASHBOARD
    // =========================================================

    @GetMapping
    public DashboardDTO getDashboard() {

        return dashboardService.getDashboard();
    }
}

