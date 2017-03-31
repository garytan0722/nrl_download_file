package com.download.nrl_download_file;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

    }
    public void test_dailog(){
    }
    @Override
    protected void setUp() throws Exception {
        createApplication();
    }

    @Override
    protected void runTest() throws Throwable {
        test_dailog();
    }
}