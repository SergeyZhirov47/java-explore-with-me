package ru.practicum.request.model;

public enum RequestStatus {
    PENDING,
    CONFIRMED,
    CANCELED, // отменил тот кто, подал заявку
    REJECTED
}
