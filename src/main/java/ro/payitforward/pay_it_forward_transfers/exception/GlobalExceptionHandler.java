package ro.payitforward.pay_it_forward_transfers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ro.payitforward.pay_it_forward_transfers.dto.BaseResponse;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static ro.payitforward.pay_it_forward_transfers.dto.BaseResponse.BaseResponseCode.SIGNATURE_NOT_VALID;
import static ro.payitforward.pay_it_forward_transfers.dto.BaseResponse.BaseResponseCode.SIGNATURE_VALID;
import static ro.payitforward.pay_it_forward_transfers.dto.BaseResponse.BaseResponseStatus.FAILURE;
import static ro.payitforward.pay_it_forward_transfers.dto.BaseResponse.BaseResponseStatus.SUCCESS;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<FailureResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        final FailureResponseDto response = FailureResponseDto.builder()
                .message(e.getMessage())
                .code(ResponseCodeEnum.ILLEGAL_ARGUMENT.name())
                .fields(emptyMap())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SignatureException.class)
    public ResponseEntity<BaseResponse> handleSignatureNotValid(SignatureException e) {
        final Map<String, String> fields = new HashMap<>();
        fields.put("errorMessage", e.getMessage());
        return ResponseEntity.ok().body(
                BaseResponse.builder()
                        .status(FAILURE.name())
                        .code(SIGNATURE_NOT_VALID.name())
                        .message("Signature is not valid!")
                        .fields(fields)
                        .build());
    }

}
