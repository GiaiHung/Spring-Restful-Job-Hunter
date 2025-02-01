package vn.giaihung.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.giaihung.jobhunter.domain.Subscriber;
import vn.giaihung.jobhunter.service.SubscriberService;
import vn.giaihung.jobhunter.utils.SecurityUtil;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber postSubscriber)
            throws InvalidIdException {
        Subscriber newSubscriber = subscriberService.handleCreateSubscriber(postSubscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSubscriber);
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber putSubscriber)
            throws InvalidIdException {
        Subscriber updatedSubscriber = subscriberService.handleUpdateSubscriber(putSubscriber);
        return ResponseEntity.ok().body(updatedSubscriber);
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skills")
    public ResponseEntity<Subscriber> getSubscribersSkills() throws InvalidIdException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.isEmpty()) {
            throw new InvalidIdException("Please logged in to access this endpoint");
        }

        return ResponseEntity.ok().body(subscriberService.handleGetSubscriberByEmail(email));
    }
}
