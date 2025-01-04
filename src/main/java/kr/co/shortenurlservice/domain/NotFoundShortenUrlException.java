package kr.co.shortenurlservice.domain;

public class NotFoundShortenUrlException extends RuntimeException {

    /**
     * 오류 메세지를 전달받는 생성자
     * @param message: 오류 메세지 (shortenUrlKey)
     */
    public NotFoundShortenUrlException(String message) {
        super(message);
    }

}
