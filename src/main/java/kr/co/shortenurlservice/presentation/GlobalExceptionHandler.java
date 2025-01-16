package kr.co.shortenurlservice.presentation;

import kr.co.shortenurlservice.domain.LackOfShortenUrlKeyException;
import kr.co.shortenurlservice.domain.NotFoundShortenUrlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LackOfShortenUrlKeyException.class)
    public ResponseEntity<String> handleLackOfShortenUrlKeyException(
            LackOfShortenUrlKeyException ex
    ) {
        log.error("단축 URL 자원이 부족합니다.");
        return new ResponseEntity<>("단축 URL 자원이 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundShortenUrlException.class)
    public ResponseEntity<String> handleNotFoundShortenUrlException(
            NotFoundShortenUrlException ex
    ) {
        /**
         * log level 이 info 인지 error 인지는
         * 생각을 좀 해봐야 함
         * 시스템적인 이슈가 발생하면 ERROR LEVEL 적절하겠지만
         * 사용자의 실수로 인한 이슈이기 때문에 INFO LEVEL 이 적절할 것이라고 봄
         */
        log.info(ex.getMessage());
        return new ResponseEntity<>("단축 URL을 찾지 못했습니다.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // 유효성 검증 오류 세부 정보 추출
        StringBuilder errorMessage = new StringBuilder("유효성 검증 실패: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(String.format("필드 '%s': %s. ", error.getField(), error.getDefaultMessage()));
        });

        // 상세로그
        log.debug("잘못된 요청: {}", errorMessage);

        // 클라이언트에 응답
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }

}
