/* This file has been generated by gensignals.py. Do not edit */
package com.agateau.burgerparty.utils;

public class Signal1<T1> extends AbstractSignal<Signal1.Handler<T1>> {
	public interface Handler<T1> {
		void handle(T1 a1);
	}

	public void emit(T1 a1) {
		for(Handler<T1> handler : mHandlers) {
			handler.handle(a1);
		}
	}
}
