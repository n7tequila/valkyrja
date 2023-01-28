package org.valkyrja2.component.async;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

class AsyncStorageManagerTest {

    private static final Logger log = LoggerFactory.getLogger(AsyncStorageManagerTest.class);

    @Test
    void testAsyncStorageHandler() {
        TestAsyncStorageHandler handler = new TestAsyncStorageHandler();
        handler.handleStorageSave(new AsyncStorageBean());
    }


    private static class TestAsyncStorageHandler implements AsyncStorageHandler<AsyncStorageBean> {

        @Override
        public boolean handleStorageSave(Object obj) {
            String json = toJson(obj);
            log.info("handleStorageSave.toJson(), {}", json);

            try {
                AsyncStorageBean parseObj = parseJson(json);

                log.info("handleStorageSave.parseJson(), {}", parseObj);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

    }

    private static class AsyncStorageBean {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}