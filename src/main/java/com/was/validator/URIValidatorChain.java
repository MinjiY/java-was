package com.was.validator;

import com.was.HttpRequest;
import com.was.exception.AccessDeniedException;
import com.was.exception.ExceptionCode;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class URIValidatorChain implements RequestValidator {
    private final List<RequestValidator> validators;

    private URIValidatorChain(List<RequestValidator> validators) {
        this.validators = List.copyOf(validators);
    }

    public static class Builder {
        private final List<RequestValidator> list = new CopyOnWriteArrayList<>();

        public Builder add(RequestValidator v) {
            list.add(v);
            return this;
        }

        public URIValidatorChain build() {
            return new URIValidatorChain(list);
        }
    }

    @Override
    public void validate(HttpRequest request, Path target){
        for (RequestValidator v : validators) {
            v.validate(request, target);    // 실패 시 예외 throw → 즉시 상위 catch
        }
    }


    public static URIValidatorChain defaultChain() { // 매개변수에 설정파일을 추가해서 규칙에대한 행동을 미리 정의
        return new Builder()
                .add((request, target) -> {          // .exe 차단
                    // 어떤 경로든 마지막의 fileName을 가져옴
                    String fileName = target.toString().substring(target.toString().lastIndexOf('/') + 1);
                    if (fileName.lastIndexOf('.') != -1 // 점이 있고
                            && "exe".equalsIgnoreCase(
                            fileName.substring(fileName.lastIndexOf('.') + 1))) { // exe
                        throw new AccessDeniedException(ExceptionCode.ACCESS_DENIED);
                    }
                })
                .add((request, target) -> {          // 상위 경로 탐색하는지 확인
                        // normalize한 경로랑 실제 요청한 uri 비교, 다르면 에러
                    if (!target.toString().equals(request.getUri())) {
                        throw new AccessDeniedException(ExceptionCode.ACCESS_DENIED);
                    }
                })
                // 규칙 추가 ...
                .build();
    }
}
