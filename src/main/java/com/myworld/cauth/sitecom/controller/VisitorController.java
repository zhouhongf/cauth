package com.myworld.cauth.sitecom.controller;

import com.myworld.cauth.sitecom.service.VisitorService;
import com.myworld.cauth.common.model.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/visitor")
public class VisitorController {

    private VisitorService visitorService;

    @Autowired
    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }


    @GetMapping("/updateVisitor")
    public ApiResult updateVisitor(HttpServletRequest request, HttpServletResponse response) {
        return visitorService.updateVisitor(request, response);
    }

    @PostMapping("/updateVisitorMobile")
    public ApiResult updateVisitorMobile(@RequestBody String personStr, HttpServletRequest request, HttpServletResponse response) {
        return visitorService.updateVisitorMobile(personStr, request, response);
    }

    @GetMapping("/visitorList")
    public ApiResult getVisitorList(@RequestParam Integer pageSize, @RequestParam Integer pageIndex) {
        return visitorService.getVisitorList(pageSize, pageIndex);
    }

}
