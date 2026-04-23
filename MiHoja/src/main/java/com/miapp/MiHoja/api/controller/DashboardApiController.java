package com.miapp.MiHoja.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miapp.MiHoja.api.dto.DashboardActionResponse;
import com.miapp.MiHoja.api.dto.DashboardCleanupResponse;
import com.miapp.MiHoja.api.dto.DashboardDeleteRequest;
import com.miapp.MiHoja.api.dto.DashboardOverviewResponse;
import com.miapp.MiHoja.api.dto.DashboardPeopleResponse;
import com.miapp.MiHoja.api.service.DashboardApiService;
import com.miapp.MiHoja.api.service.DashboardPeopleQuery;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DashboardApiService dashboardApiService;

    public DashboardApiController(DashboardApiService dashboardApiService) {
        this.dashboardApiService = dashboardApiService;
    }

    @GetMapping("/overview")
    public DashboardOverviewResponse overview() {
        return dashboardApiService.getOverview();
    }

    @GetMapping("/people")
    public DashboardPeopleResponse people(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(required = false) List<String> sexo,
            @RequestParam(required = false) List<String> lugarExpedicion,
            @RequestParam(required = false) List<String> formacion,
            @RequestParam(required = false) List<String> dependencia,
            @RequestParam(required = false) List<String> cargo,
            @RequestParam(defaultValue = "name-asc") String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return dashboardApiService.getPeople(new DashboardPeopleQuery(
                query,
                sexo,
                lugarExpedicion,
                formacion,
                dependencia,
                cargo,
                sortBy,
                page,
                size
        ));
    }

    @DeleteMapping("/people")
    public DashboardActionResponse deletePeople(@RequestBody DashboardDeleteRequest request) {
        return dashboardApiService.deletePeople(request.ids());
    }

    @PostMapping("/maintenance/cleanup-duplicates")
    public DashboardCleanupResponse cleanupDuplicates() {
        return dashboardApiService.cleanupDuplicates();
    }
}
