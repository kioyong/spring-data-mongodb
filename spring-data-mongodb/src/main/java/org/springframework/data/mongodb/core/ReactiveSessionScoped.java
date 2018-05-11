/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import org.reactivestreams.Publisher;

import com.mongodb.reactivestreams.client.ClientSession;

/**
 * Gateway interface to execute {@link ClientSession} bound operations against MongoDB via a
 * {@link ReactiveSessionCallback}.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 2.1
 */
public interface ReactiveSessionScoped {

	/**
	 * Executes the given {@link ReactiveSessionCallback} within the {@link com.mongodb.session.ClientSession}.
	 * <p/>
	 * It is up to the caller to make sure the {@link com.mongodb.session.ClientSession} is {@link ClientSession#close()
	 * closed} when done.
	 *
	 * @param action callback object that specifies the MongoDB action the callback action. Must not be {@literal null}.
	 * @param <T> return type.
	 * @return a result object returned by the action. Can be {@literal null}.
	 */
	default <T> Flux<T> execute(ReactiveSessionCallback<T> action) {
		return execute(action, (session) -> {});
	}

	/**
	 * Executes the given {@link ReactiveSessionCallback} within the {@link com.mongodb.session.ClientSession}.
	 * <p/>
	 * It is up to the caller to make sure the {@link com.mongodb.session.ClientSession} is {@link ClientSession#close()
	 * closed} when done.
	 *
	 * @param action callback object that specifies the MongoDB action the callback action. Must not be {@literal null}.
	 * @param doFinally callback object that accepts {@link ClientSession} after invoking {@link ReactiveSessionCallback}.
	 *          This {@link Consumer} is guaranteed to be notified in any case (successful and exceptional outcome of
	 *          {@link ReactiveSessionCallback}).
	 * @param <T> return type.
	 * @return a result object returned by the action. Can be {@literal null}.
	 */
	default <T> Flux<T> execute(ReactiveSessionCallback<T> action, Consumer<ClientSession> doFinally) {
		return Flux.from(flatMap(action, doFinally));
	}

	/**
	 * Executes the given {@link ReactiveSessionCallback} within the {@link com.mongodb.session.ClientSession} returning a
	 * plain {@link Publisher}. For more convenience use {@link #execute(ReactiveSessionCallback, Consumer)} or
	 * {@link #executeSingle(ReactiveSessionCallback, Consumer)} to get the Reactor types.
	 * <p/>
	 * It is up to the caller to make sure the {@link com.mongodb.session.ClientSession} is {@link ClientSession#close()
	 * closed} when done.
	 *
	 * @param action callback object that specifies the MongoDB action the callback action. Must not be {@literal null}.
	 * @param doFinally callback object that accepts {@link ClientSession} after invoking {@link ReactiveSessionCallback}.
	 *          This {@link Consumer} is guaranteed to be notified in any case (successful and exceptional outcome of
	 *          {@link ReactiveSessionCallback}).
	 * @param <T> return type.
	 * @return a result object returned by the action. Can be {@literal null}.
	 */
	<T> Publisher<T> flatMap(ReactiveSessionCallback<T> action, Consumer<ClientSession> doFinally);

	/**
	 * Executes the given {@link ReactiveSessionCallback} within the {@link com.mongodb.session.ClientSession} returning
	 * single result emitted via {@link Mono}.
	 * <p/>
	 * It is up to the caller to make sure the {@link com.mongodb.session.ClientSession} is {@link ClientSession#close()
	 * closed} when done.
	 *
	 * @param action callback object that specifies the MongoDB action the callback action. Must not be {@literal null}.
	 * @param <T> return type.
	 * @return a result object returned by the action. Can be {@literal null}.
	 */
	default <T> Mono<T> executeSingle(ReactiveSessionCallback<T> action) {
		return executeSingle(action, (session) -> {});
	}

	/**
	 * Executes the given {@link ReactiveSessionCallback} within the {@link com.mongodb.session.ClientSession} returning
	 * single result emitted via {@link Mono}.
	 * <p/>
	 * It is up to the caller to make sure the {@link com.mongodb.session.ClientSession} is {@link ClientSession#close()
	 * closed} when done.
	 *
	 * @param action callback object that specifies the MongoDB action the callback action. Must not be {@literal null}.
	 * @param doFinally callback object that accepts {@link ClientSession} after invoking {@link ReactiveSessionCallback}.
	 *          This {@link Consumer} is guaranteed to be notified in any case (successful and exceptional outcome of
	 *          {@link ReactiveSessionCallback}).
	 * @param <T> return type.
	 * @return a result object returned by the action. Can be {@literal null}.
	 */
	default <T> Mono<T> executeSingle(ReactiveSessionCallback<T> action, Consumer<ClientSession> doFinally) {
		return Mono.from(flatMap(action, doFinally));
	}
}