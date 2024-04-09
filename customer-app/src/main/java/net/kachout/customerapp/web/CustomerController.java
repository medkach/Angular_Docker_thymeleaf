package net.kachout.customerapp.web;

import net.kachout.customerapp.entity.Customer;
import net.kachout.customerapp.model.Product;
import net.kachout.customerapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller

public class CustomerController {
    public final CustomerRepository customerRepository;
    public final ClientRegistrationRepository clientRegistrationRepository;
    //private final RestClient restClient;
    @Value("${inventory.service.base.uri}")
    private String inventoryServiceBaseUri;
    public CustomerController(CustomerRepository customerRepository, ClientRegistrationRepository clientRegistrationRepository) {
        this.customerRepository = customerRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
//        restClient = RestClient.builder()
//                .baseUrl("http://localhost:8082")
//                .build();
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('admin')")
    public String customers(Model model) {
        List<Customer> customerList = customerRepository.findAll();
        model.addAttribute("customers", customerList);
        return "customers";
    }
    @GetMapping("/products")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String products(Model model) {
        RestClient restClient   = RestClient.create(inventoryServiceBaseUri);
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        DefaultOidcUser oidcUser = (DefaultOidcUser) oAuth2AuthenticationToken.getPrincipal();
        String jwtTokenValue = oidcUser.getIdToken().getTokenValue();
        List<Product> products = restClient.get()
                .uri("/products")
                .headers(httpHeaders -> httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenValue))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        model.addAttribute("products",products);

        return "products";
    }
    @ResponseBody
    @GetMapping("/authentication")
    public Authentication authentication(Authentication authentication){
        return authentication;
    }
    @GetMapping("/oauth2Login")
    public String oauth2Login(Model model) {
        String authorizationRequestBaseUri = "oauth2/authorization";
        Map<String, String> oauth2AuthenticationUrls = new HashMap();
        Iterable<ClientRegistration> clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        clientRegistrations.forEach(registration ->{
            oauth2AuthenticationUrls.put(registration.getClientName(),
                    authorizationRequestBaseUri + "/" + registration.getRegistrationId());
        });
        oauth2AuthenticationUrls.forEach((k,v)-> System.out.println("k:"+k+"v:"+v));
        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "oauth2Login";
    }
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/notAutorized")
    public String notAutorized() {
        return "notAutorized";
    }

}
