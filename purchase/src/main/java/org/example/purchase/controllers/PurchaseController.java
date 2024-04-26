package org.example.purchase.controllers;


import com.pi.cours.models.Course;
import com.pi.users.entities.User;
import jakarta.ws.rs.PathParam;
import org.example.purchase.dto.Purchase;
import org.example.purchase.externalApi.CourseApi;
import org.example.purchase.externalApi.UserApi;
import org.example.purchase.services.PurchaseImp;
import org.example.purchase.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase")
@CrossOrigin
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @Autowired
    UserApi userApi ;

    @Autowired
    CourseApi courseApi;

    @PostMapping("/create/{userId}/{courseId}/{paymentId}")
    public ResponseEntity<String> createPurchase(@PathVariable Long userId, @PathVariable Long courseId , @PathVariable String paymentId) {
        purchaseService.createPurchase(userId, courseId , paymentId);
        return ResponseEntity.ok("added");
    }


    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id) {
        return userApi.getUserById(id);
    }


    @GetMapping("/cours/{id}")
    public Course getCoursById(@PathVariable Long id) {
        return courseApi.getCoursById(id);
    }


    @GetMapping("/all")

    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }
}

