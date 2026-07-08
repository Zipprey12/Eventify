package ru.zipprey.eventify.event.exception;

public class CapacityReductionException extends RuntimeException {

    public CapacityReductionException(int booked) {
        super("Нельзя уменьшить количество мест ниже уже забронированных: " + booked);
    }
}
