package org.example.purchase.services;

import com.pi.cours.models.Course;
import com.pi.users.entities.User;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import org.example.purchase.dao.PurchaseDao;
import org.example.purchase.dto.Purchase;
import org.example.purchase.externalApi.CourseApi;
import org.example.purchase.externalApi.UserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PurchaseImp implements PurchaseService {


    @Autowired
    PurchaseDao purchaseDao;

    @Autowired
    CourseApi courseApi ;

    @Autowired
    UserApi userApi ;
    @Override
    public void createPurchase(Long userId , Long courseId , String paymentId) {

        //


        Course course = courseApi.getCoursById(courseId);
        if(course==null){
            throw new RuntimeException("course not found");
        }
/*
        User user = userApi.getUserById(userId);
        if(user==null){
            throw new RuntimeException("user not found");
        }
*/


        Purchase purchase = new Purchase();
        purchase.setSellerId(course.getUserId());
        purchase.setPaymentId(paymentId);
        purchase.setIdUser(userId);
        purchase.setIdCourse(courseId);

        purchaseDao.save(purchase);


    }

    @Override
    public List<Purchase> getAllPurchases() {
        return purchaseDao.findAll();
    }

    @Override
    public Purchase getPurchaseById(Long idPurchase) {
        return purchaseDao.findById(idPurchase).orElseThrow(()->new RuntimeException("purchase not found"));

    }

    @Override
    public List<Purchase> getPurchaseByUserId(Long idUser) {
        return purchaseDao.findPurchaseByIdUser(idUser);
    }

    @Override
    public List<Purchase> getPurchaseByCourseId(Long idCourse) {
        return purchaseDao.findPurchaseByIdCourse(idCourse);
    }

    @Override
    public List<Course> getCoursesByUserId(Long userId) {

//        List<Purchase> purchases = purchaseDao.findPurchaseByIdUser(userId);
//
//        List<Course> courses= new ArrayList<>();
//        for (Purchase p : purchases){
//            courses.add(courseApi.getCoursById(p.getIdCourse()));
//
//        }
//
//
//
//        return courses;
        List<Purchase> purchases = purchaseDao.findPurchaseByIdUser(userId);

        return purchases.stream()
                .map(purchase -> {
                    try {
                        return courseApi.getCoursById(purchase.getIdCourse());
                    } catch (Exception e) {
                        throw new RuntimeException("Error retrieving course with ID: " + purchase.getIdCourse(), e);

                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersByIdCourse(Long idC) {
       List<Purchase> purchases = purchaseDao.findPurchaseByIdCourse(idC);


        return purchases.stream()
                .map(purchase -> {
                    try {
                       return userApi.getUserById(purchase.getIdUser());
                    }catch (Exception e){
                        throw new RuntimeException("erreur with retrieving user by id");
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    @Override
    public List<String> getPaymentId(Integer sellerId) {
        List<Purchase> purchases = purchaseDao.findPurchasesBySellerId(sellerId);
        List<String> paymentIds = purchases.stream()
                .map(Purchase::getPaymentId) // Assuming there's a getPaymentId method in your Purchase class
                .collect(Collectors.toList());
        return paymentIds;
    }


    public Double getTotalSells(Integer sellerId) {
        double total = 0.0; // Initialize total to 0
        List<String> paymentsIdS = getPaymentId(sellerId);

        Stripe.apiKey = "sk_test_51OvQL1JeISkzjGkftJ4YeJZTGwgr5KxjespCPaAL1BwNNXvtzXZFCRJUEGsZfuqfRO43gXiV4fPDqbnN2YTkmPTA00eGjIguha";

        for (String paymentId : paymentsIdS) {
            try {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
                // Stripe amounts are in the smallest currency unit (e.g., cents for USD)
                double amount = paymentIntent.getAmount() / 100.0;
                total += amount;
            } catch (Exception e) {
                // Handle any exceptions, such as a payment ID not found
                e.printStackTrace();
            }
        }

        return total;
    }


}



