//package com.was.validator;
//
//import com.was.config.ServerConfig;
//import com.was.exception.AccessDeniedException;
//import com.was.exception.ExceptionCode;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//public class ConfigValidatorChain {
//    private final List<ConfigValidator> validators;
//
//    private ConfigValidatorChain(List<ConfigValidator> validators) {
//        this.validators = List.copyOf(validators);
//    }
//
//    public static class Builder {
//        private final List<ConfigValidator> list = new CopyOnWriteArrayList<>();
//
//        public ConfigValidatorChain.Builder add(ConfigValidator v) {
//            list.add(v);
//            return this;
//        }
//
//        public ConfigValidatorChain build() {
//            return new ConfigValidatorChain(list);
//        }
//    }
//
//
//    @Override
//    public void validate(ServerConfig serverConfig){
//        for (ConfigValidator v : validators) {
//            v.validate(serverConfig);    // 실패 시 예외 throw → 즉시 상위 catch
//        }
//    }
//
//    public static ConfigValidatorChain defaultChain() {
//        return new ConfigValidatorChain.Builder()
//                .add((config) -> {
//
//                })
//                .add(config -> {
//
//                })
//                // 규칙 추가 ...
//                .build();
//    }
//
//}
