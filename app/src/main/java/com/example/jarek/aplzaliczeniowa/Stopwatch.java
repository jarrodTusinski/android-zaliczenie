package com.example.jarek.aplzaliczeniowa;

class Stopwatch {

    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;


    void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }


    void stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }


    // elapsed time in milliseconds
    public long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        }
        return stopTime - startTime;
    }


    // elapsed time in seconds
    public long getElapsedTimeSecs() {
        if (running) {
            return ((System.currentTimeMillis() - startTime) / 1000);
        }
        return ((stopTime - startTime) / 1000);
    }

}
