package ru.adel.socialmedia.util.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    private List<ValidationError> errors;

    public ValidationErrorResponse(HttpStatus status, String msg, List<ValidationError> errors) {
        super(status, msg);
        this.errors = errors;
    }


}
