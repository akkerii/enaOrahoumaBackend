package com.pi.projet.ServiceImp;

import com.pi.projet.DTO.RequestProjet;
import com.pi.projet.DTO.ResponseProjet;
import com.pi.projet.FeignClients.User;
import com.pi.projet.FeignClients.UserProfile;
import com.pi.projet.Services.ProjetService;
import com.pi.projet.entities.Category;
import com.pi.projet.entities.ProjectStatus;
import com.pi.projet.entities.Projet;
import com.pi.projet.repositories.CategoryRepo;
import com.pi.projet.repositories.ProjetRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetServiceImp implements ProjetService {


    private final ProjetRepo projetRepo ;
    private final CategoryRepo categoryRepo;
    private final User user ;



    @Override
    public ResponseEntity<?> createProject(RequestProjet requestProjet) {

        if(requestProjet.getTitle()!=null && requestProjet.getDescription()!=null && requestProjet.getCategoryId()!=null && requestProjet.getCreatorId()!=null){

            Projet projet = this.mapDTOToModel(requestProjet);
            projet.setStatus(ProjectStatus.OPEN);
            projetRepo.save(projet);
            ResponseProjet responseProjet = this.mapModelToDTO(projet);
            return ResponseEntity.ok(responseProjet);

        }else {
            return ResponseEntity.badRequest().body("bad request");
        }



    }

    @Override
    public ResponseEntity<List<ResponseProjet>> findProjetByCategory_Id(Long id) {
        List<Projet> projets = projetRepo.findProjetByCategory_Id(id);
        List<ResponseProjet> responseProjets = projets.stream().map(this::mapModelToDTO).toList();

        return ResponseEntity.ok(responseProjets);
    }

    @Override
    public List<ResponseProjet> getAllProjects() {
        List<Projet> projets = projetRepo.findAll();
        List<ResponseProjet> responseProjets = projets.stream().map(this::mapModelToDTO).toList();
        return responseProjets;
    }

    @Override
    public ResponseEntity<?> updateProjetTitle(Long id, String title) {
        Optional<Projet> projet = projetRepo.findById(id);
        if(projet.isPresent() && title!=null){
            Projet projet1 = projet.get();
            projet1.setTitle(title);
            return ResponseEntity.ok(this .mapModelToDTO(projetRepo.save(projet1)));
        }
        else
            return ResponseEntity.badRequest().body("project do not exists");
    }

    @Override
    public ResponseEntity<?> updateProjetDescription(Long id, String desc) {
        Optional<Projet> projet = projetRepo.findById(id);
        if(projet.isPresent() && desc!=null){
            Projet projet1 = projet.get();
            projet1.setDescription(desc);
            return ResponseEntity.ok(this .mapModelToDTO(projetRepo.save(projet1)));
        }
        else
            return ResponseEntity.badRequest().body("project do not exists");
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateProjetCategory(Long id, Long catId) {
        Optional<Projet> projet = projetRepo.findById(id);
        Optional<Category> category = categoryRepo.findById(catId);
        if(projet.isPresent() && category.isPresent()){
            Projet projet1 = projet.get();
            Category category1 = category.get();
            projet1.setCategory(category1);
            projetRepo.save(projet1);
            return ResponseEntity.ok(this.mapModelToDTO(projet1));
        }
        else return ResponseEntity.badRequest().body("Error While Updating Category !");
    }

    @Override
    public ResponseEntity<?> deleteProjet(Long id) {
        Optional<Projet> projet = projetRepo.findById(id);
        if(projet.isPresent()){
            projetRepo.delete(projet.get());
           return ResponseEntity.ok("Project deleted Successfully ! ");
        }
        else
            return ResponseEntity.badRequest().body("Project Do Not Exist !");
    }

    @Override
    public ResponseEntity<?> getAllOpenProjet() {
     List<Projet> projets=projetRepo.findOpenProjet();
     List<ResponseProjet> responseProjets = projets.stream().map(this::mapModelToDTO).toList();
        return ResponseEntity.ok(responseProjets);
    }

    @Override
    public ResponseEntity<?> getAllClosedProjet() {
        List<Projet> projets=projetRepo.findClosedProjet();
        List<ResponseProjet> responseProjets = projets.stream().map(this::mapModelToDTO).toList();
        return ResponseEntity.ok(responseProjets);
    }

    @Override
    public ResponseEntity<?> openProjet(Long id) {
        Optional<Projet> projet = projetRepo.findById(id);
        if(projet.isPresent()){
            Projet projet1 = projet.get();
            projet1.setStatus(ProjectStatus.OPEN);
            projetRepo.save(projet1);
            return ResponseEntity.ok("Project Opened");
        }
        else
            return ResponseEntity.badRequest().body("Project Do Not Exist");
    }

    @Override
    public ResponseEntity<?> closeProjet(Long id) {
        Optional<Projet> projet = projetRepo.findById(id);
        if(projet.isPresent()){
            Projet projet1 = projet.get();
            projet1.setStatus(ProjectStatus.CLOSED);
            projetRepo.save(projet1);
            return ResponseEntity.ok("Project  Closed");
        }
        else
            return ResponseEntity.badRequest().body("Project Do Not Exist");
    }

    @Override
    public ResponseEntity<?> getUserProjets(Long id) {
        List<Projet> projets = projetRepo.findProjetByCreatorId(id);
        if(projets.isEmpty())
            return ResponseEntity.ok("You don't have Projects !");
        else
            return ResponseEntity.ok(projets.stream().map(this::mapModelToDTO).toList());
    }


    public Projet mapDTOToModel (RequestProjet requestProjet){
        Optional<Category> category = categoryRepo.findById(requestProjet.getCategoryId());
        return category.map(value -> Projet.builder()
                .title(requestProjet.getTitle())
                .description(requestProjet.getDescription())
                .category(value)
                .creatorId(requestProjet.getCreatorId())
                .status(ProjectStatus.OPEN)
                .build()).orElse(null);
    }

    public ResponseProjet mapModelToDTO(Projet projet) {
        return ResponseProjet.builder()
                .title(projet.getTitle())
                .description(projet.getDescription())
                .creationDate(projet.getCreationDate())
                .status(projet.getStatus())
                .build();
    }



}
