package com.ly.export.controller;

import com.ly.export.service.ExportService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/export")
public class ExportController {

    @Resource
    private ExportService exportService;

    @RequestMapping("/doExport")
    public void doExport(){
        exportService.doExport();
    }

}
