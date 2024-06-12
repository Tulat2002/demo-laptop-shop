package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create s subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        //check exists
        boolean isExist = this.subscriberService.isExistsByEmail(subscriber.getEmail());
        if (isExist == true){
            throw new IdInvalidException("Email " + subscriber.getEmail() + " da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.createSubscriber(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {
        //check ton tai
        Subscriber subscriberDb = this.subscriberService.findById(subscriber.getId());
        if (subscriberDb == null){
            throw new IdInvalidException("Id " + subscriber.getId() + " not found");
        }
        return ResponseEntity.ok().body(this.subscriberService.updateSubscribers(subscriberDb, subscriber));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscribers skill")
    public ResponseEntity<Subscriber> getSubscribersSkill(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

}
