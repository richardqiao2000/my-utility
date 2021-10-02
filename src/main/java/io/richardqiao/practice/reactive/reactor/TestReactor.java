package io.richardqiao.practice.reactive.reactor;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Stack;

/***
 * Test class
 */
public class TestReactor {

    public static void main(String[] args){
//        List<Integer> list = new ArrayList<>();
//        Flux.range(0, 5)
//                .parallel()
//                .runOn(Schedulers.boundedElastic())
//                .log()
//                .subscribe(list::add)
//                .dispose();
//        System.out.println(list.size());

//        ConnectableFlux<Object> publish = Flux.create(
//                fluxSink -> {
//                    while(true){
//                        fluxSink.next(System.nanoTime());
//                    }
//                })
//                .sample(Duration.ofSeconds(2))
//                .publish();
//        publish.subscribe(System.out::println);
//        publish.connect();

        Flux<Integer> fluxInteger = Flux.generate(
                () -> {
                    Stack<Integer> stack = new Stack<>();
                    stack.push(0);
                    stack.push(1);
                    stack.push(2);
                    stack.push(3);
                    stack.push(4);
                    return stack;
                    },
                (stack, sink) ->{
                    if(stack.isEmpty()){
                        sink.complete();
                        return stack;
                    }
                    int num = stack.pop();
                    System.out.println(num);
                    sink.next(num);
                    return stack;
                },
                Stack::clear
        );
        List<Integer> list = fluxInteger.collectList().block();
        assert list != null;
    }
}
