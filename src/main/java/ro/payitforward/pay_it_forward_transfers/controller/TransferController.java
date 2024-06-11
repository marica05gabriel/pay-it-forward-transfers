package ro.payitforward.pay_it_forward_transfers.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import ro.payitforward.pay_it_forward_transfers.converter.Converter;
import ro.payitforward.pay_it_forward_transfers.dto.*;
import ro.payitforward.pay_it_forward_transfers.dto.enums.TransferActionEnum;
import ro.payitforward.pay_it_forward_transfers.dto.enums.TransferPartyEnum;
import ro.payitforward.pay_it_forward_transfers.dto.enums.TransferRequestConsentEnum;
import ro.payitforward.pay_it_forward_transfers.entity.Transfer;
import ro.payitforward.pay_it_forward_transfers.entity.TransferRequest;
import ro.payitforward.pay_it_forward_transfers.service.TransferRequestService;
import ro.payitforward.pay_it_forward_transfers.service.TransferService;

import java.security.SignatureException;
import java.util.*;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;
import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;
import static ro.payitforward.pay_it_forward_transfers.dto.BaseResponse.BaseResponseCode.*;
import static ro.payitforward.pay_it_forward_transfers.dto.BaseResponse.BaseResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping(value = "/api/transfer")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class TransferController {

    private final TransferRequestService transferRequestService;
    private final TransferService transferService;
    private final Converter<TransferRequest, TransferRequestDto> transferRequestToDtoConverter;

    public TransferController(TransferRequestService transferRequestService,
                              TransferService transferService,
                              @Qualifier(value = "transferRequestToDtoConverter")
                              Converter<TransferRequest, TransferRequestDto> transferRequestToDtoConverter,
                              @Qualifier(value = "transferToDtoConverter")
                              Converter<Transfer, TransferDto> transferToDtoConverter) {
        this.transferRequestService = transferRequestService;
        this.transferService = transferService;
        this.transferRequestToDtoConverter = transferRequestToDtoConverter;
        this.transferToDtoConverter = transferToDtoConverter;
    }

    private final Converter<Transfer, TransferDto> transferToDtoConverter;

    /***
     * Get transfer requests by transfer party
     * @param transferParty [transferor, transferee]
     * @param userId User id
     * @return {status, code, message}
     */
    @GetMapping(value = "/request/{transferParty}/{userId}")
    public ResponseEntity<Page<TransferRequestDto>> getTransferRequestsByTransferParty(@RequestParam(required = false) Integer page,
                                                                                       @RequestParam(required = false) Integer size,
                                                                                       @PathVariable String transferParty,
                                                                                       @PathVariable String userId) {
//        assert isNotBlank(userId);
//        assert isNotBlank(transferParty);
        final TransferPartyEnum party = TransferPartyEnum.getOrThrow(transferParty);

        if (isNull(page)) {
            page = 0;
        }
        if (isNull(size)) {
            size = 10;
        }

        final Page<TransferRequest> transferRequests = switch (party) {
            case TRANSFEROR -> transferRequestService.findByFromUserEqualsUserId(userId, page, size);
            case TRANSFEREE -> transferRequestService.findByToUserEqualsUserId(userId, page, size);
        };
        final Page<TransferRequestDto> result = transferRequests.map(transferRequestToDtoConverter::convert);
        return ResponseEntity.ok().body(result);
    }

    /***
     * Get transfers by transfer party
     * @param transferParty [transferor, transferee]
     * @param userId User id
     * @return {status, code, message}
     */
    @GetMapping(value = "/{transferParty}/{userId}")
    public ResponseEntity<Page<TransferDto>> getTransfersByTransferParty(@RequestParam Integer page,
                                                                         @RequestParam Integer size,
                                                                         @PathVariable String transferParty,
                                                                         @PathVariable String userId) {
//        assert isNotBlank(userId);
//        assert isNotBlank(transferParty);
        final TransferPartyEnum party = TransferPartyEnum.getOrThrow(transferParty);

        if (isNull(page)) {
            page = 0;
        }
        if (isNull(size)) {
            size = 10;
        }

        final Page<Transfer> transfers = switch (party) {
            case TRANSFEROR -> transferService.findByFromUserEqualsUserId(userId, page, size);
            case TRANSFEREE -> transferService.findByToUserEqualsUserId(userId, page, size);
        };
        final Page<TransferDto> result = transfers.map(transferToDtoConverter::convert);
        return ResponseEntity.ok().body(result);
    }

    /***
     * Get transfers where user is involved either as a transferor or transferee.
     * @param userId User id
     * @return {status, code, message}
     */
    @GetMapping(value = "/byUser/{userId}")
    public ResponseEntity<Collection<TransferDto>> getTransfersByTransferParty(@PathVariable String userId) {
//        assert isNotBlank(userId);
//        assert isNotBlank(transferParty);

        final Collection<Transfer> transfers = transferService.findByUser(userId);
        final Collection<TransferDto> result = transfers.stream().map(transferToDtoConverter::convert).toList();
        return ResponseEntity.ok().body(result);
    }


    /***
     * Get transfers by id
     * @param uuid Transfer id
     * @param userId User id
     * @return TransferDto
     */
    @GetMapping(value = "/byId/{uuid}/{userId}")
    public ResponseEntity<TransferDto> getTransferById(@PathVariable String uuid,
                                                       @PathVariable String userId) {
//        assert isNotBlank(userId);
//        assert isNotBlank(uuid);

        final Transfer transfer = transferService.findOrThrow(UUID.fromString(uuid));
        if (!transfer.getToUser().equalsIgnoreCase(userId)
                && !transfer.getFromUser().equalsIgnoreCase(userId)) {
            log.error("User id: {} not involved in transfer: {}!", userId, uuid);
            throw new IllegalArgumentException("User id: " + userId + " not involved in transfer: " + uuid + "!");
        }

        final TransferDto result = transferToDtoConverter.convert(transfer);
        return ResponseEntity.ok().body(result);
    }

    /***
     * Creates a Transfer Request
     * @param request {from, to, target, fromPublicId, toPublicId, targetPublicId}
     * @return {status, code, message}
     */
    @PostMapping(value = "/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> transferRequest(@RequestBody TransferRequestCreationDto request) {
        validateTransactionRequestCreation(request);

        final TransferRequest transferRequest = transferRequestService.create(request);

        final Map<String, String> fields = new HashMap<>();
        fields.put("id", transferRequest.getId().toString());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .status(SUCCESS.name())
                        .code(TRANSFER_REQUEST.name())
                        .message("Transfer request successfully created.")
                        .fields(fields)
                        .build());
    }


    /***
     * A transfer request can be accepted and will generate a new Transfer will be created with status 'PENDING'
     * A transfer request can be refused by the 'from' participant
     * A transfer request can be canceled by the 'to' participant
     * @param consent Possible values: [accept, refuse, cancel]
     * @return {status, code, message}
     */
    @PatchMapping(value = "/request/{consent}/{uuid}")
    public ResponseEntity<BaseResponse> transferRequestConsent(@PathVariable String consent,
                                                               @PathVariable String uuid,
                                                               @RequestBody TransferRequestConsentDto request) {

        final TransferRequestConsentEnum consentEnum = TransferRequestConsentEnum.getOrThrow(consent);
        final UUID result = transferRequestService.consent(consentEnum, uuid, request);

        return switch (consentEnum) {
            case CANCEL -> ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .status(SUCCESS.name())
                            .code(TRANSFER_REQUEST_CANCELED.name())
                            .message("Transfer request was canceled.")
                            .build());
            case ACCEPT -> ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.builder()
                            .status(SUCCESS.name())
                            .code(TRANSFER_PENDING.name())
                            .message("Transfer request accepted. The new transfer is pending until completion. Transfer id: " + result)
                            .build());
            case REFUSE -> ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .status(SUCCESS.name())
                            .code(TRANSFER_REQUEST_REFUSED.name())
                            .message("Transfer request consent received. The transfer was refused.")
                            .build());
        };
    }

    /***
     * Validates a signature for a transfer.
     * The signature must be generated by 'to' participant.
     * The signature shall be validated by the 'from' participant, before passing the target object.
     * @param uuid Transfer id
     * @param request Request must contain the signature. {signature}
     * @return {status, code, message}
     */
    @PostMapping(value = "/{uuid}/signature/validate")
    public ResponseEntity<BaseResponse> validateSignatureForTransfer(@PathVariable String uuid,
                                                                     @RequestBody TransferActionDto request) throws SignatureException {

        validateSignatureRequest(uuid, request);

        transferService.validateSignature(UUID.fromString(uuid), request.getSignature());

        return ResponseEntity.ok().body(
                BaseResponse.builder()
                        .status(SUCCESS.name())
                        .code(SIGNATURE_VALID.name())
                        .message("Signature is valid. Transfer id: " + uuid)
                        .build());
    }


    /***
     * A pending transfer can be completed or cancelled
     * @param action Possible values: [complete, cancel]
     * @param request Request must contain the candidate id and candidate public id. {candidateId, candidatePublicId}
     *                If the action is 'complete', the request must contain the signature. {candidateId, candidatePublicId, signature}
     * @return {status, code, message}
     */
    @PatchMapping(value = "/{uuid}/{action}")
    public ResponseEntity<BaseResponse> transferAction(@PathVariable String uuid,
                                                       @PathVariable String action,
                                                       @RequestBody TransferActionDto request) throws SignatureException {
//        assert isNotBlank(uuid);
//        assert nonNull(request);
//        assert isNotBlank(request.getCandidateId());
//        assert isNotBlank(request.getCandidatePublicId());

        final TransferActionEnum transferAction = TransferActionEnum.getOrThrow(action);
        final Transfer transfer = transferService.execute(transferAction, UUID.fromString(uuid), request);

        return switch (transferAction) {
            case CANCEL -> ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .status(SUCCESS.name())
                            .code(TRANSFER_CANCELED.name())
                            .message("Transfer with id: " + transfer.getId() + " was canceled")
                            .build());
            case COMPLETE -> ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .status(SUCCESS.name())
                            .code(TRANSFER_COMPLETED.name())
                            .message("Transfer with id: " + transfer.getId() + " completed successfully.")
                            .build());
        };
    }

    private void validateTransactionRequestCreation(TransferRequestCreationDto request) {
        final List<String> missingFields = new ArrayList<>();
        if (isNull(request)) {
            missingFields.add("request");
        }
        if (isBlank(request.getTo())) {
            missingFields.add("to");
        }
        if (isBlank(request.getToPublicId())) {
            missingFields.add("toPublicId");
        }
        if (isBlank(request.getFrom())) {
            missingFields.add("from");
        }
        if (isBlank(request.getFromPublicId())) {
            missingFields.add("fromPublicId");
        }
        if (isBlank(request.getTarget())) {
            missingFields.add("target");
        }
        if (isBlank(request.getTargetPublicId())) {
            missingFields.add("targetPublicId");
        }
        if (!CollectionUtils.isEmpty(missingFields)) {
            throw new IllegalArgumentException(String.join(", ", missingFields));
        }
    }

    private void validateSignatureRequest(String uuid, TransferActionDto request) {
        final List<String> missingFields = new ArrayList<>();

        if (isBlank(uuid)) {
            missingFields.add("uuid");
        }
        if (isNull(request)) {
            missingFields.add("request");
        }
        if (isBlank(request.getSignature())) {
            missingFields.add("signature");
        }

        if (!CollectionUtils.isEmpty(missingFields)) {
            throw new IllegalArgumentException(String.join(", ", missingFields));
        }
    }
}
