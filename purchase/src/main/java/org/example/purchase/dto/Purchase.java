package org.example.purchase.dto;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  Long idUser ;

     private Long idCourse;

     private Integer sellerId ;

     private String paymentId;

    @CreationTimestamp
    private Date dateEnrolled;

    private BigDecimal price ;
}



