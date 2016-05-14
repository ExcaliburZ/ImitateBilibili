package com.wings.zilizili;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.wings.zilizili.utils.Solution;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test1() {
        System.out.println(new Solution().addDigits(5));
    }

    public void testOkHttp() {
    }
}