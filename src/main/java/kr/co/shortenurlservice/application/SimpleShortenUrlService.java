package kr.co.shortenurlservice.application;

import kr.co.shortenurlservice.domain.LackOfShortenUrlKeyException;
import kr.co.shortenurlservice.domain.NotFoundShortenUrlException;
import kr.co.shortenurlservice.domain.ShortenUrl;
import kr.co.shortenurlservice.domain.ShortenUrlRepository;
import kr.co.shortenurlservice.presentation.ShortenUrlCreateRequestDto;
import kr.co.shortenurlservice.presentation.ShortenUrlCreateResponseDto;
import kr.co.shortenurlservice.presentation.ShortenUrlInformationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SimpleShortenUrlService {

    private ShortenUrlRepository shortenUrlRepository;

    @Autowired
    SimpleShortenUrlService(ShortenUrlRepository shortenUrlRepository) {
        this.shortenUrlRepository = shortenUrlRepository;
    }

    public ShortenUrlCreateResponseDto generateShortenUrl(ShortenUrlCreateRequestDto shortenUrlCreateRequestDto) {
        String originalUrl = shortenUrlCreateRequestDto.getOriginalUrl();
        String shortenUrlKey = getUniqueShortenUrlKey();
        log.debug("getUniqueShortenUrlKey: {}", shortenUrlKey);

        ShortenUrl shortenUrl = new ShortenUrl(originalUrl, shortenUrlKey);
        shortenUrlRepository.saveShortenUrl(shortenUrl);
        log.info("shortenUrl 생성: {}", shortenUrl);

        return new ShortenUrlCreateResponseDto(shortenUrl);
    }

    public String getOriginalUrlByShortenUrlKey(String shortenUrlKey) {
        ShortenUrl shortenUrl = shortenUrlRepository.findShortenUrlByShortenUrlKey(shortenUrlKey);

        if (null == shortenUrl) {
            throw new NotFoundShortenUrlException("단축 URL을 생성하지 못했습니다. shortenUrlKey = " + shortenUrlKey);
        }

        shortenUrl.increaseRedirectCount();
        shortenUrlRepository.saveShortenUrl(shortenUrl);

        return shortenUrl.getOriginalUrl();
    }

    public ShortenUrlInformationDto getShortenUrlInformationByShortenUrlKey(String shortenUrlKey) {
        ShortenUrl shortenUrl = shortenUrlRepository.findShortenUrlByShortenUrlKey(shortenUrlKey);

        if (null == shortenUrl) {
            throw new NotFoundShortenUrlException("단축 URL을 생성하지 못했습니다. shortenUrlKey = " + shortenUrlKey);
        }

        return new ShortenUrlInformationDto(shortenUrl);
    }

    public List<ShortenUrlInformationDto> getAllShortenUrlInformationDto() {
        List<ShortenUrl> shortenUrls = shortenUrlRepository.findAll();

        return shortenUrls
                .stream()
                .map(ShortenUrlInformationDto::new)
                .toList();
    }

    private String getUniqueShortenUrlKey() {
        final int MAX_RETRY_COUNT = 5;
        int count = 0;

        while(count++ < MAX_RETRY_COUNT) {
            String shortenUrlKey = ShortenUrl.generateShortenUrlKey();
            ShortenUrl shortenUrl = shortenUrlRepository.findShortenUrlByShortenUrlKey(shortenUrlKey);

            if (null == shortenUrl) {
                return shortenUrlKey;
            }

            // 재시도를 하게 되는 곳
            log.warn("단축 URL 생성 재시도! 재시도 횟수 : {}", count + 1);
        }

        throw new LackOfShortenUrlKeyException();
    }

}
