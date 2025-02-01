package vn.giaihung.jobhunter.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.giaihung.jobhunter.service.SubscriberService;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final SubscriberService subscriberService;

    public EmailController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email/to-subscriber")
    @ApiMessage("Send email to suscribers")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional
    public String sendEmail() {
        System.out.println("Sending email to subscribers...");
        subscriberService.handleSendEmailToSubscribers();
        return "OK";
    }

    @PostMapping("/email/to-subscriber-image")
    @ApiMessage("Send mail to subscribers with image")
    public String sendEmailWithImage(@RequestParam("image") final MultipartFile image) throws IOException {
        subscriberService.handleSendEmailToSubscribers(image.getName(),
                image.getBytes(), image.getContentType());
        return "OK";
    }
}
