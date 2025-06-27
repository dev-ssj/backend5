package org.example.backendproject.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect //공통으로 관리하고 싶은 기능을 담당하는 클래스에 붙이는 어노테이션
public class LogAspect {         
    //PointCut
    //AOP를 적용할 클래스
    @Pointcut("execution(* org.example.backendproject.board.service..*(..)) || execution(* org.example.backendproject.Auth.service..*(..))")
    public void method(){}

    //@Aroun는 호출 시작과 종료 모두에 관리할 수 있는 AOP Advice
   @Around("method()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName(); //aop가 실행된 메서드
       try{
           log.info("[AOP_LOG] {} 메서드 호출 시작 ", methodName);
           
           Object result = joinPoint.proceed(); //joinpoint //aop를 적용할 시점
           return result;
       }
       catch (Exception e){
           log.error("[AOP_LOG] {} 메서드 예외 {}  ", methodName, e.getMessage());
           return e;
       }finally {
           long end = System.currentTimeMillis();
           log.info("[AOP_LOG] {} 메서드 실행 완료 시간 = {} ", methodName, end - start);
       }
   }
}
