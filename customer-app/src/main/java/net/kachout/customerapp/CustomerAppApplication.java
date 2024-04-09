package net.kachout.customerapp;

import net.kachout.customerapp.entity.Customer;
import net.kachout.customerapp.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerAppApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository)
    {
        return  args -> {
            customerRepository.save(Customer.builder().email("ahmed@gmail.com").firstName("ahmed").lastName("ahmed").build());
            customerRepository.save(Customer.builder().email("med@gmail.com").firstName("med").lastName("med").build());
            customerRepository.save(Customer.builder().email("ali@gmail.com").firstName("ali").lastName("ali").build());
        };
    }
}
