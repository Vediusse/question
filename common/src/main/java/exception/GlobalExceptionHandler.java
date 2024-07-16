package exception;

import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import response.ResponseError;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(CustomAuthenticationException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseError(ex.getMessage(), HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleAuthenticationException(NotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseError(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseError("Ошибка подписи JWT: " + ex.getMessage(), HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseError(ex.getMessage(), HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseError("Метод не разрешен: " + ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED), HttpStatus.METHOD_NOT_ALLOWED);
    }
}

