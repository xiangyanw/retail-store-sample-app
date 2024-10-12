package com.amazon.sample.ui.config;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import java.util.List;

public class TracingFilter implements WebFilter {

    private static final List<String> HEADERS_TO_PROPAGATE = List.of(
            "x-request-id",
            "x-b3-traceid",
            "x-b3-spanid",
            "x-b3-parentspanid",
            "x-b3-sampled",
            "x-b3-flags",
            "x-ot-span-context"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange);
    }

    public static ExchangeFilterFunction propagateTracingHeaders() {
        return (clientRequest, next) -> {
            ClientRequest.Builder builder = ClientRequest.from(clientRequest);
            HEADERS_TO_PROPAGATE.forEach(header -> {
                String value = clientRequest.headers().getFirst(header);
                if (value != null) {
                    builder.header(header, value);
                }
            });
            return next.exchange(builder.build());
        };
    }
}
