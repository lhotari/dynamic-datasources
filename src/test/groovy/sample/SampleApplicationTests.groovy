/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import javax.sql.DataSource

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext


public class SampleApplicationTests {

    private static ConfigurableApplicationContext context

    @BeforeClass
    public static void start() throws Exception {
        Future<ConfigurableApplicationContext> future = Executors
                .newSingleThreadExecutor().submit(
                new Callable<ConfigurableApplicationContext>() {
                    @Override
                    public ConfigurableApplicationContext call() throws Exception {
                        return SpringApplication
                        .run(SampleApplication.class)
                    }
                })
        context = future.get(60, TimeUnit.SECONDS)
    }

    @AfterClass
    public static void stop() {
        if (context != null) {
            context.close()
        }
    }

    @Test
    void testDataSourceBeans() throws Exception {
        assert context.getBeansOfType(DataSource).size() == 5
    }
}
