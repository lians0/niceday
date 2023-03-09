package com.niceday.tools.log;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomAnnotationAspectTest {

    public static void main(String[] args) {
        CustomAnnotationAspectTest customAnnotationAspectTest = new CustomAnnotationAspectTest();
        customAnnotationAspectTest.test();
    }

    @NiceLog
    void test() {
        System.out.println("test");
    }
}