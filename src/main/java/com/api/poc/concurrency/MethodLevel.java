package com.api.poc.concurrency;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import android.R.integer;

public class MethodLevel {

	// Simulate an asynchronous task that takes some time and returns a result after
	// a delay.
	public static CompletableFuture<String> actualMethod(int request) {
		try {
		return CompletableFuture.supplyAsync(() -> {
				//Math.floorDiv(request, request);
				return "Response for request: " + request;
		});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		// List of requests (you can replace these with your actual requests).
		List<Integer> reqObjList = new ArrayList<>();

		int n = 0;
		while (n <= 100) {
			reqObjList.add(n++);
		}

		List<CompletableFuture<String>> completableFutures = new ArrayList<>();

		for (int param : reqObjList) {
			CompletableFuture<String> completableFuture = actualMethod(param);
			completableFutures.add(completableFuture);
		}

		CompletableFuture<Void> allFutures = CompletableFuture
				.allOf(completableFutures.toArray(new CompletableFuture[0]));

		allFutures.join();

		for (CompletableFuture<String> future : completableFutures) {
			try {
				String response = future.get();
				System.out.println(response);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		System.out.println("End call");
	}
}
