package com.nexus.stripe;

import com.nexus.abstraction.UserContext;
import com.nexus.email.SendEmailService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.notification.NotificationDTO;
import com.nexus.notification.NotificationManager;
import com.nexus.notification.NotificationType;
import com.nexus.tenant.Tenant;
import com.nexus.tenant.TenantContext;
import com.nexus.tenant.TenantRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.net.OAuth;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("stripe")
public class StripeController extends UserContext {

    private static final Logger LOG = LoggerFactory.getLogger(StripeController.class);
    private final TenantRepository tenantRepository;
    private final NotificationManager notificationManager;
    private final SendEmailService sendEmailService;

    @Value("${stripe.webhook-secret}")
    private String stripeWebhookSecret;

    @Value("${stripe.client-id}")
    private String stripeClientId;

    @Value("${stripe.redirect-uri}")
    private String stripeRedirectUri;

    public StripeController(TenantRepository tenantRepository, NotificationManager notificationManager, SendEmailService sendEmailService) {
        this.tenantRepository = tenantRepository;
        this.notificationManager = notificationManager;
        this.sendEmailService = sendEmailService;
    }

    @GetMapping("/connect")
    public void connectStripe(HttpServletResponse response) {
        try {
            String url = "https://connect.stripe.com/oauth/authorize?response_type=code" +
                    "&client_id=" + stripeClientId +
                    "&scope=read_write" +
                    "&redirect_uri=" + URLEncoder.encode(stripeRedirectUri, StandardCharsets.UTF_8);
            response.sendRedirect(url);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<String> stripeCallback(@RequestParam("code") String code,
                                                 @RequestParam(value = "state", required = false) String state) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("client_secret", Stripe.apiKey);
            params.put("code", code);
            params.put("grant_type", "authorization_code");

            RequestOptions options = RequestOptions
                    .builder()
                    .setClientId(Stripe.apiKey)
                    .build();

            TokenResponse tokenResponse = OAuth.token(params, options);

            Tenant tenant = tenantRepository.findById(TenantContext.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

            tenant.setStripeAccountId(tokenResponse.getStripeUserId());

            return ResponseEntity.ok("Stripe account connected: " + tokenResponse.getStripeUserId());
        } catch (StripeException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{paymentMethodId}")
    public ResponseEntity<PaymentMethod> getPaymentMethod(@Valid @NotEmpty @PathVariable String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            return ResponseEntity.ok().body(paymentMethod);
        } catch (StripeException e) {
            LOG.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(@RequestBody @Valid CreateSubscriptionRequest request) {
        SubscriptionCreateParams.PaymentSettings paymentSettings =
                SubscriptionCreateParams.PaymentSettings
                        .builder()
                        .setSaveDefaultPaymentMethod(SubscriptionCreateParams
                                .PaymentSettings
                                .SaveDefaultPaymentMethod.ON_SUBSCRIPTION)
                        .build();

        SubscriptionCreateParams subCreateParams = SubscriptionCreateParams
                .builder()
                .setCustomer(request.customerId())
                .addItem(
                        SubscriptionCreateParams
                                .Item.builder()
                                .setPrice(request.priceId())
                                .build()
                )
                .setPaymentSettings(paymentSettings)
                .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .addAllExpand(List.of("latest_invoice.payment_intent"))
                .putMetadata("tenantId", TenantContext.getTenantId().toString())
                .putMetadata("userId", getUserId().toString())
                .build();

        try {
            Subscription subscription = Subscription.create(subCreateParams);
            SubscriptionResponse response = new SubscriptionResponse(
                    subscription.getId(),
                    subscription.getLatestInvoiceObject().getPaymentIntentObject().getClientSecret()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("un-subscribe/{id}")
    public ResponseEntity<Subscription> unSubscribe(@Valid @NotEmpty @PathVariable String id ) {
        try {
            Subscription subscription = Subscription.retrieve(id);
            Subscription deletedSubscription = subscription.cancel();
            return ResponseEntity.ok(deletedSubscription);
        } catch (StripeException e) {
            LOG.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("update-subscription")
    public ResponseEntity<Subscription> updateSubscription(@Valid @RequestBody UpdateSubscriptionRequest request) {
        try {
            Subscription subscription = Subscription.retrieve(request.subscriptionId());

            SubscriptionUpdateParams params = SubscriptionUpdateParams
                    .builder()
                    .addItem(
                            SubscriptionUpdateParams
                                    .Item.builder()
                                    .setId(subscription.getItems().getData().getFirst().getId())
                                    .setPrice(request.newPriceId())
                                    .build()
                    )
                    .setCancelAtPeriodEnd(false)
                    .build();

            subscription.update(params);
            return ResponseEntity.ok(subscription);
        } catch (StripeException e) {
            LOG.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    @PostMapping("webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            LOG.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

        if (stripeObject == null) {
            LOG.error("Failed to deserialize Stripe event: {}", event.getType());
            return ResponseEntity.badRequest().body("Invalid event data");
        }

        switch (event.getType()) {
            case "invoice.paid": {
                Invoice invoicePaid = (Invoice) stripeObject;

                LOG.info("Invoice {} paid for customer {}", invoicePaid.getId(), invoicePaid.getCustomer());
                UUID tenantId = UUID.fromString(invoicePaid.getMetadata().get("tenantId"));
                tenantRepository.updateTenantSubscriptionStatus(tenantId, SubscriptionStatus.ACTIVE);

                sendEmailService.sendEmail(
                        invoicePaid.getCustomerObject().getEmail(),
                        "Thank you!",
                        "Your subscription payment was successful"
                );

                break;
            }
            case "invoice.payment_failed": {
                Invoice invoiceFailed = (Invoice) stripeObject;
                LOG.warn("Invoice {} payment failed for customer {}", invoiceFailed.getId(), invoiceFailed.getCustomer());
                long userId;
                try {
                    userId = Long.parseLong(invoiceFailed.getMetadata().get("userId"));
                } catch (NumberFormatException e) {
                    LOG.error("Invalid user id {}", invoiceFailed.getMetadata().get("userId"));
                    return ResponseEntity.badRequest().body("Invalid user id " + invoiceFailed.getMetadata().get("userId"));
                }

                String title = "Unable to subscribe";
                String body = "Please update your payment details and try again";

                notificationManager.addNotification(new NotificationDTO(userId, title, body, NotificationType.REMINDER));
                sendEmailService.sendEmail(invoiceFailed.getCustomerObject().getEmail(), title, body);
                break;
            }
            case "customer.subscription.created": {
                Subscription subscriptionCreated = (Subscription) stripeObject;
                LOG.info("Subscription {} created for customer {}", subscriptionCreated.getId(), subscriptionCreated.getCustomer());
                UUID tenantId = UUID.fromString(subscriptionCreated.getMetadata().get("tenantId"));
                tenantRepository.updateTenantSubscriptionStatus(tenantId, SubscriptionStatus.PENDING);

                sendEmailService.sendEmail(
                        subscriptionCreated.getCustomerObject().getEmail(),
                        "Subscription Created",
                        "Your subscription has been created and is pending payment."
                );
                break;
            }
            case "customer.subscription.updated": {
                Subscription subscriptionUpdated = (Subscription) stripeObject;
                LOG.info("Subscription {} updated for customer {}", subscriptionUpdated.getId(), subscriptionUpdated.getCustomer());
                String status = subscriptionUpdated.getStatus();
                UUID tenantId = UUID.fromString(subscriptionUpdated.getMetadata().get("tenantId"));

                if ("active".equals(status)) {
                    tenantRepository.updateTenantSubscriptionStatus(tenantId, SubscriptionStatus.ACTIVE);
                } else if ("past_due".equals(status)) {
                    tenantRepository.updateTenantSubscriptionStatus(tenantId, SubscriptionStatus.PAST_DUE);
                    sendEmailService.sendEmail(
                            subscriptionUpdated.getCustomerObject().getEmail(),
                            "Your subscription is past due",
                            "Your subscription is past due please update payment method or try again"
                    );
                } else if ("unpaid".equals(status)) {
                    tenantRepository.updateTenantSubscriptionStatus(tenantId, SubscriptionStatus.UNPAID);
                    sendEmailService.sendEmail(
                            subscriptionUpdated.getCustomerObject().getEmail(),
                            "Subscription un paid",
                            "Subscription un paid please update payment method or try again"
                    );
                }
                break;
            }
            case "customer.subscription.deleted": {
                Subscription deletedSubscription = (Subscription) stripeObject;

                LOG.info("Subscription {} canceled for customer {}", deletedSubscription.getId(), deletedSubscription.getCustomer());
                UUID tenantId = UUID.fromString(deletedSubscription.getMetadata().get("tenantId"));
                tenantRepository.updateTenantSubscriptionStatus(tenantId, SubscriptionStatus.CANCELED);

                break;
            }
            default:
                // Unhandled event type
                LOG.warn("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok().build();
    }
}
