package com.example.MovieTicker.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.MovieTicker.entity.HoaDon;
import com.example.MovieTicker.entity.Ve;
import com.example.MovieTicker.request.PaymentRequest;
import com.example.MovieTicker.request.TicketBookingRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.HoaDonService;
import com.example.MovieTicker.service.VeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/ve")
public class VeController {
    @Autowired
    private VeService veService;

    @PostMapping
    public ApiResponse<?> booking(@RequestBody TicketBookingRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) 
        throws IOException
    {
        List<Object> result = veService.createTickets(request, httpRequest, httpResponse);

        return ApiResponse.builder()
                .code(200)
                .message("Đặt vé thành công")
                .data(result)
                .build();
        
    }
    
}
