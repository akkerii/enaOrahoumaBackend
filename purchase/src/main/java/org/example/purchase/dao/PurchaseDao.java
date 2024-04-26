package org.example.purchase.dao;

import com.pi.cours.models.Course;
import org.example.purchase.dto.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseDao extends JpaRepository<Purchase,Long> {

    public List<Purchase> findPurchaseByIdUser(Long idUser);

    public List<Purchase> findPurchaseByIdCourse(Long IdCourse);

    public List<Purchase> findPurchasesBySellerId(Integer sellerId);

}
