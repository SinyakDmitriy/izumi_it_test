package com.izumi_it_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SpringBootApplication
public class IzumiItApllication {
	int MAX_RANGE_FILTER_BUFFER_LENGTH = 512;
	private CarSateBuffer carStateBuffer;
	List<CarState> list = new CopyOnWriteArrayList<>();
	public static void main(String[] args) {
		SpringApplication.run(IzumiItApllication.class, args);
	}

	@PostConstruct void init() throws InterruptedException {
		carStateBuffer = new CarSateBuffer();
		for (int i = 1024; i > 0; i--) {
			CarState carState = new CarState();
			carState.setCarTimestamp(System.currentTimeMillis());
			carState.setCarTemp((short) (i + (Math.random() * 50)));
			carState.setCarId(1L);
			Thread.sleep(3);
			list.add(carState);
		}

		short value = 0;
		for (CarState state : list) {
			carStateBuffer.put(state);
			value = carStateBuffer.getLastFiltered(state.getCarId());
		}

		System.out.println("filtered - " + carStateBuffer.getLastFiltered(1));
	}
}
