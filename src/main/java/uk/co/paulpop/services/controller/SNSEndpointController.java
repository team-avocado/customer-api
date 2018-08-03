package uk.co.paulpop.services.controller;

import io.swagger.annotations.Api;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationSubject;
import org.springframework.cloud.aws.messaging.endpoint.NotificationStatus;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationMessageMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationUnsubscribeConfirmationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("Java Spring Service API")
@RestController
@RequestMapping("/flightUpdates")
public class SNSEndpointController {

    @NotificationSubscriptionMapping
    public void confirmUnsubscribeMessage(
        NotificationStatus notificationStatus) {
        System.out.println("Hit subscription confirmation");
        notificationStatus.confirmSubscription();
    }

    @NotificationMessageMapping
    public void receiveNotification(@NotificationMessage String message,
                                    @NotificationSubject String subject) {
        // handle message
        System.out.println("Hit the endpoint");
        System.out.println(message);
        System.out.println(subject);
    }

    @NotificationUnsubscribeConfirmationMapping
    public void confirmSubscriptionMessage(
        NotificationStatus notificationStatus) {
        System.out.println("Hit subscription confirmation");
        notificationStatus.confirmSubscription();
    }
}
