package ro.payitforward.pay_it_forward_transfers.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class BaseResponse {
    private String status;
    private String code;
    private String message;
    private Map<String,String> fields;

    public enum BaseResponseStatus {
        SUCCESS, FAILURE
    }

    public enum BaseResponseCode {
        TRANSFER_REQUEST,
        TRANSFER_REQUEST_REFUSED,
        TRANSFER_PENDING,
        TRANSFER_REQUEST_CANCELED,
        SIGNATURE_VALID,
        TRANSFER_CANCELED,
        TRANSFER_COMPLETED
    }
}


