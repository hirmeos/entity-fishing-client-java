package com.scienceminer.nerd.client;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

public class NerdClientTest {

    NerdClient target;

    @Before
    public void setUp() throws Exception {

        target = new NerdClient();

    }

    @Test
    @Ignore
    public void test() throws Exception {

        target = new NerdClient("nerd.huma-num.fr/test/service");
        System.out.println(target.disambiguateText("We were walking to Washington and then we arrive to Milan, but nobody was there. ", null));
    }

}
