package ro.payitforward.pay_it_forward_transfers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static java.util.Collections.emptyMap;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<FailureResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        final FailureResponseDto response = FailureResponseDto.builder()
                .message(e.getMessage())
                .code(ResponseCodeEnum.ILLEGAL_ARGUMENT.name())
                .fields(emptyMap())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
