package cnd.trelloDuPauvre.perso.Exceptions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler   extends ResponseEntityExceptionHandler
{
    public CustomRestExceptionHandler() {
        super();
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, ex);
        return  customError.generateErrorResponseEntity();
    }

    /**
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
     *
     * @param ex      HttpMediaTypeNotSupportedException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        ApiCustomException customError = new ApiCustomException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex);
        return  customError.generateErrorResponseEntity();
    }


    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex);
        return  customError.generateErrorResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, ex);
        return  customError.generateErrorResponseEntity();
    }

    /**
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     *
     * @param ex      MissingServletRequestParameterException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getParameterName() + " parameter is missing";
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, message, ex);
        return  customError.generateErrorResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, ex);
        return  customError.generateErrorResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.INTERNAL_SERVER_ERROR, "test", ex);
        return  customError.generateErrorResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, ex);
        return  customError.generateErrorResponseEntity();
    }
    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex      HttpMessageNotReadableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = "Malformed JSON request";
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, message, ex);
        return  customError.generateErrorResponseEntity();
    }

    /**
     * Handle HttpMessageNotWritableException.
     *
     * @param ex      HttpMessageNotWritableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = "Error writing JSON output";
        ApiCustomException customError = new ApiCustomException(HttpStatus.INTERNAL_SERVER_ERROR, message, ex);
        return  customError.generateErrorResponseEntity();
    }

    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, "Validation error", ex);
        return  customError.generateErrorResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, ex);
        return  customError.generateErrorResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        return  customError.generateErrorResponseEntity();
    }

    /**
     * Handle NoHandlerFoundException.
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL());
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, message, ex);
        return  customError.generateErrorResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.REQUEST_TIMEOUT, ex);
        return  customError.generateErrorResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        return  customError.generateErrorResponseEntity();
    }

    /**
     * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
     *
     * @param ex the DataIntegrityViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                  WebRequest request) {
        if (ex.getCause() instanceof ConstraintViolationException) {
            ApiCustomException customError = new ApiCustomException(HttpStatus.CONFLICT, "Database error", ex.getCause());
            return customError.generateErrorResponseEntity();
        }
        ApiCustomException customError = new ApiCustomException(HttpStatus.INTERNAL_SERVER_ERROR , ex.getCause());
        return customError.generateErrorResponseEntity();
    }

    /**
     * Handle Exception, handle generic Exception.class
     *
     * @param ex the Exception
     * @return the ApiError object
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                      WebRequest request) {
        String message = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        ApiCustomException customError = new ApiCustomException(HttpStatus.BAD_REQUEST, message, ex);
        return  customError.generateErrorResponseEntity();
    }

    /**
     * Handles EntityNotFoundException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.NOT_FOUND, ex.getMessage() , ex);
        return  customError.generateErrorResponseEntity();
    }


    /**
     * Handle javax.persistence.EntityNotFoundException
     */
    @ExceptionHandler(javax.persistence.EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(javax.persistence.EntityNotFoundException ex) {
        ApiCustomException customError = new ApiCustomException(HttpStatus.NOT_FOUND,  ex);
        return  customError.generateErrorResponseEntity();

    }





}