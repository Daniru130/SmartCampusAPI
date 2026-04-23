/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.smartcampusapi;

/**
 *
 * @author ASUS
 */
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/v1")
public class SmartCampusApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(com.smartcampus.smartcampusapi.resource.DiscoveryResource.class);
        classes.add(com.smartcampus.smartcampusapi.resource.RoomResource.class);
        classes.add(com.smartcampus.smartcampusapi.resource.SensorResource.class);
        classes.add(com.smartcampus.smartcampusapi.exception.RoomNotEmptyExceptionMapper.class);
        classes.add(com.smartcampus.smartcampusapi.exception.LinkedResourceNotFoundExceptionMapper.class);
        classes.add(com.smartcampus.smartcampusapi.exception.SensorUnavailableExceptionMapper.class);
        classes.add(com.smartcampus.smartcampusapi.exception.GlobalExceptionMapper.class);
        classes.add(com.smartcampus.smartcampusapi.filter.LoggingFilter.class);
        return classes;
    }
}
