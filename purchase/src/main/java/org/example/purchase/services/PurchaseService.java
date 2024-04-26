package org.example.purchase.services;


import com.pi.cours.models.Course;
import com.pi.users.entities.User;
import org.example.purchase.dto.Purchase;

import java.util.List;

public interface PurchaseService {

    //create a purchase
    public void createPurchase(Long userId , Long courseId , String paymentId);
    // list all purchases
    public List<Purchase> getAllPurchases();


    //get purchases by id
    public Purchase getPurchaseById(Long idPurchase);

    //get purchases by id user
    public List<Purchase> getPurchaseByUserId(Long idUser);


    //get purchase by course id
    public List<Purchase> getPurchaseByCourseId(Long idCourse);


    //list all courses of a user
    public List<Course> getCoursesByUserId(Long userId);



    //list all user of a course

    public List<User> getUsersByIdCourse(Long id);



    //get list of payments with seller id
    public List<String> getPaymentId(Integer sellerId);


    public Double getTotalSells(Integer sellerId);


}
