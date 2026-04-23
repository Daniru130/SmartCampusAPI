/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.smartcampusapi.resource;

/**
 *
 * @author ASUS
 */
import com.smartcampus.smartcampusapi.exception.SensorUnavailableException;
import com.smartcampus.smartcampusapi.model.Sensor;
import com.smartcampus.smartcampusapi.model.SensorReading;
import com.smartcampus.smartcampusapi.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Sensor not found\"}")
                    .build();
        }
        List<SensorReading> list = store.getReadings().getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(list).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Sensor not found\"}")
                    .build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId);
        }
        SensorReading newReading = new SensorReading(reading.getValue());
        store.getReadings().computeIfAbsent(sensorId, k -> new ArrayList<>()).add(newReading);
        sensor.setCurrentValue(reading.getValue());
        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }
}
