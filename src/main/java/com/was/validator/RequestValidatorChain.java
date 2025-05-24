package com.was.validator;

import com.was.HttpRequest;
import com.was.HttpResponse;
import com.was.exception.AccessDeniedException;
import com.was.exception.ExceptionCode;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RequestValidatorChain implements RequestValidator {
    private final List<RequestValidator> validators;

    private RequestValidatorChain(List<RequestValidator> validators) {
        this.validators = List.copyOf(validators);
    }

    public static class Builder {
        private final List<RequestValidator> list = new CopyOnWriteArrayList<>();

        public Builder add(RequestValidator v) {
            list.add(v);
            return this;
        }

        public RequestValidatorChain build() {
            return new RequestValidatorChain(list);
        }
    }

    @Override
    public void validate(HttpRequest request, HttpResponse response, Path target){
        for (RequestValidator v : validators) {
            v.validate(request, response, target);    // 실패 시 예외 throw → 즉시 상위 catch
        }
    }


    public static RequestValidatorChain defaultChain() { // 매개변수에 설정파일을 추가해서 규칙에대한 행동을 미리 정의
        return new Builder()
                .add((request, response, target) -> {          // .exe 차단
                    // 어떤 경로든 마지막의 fileName을 가져옴
                    try {
                        String fileName = target.toString().substring(target.toString().lastIndexOf('/') + 1);
                        if (fileName.lastIndexOf('.') != -1 // 점이 있고
                                && "exe".equalsIgnoreCase(
                                fileName.substring(fileName.lastIndexOf('.') + 1))) { // exe
                            throw new AccessDeniedException(ExceptionCode.ACCESS_DENIED, response);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                })
                .add((request, response, target) -> {          // 상위 경로 탐색하는지 확인
                    try {
                        // normalize한 경로랑 실제 요청한 uri 비교, 다르면 에러
                        if (!target.toString().equals(request.getUri())) {
                            throw new AccessDeniedException(ExceptionCode.ACCESS_DENIED, response);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                })
                // 규칙 추가 ...
                .build();
    }
}
