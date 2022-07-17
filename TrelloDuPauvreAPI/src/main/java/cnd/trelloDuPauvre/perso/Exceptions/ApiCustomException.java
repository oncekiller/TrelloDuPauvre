package cnd.trelloDuPauvre.perso.Exceptions;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
@Getter
@Setter
public class ApiCustomException {
    private String message;
    private String error;
    private HttpStatus status;

    private int statusCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiCustomException() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiCustomException(String message, String error, HttpStatus status, LocalDateTime timestamp) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.timestamp = timestamp;
        this.statusCode = status.value();
    }

    public ApiCustomException(HttpStatus status , Throwable ex){
        this.error = ex.getLocalizedMessage();
        this.message = "Unexpected Error";
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.statusCode = status.value();
    }

    public ApiCustomException(HttpStatus status, String message, Throwable ex){
        this.error = ex.getLocalizedMessage();
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.statusCode = status.value();
    }

    public ResponseEntity<Object> generateErrorResponseEntity(){
        return new ResponseEntity<>(this, this.status);
    }
}
