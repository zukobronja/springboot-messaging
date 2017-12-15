/*****
 * Spring Boot
 * Building Micro-services with Spring Boot and RabbitMQ.
 * 
 */
package com.db.springboot.messaging;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class DBSpringBootMessaging  {

	public static void main(String[] args) {
		SpringApplication.run(DBSpringBootMessaging.class, args);
	}

}


class Greet {
	private String message;
public Greet() {}

	public Greet(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
 
}

@RestController
class GreetingController{
	
	@Autowired
	Sender sender;
	
	@GetMapping("/message/{msg}")// http://localhost:8080/message/World
	Greet greet(@PathVariable String msg){
		sender.send("Hello -> " + msg);//Send to messaging node
		
		return new Greet("Hello -> " + msg);
	}

	
	/* Reactive using Mono; or Flux can be used. - Reactive client out of scope
	@RequestMapping("/")
	Mono<Greet> greet(){
		sender.send("Hello -> " + msg);//Send to messaging node
		return Mono.just(new Greet("Hello World!"));
	}
	*/
}


////////////////////////// MESSAGING PART ///////////

/*****
 * 
 * Sender Component
 *
 */
@Component 
class Sender {
	@Autowired
	RabbitMessagingTemplate template;
	@Bean
	Queue queue() {
		return new Queue("GreetQueue", false);
	}
	public void send(String message){
		System.out.println("Sent message: " + message);
		template.convertAndSend("GreetQueue", message);
	}
	
}

/*****
 * 
 * Receiver Component
 *
 */
@Component
class Receiver {
    @RabbitListener(queues = "GreetQueue")
    public void processMessage(String content) {
       System.out.println("Recived message: " + content);
    }
}


//////////////////////////////////