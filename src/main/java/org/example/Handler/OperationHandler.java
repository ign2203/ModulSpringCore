package org.example.Handler;


import org.example.OperationType;

public  interface OperationHandler {
    void handle();
    OperationType getOperationType();
}
