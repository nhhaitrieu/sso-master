package com.auth.application.controller;

import com.auth.application.model.Customer;
import com.auth.application.model.ResponseObject;
import com.auth.application.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/cus")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    List<Customer> getAllCustomer(){
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id){
        Optional<Customer> foundCus = customerRepository.findById(id);

        return foundCus.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Query customer succesfully", foundCus)
                        // you can replace "OK" with your defiend "error code"
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false", "cannot find customer with id = " + id, " ")
                );
    }

    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertCus(@RequestBody Customer newCus){

        return	ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("OK", "Insert customer successfully", customerRepository.save(newCus))
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateCus(@RequestBody Customer newCus, @PathVariable Long id){
        Customer updateCus = customerRepository.findById(id)
                .map(customer -> {
                    customer.setFullname(newCus.getFullname());
                    customer.setSex(newCus.getSex());
                    customer.setBorn(newCus.getBorn());
                    return customerRepository.save(customer);
                }).orElseGet(() -> {
                    newCus.setId(id);
                    return customerRepository.save(newCus);

                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update customer successfully",updateCus)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteCus(@PathVariable Long id){
        boolean exists = customerRepository.existsById(id);
        if (exists) {
            customerRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Delete customer successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("false", "cannot find customer to delete " , " ")
        );

    }


}
