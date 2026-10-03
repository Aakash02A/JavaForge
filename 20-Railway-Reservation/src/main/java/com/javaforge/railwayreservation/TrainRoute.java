package com.javaforge.railwayreservation;

import java.time.LocalTime;

public class TrainRoute {
    private final int trainId;
    private final String trainNumber;
    private final String trainName;
    private final String fromStationCode;
    private final String fromStationName;
    private final LocalTime departureTime;
    private final String toStationCode;
    private final String toStationName;
    private final LocalTime arrivalTime;
    private final int availableSeats;

    public TrainRoute(int trainId, String trainNumber, String trainName,
                      String fromStationCode, String fromStationName, LocalTime departureTime,
                      String toStationCode, String toStationName, LocalTime arrivalTime,
                      int availableSeats) {
        this.trainId = trainId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.fromStationCode = fromStationCode;
        this.fromStationName = fromStationName;
        this.departureTime = departureTime;
        this.toStationCode = toStationCode;
        this.toStationName = toStationName;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
    }

    public int getTrainId() {
        return trainId;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getFromStationCode() {
        return fromStationCode;
    }

    public String getFromStationName() {
        return fromStationName;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public String getToStationCode() {
        return toStationCode;
    }

    public String getToStationName() {
        return toStationName;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }
}
