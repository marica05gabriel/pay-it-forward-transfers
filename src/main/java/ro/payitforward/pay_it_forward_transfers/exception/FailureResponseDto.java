package ro.payitforward.pay_it_forward_transfers.exception;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FailureResponseDto {
    private String code;
    private String message;
    private Map<String, String> fields;
}
