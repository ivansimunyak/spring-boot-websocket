package com.example.messagingstompwebsocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class StopwatchController {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicReference<LocalTime> startTime = new AtomicReference<>();

    @MessageMapping("/start")
    @SendTo("/topic/start")
    public synchronized TimeMessage start() {
        if (!running.getAndSet(true)) {
            startTime.set(LocalTime.now());
        }
        LocalTime time = startTime.get();
        return new TimeMessage(time.getHour(), time.getMinute(), time.getSecond(), running.get());
    }

    @MessageMapping("/stop")
    @SendTo("/topic/stop")
    public synchronized TimeMessage stop() {
        running.set(false);
        LocalTime time = startTime.get();
        startTime.set(time);
        return new TimeMessage(time.getHour(), time.getMinute(), time.getSecond(), running.get());
    }

    @MessageMapping("/reset")
    @SendTo("/topic/reset")
    public synchronized TimeMessage reset() {
        running.set(false);
        startTime.set(null);
        return new TimeMessage(0, 0, 0, running.get());
    }

    public static class TimeMessage {
        private int hours;
        private int minutes;
        private int seconds;
        private boolean running;

        public TimeMessage() {
        }

        public TimeMessage(int hours, int minutes, int seconds, boolean running) {
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
            this.running = running;
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }
}
